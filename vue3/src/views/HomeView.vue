<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import {
  deleteComment,
  deleteNote,
  followUser,
  getChannels,
  getComments,
  getCurrentProfile,
  getFeed,
  getFollowingList,
  getNoteInteractionState,
  getNoteDetail,
  likeNote,
  publishComment,
  publishNote,
  searchNotes,
  searchUsers,
  unfollowUser,
  unlikeNote,
  uploadFile,
  type Channel,
  type CommentItem,
  type FeedNote,
  type FollowingUserItem,
  type NoteDetail,
  type SearchNoteItem,
  type SearchUserItem,
  type UserProfile,
} from '@/services/redbookApi'

const token = ref(localStorage.getItem('authToken') || '')
const channels = ref<Channel[]>([])
const selectedChannelId = ref<number | null>(1)
const feed = ref<FeedNote[]>([])
const comments = ref<CommentItem[]>([])
const activeNote = ref<FeedNote | null>(null)
const activeDetail = ref<NoteDetail | null>(null)
const currentProfile = ref<UserProfile | null>(null)
const currentUserId = ref<number | null>(null)
const searchMode = ref<'note' | 'user'>('note')
const searchKeyword = ref('')
const noteSearchResults = ref<FeedNote[]>([])
const userSearchResults = ref<SearchUserItem[]>([])
const followingIds = ref<Set<number>>(new Set())
const activeLiked = ref(false)
const activeFollowing = ref(false)

const loadingFeed = ref(false)
const loadingDetail = ref(false)
const loadingComments = ref(false)
const loadingProfile = ref(false)
const searching = ref(false)
const togglingLike = ref(false)
const togglingFollowId = ref<number | null>(null)
const publishing = ref(false)
const commenting = ref(false)
const deletingNote = ref(false)
const deletingCommentId = ref<number | null>(null)
const confirmDeleteNote = ref(false)

const errorMessage = ref('')
const successMessage = ref('')

const title = ref('')
const content = ref('')
const topicInput = ref('校园,生活')
const imageFile = ref<File | null>(null)
const imagePreview = ref('')
const imageUrlInput = ref('')
const commentText = ref('')

let feedbackTimer: number | null = null

const isLoggedIn = computed(() => Boolean(token.value))

const topicList = computed(() =>
  topicInput.value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean),
)

const imageSource = computed(() => imagePreview.value || imageUrlInput.value.trim())

const normalizedSearchKeyword = computed(() => searchKeyword.value.trim())

const isSearchingNotes = computed(() => Boolean(normalizedSearchKeyword.value) && searchMode.value === 'note')

const visibleFeed = computed(() => (isSearchingNotes.value ? noteSearchResults.value : feed.value))

const selectedChannelName = computed(() => {
  return channels.value.find((channel) => channel.id === selectedChannelId.value)?.name || '全部'
})

const detailImages = computed(() => {
  const images = activeDetail.value?.imgUris

  if (Array.isArray(images)) {
    return images.filter(Boolean)
  }

  if (typeof images === 'string') {
    return images
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
  }

  return activeNote.value?.cover ? [activeNote.value.cover] : []
})

const feedStats = computed(() => {
  const likeTotal = visibleFeed.value.reduce((total, note) => total + Number(note.likeTotal || 0), 0)
  return {
    noteTotal: visibleFeed.value.length,
    likeTotal,
  }
})

const activeCreatorId = computed(() => activeDetail.value?.creatorId || activeNote.value?.creatorId || null)

const canFollowActiveCreator = computed(() => {
  return Boolean(activeCreatorId.value && currentUserId.value && activeCreatorId.value !== currentUserId.value)
})

const getDisplayName = (nickname?: string) => nickname || '校园用户'

const toNumber = (value: string | number | undefined) => Number(value || 0)

const setFollowingIds = (users: FollowingUserItem[]) => {
  followingIds.value = new Set(users.map((user) => user.userId))
}

const isFollowing = (userId?: number) => {
  if (!userId) return false
  return followingIds.value.has(userId)
}

const mapSearchNote = (note: SearchNoteItem): FeedNote => ({
  id: note.noteId,
  title: note.title,
  type: 0,
  cover: note.cover,
  nickname: note.nickname,
  avatar: note.avatar,
  likeTotal: note.likeTotal,
})

const updateFeedLikeTotal = (noteId: string | number, delta: number) => {
  const update = (note: FeedNote) => {
    if (String(note.id) === String(noteId)) {
      note.likeTotal = String(Math.max(0, toNumber(note.likeTotal) + delta))
    }
  }

  feed.value.forEach(update)
  noteSearchResults.value.forEach(update)

  if (activeDetail.value && String(activeDetail.value.id) === String(noteId)) {
    activeDetail.value.likeTotal = String(Math.max(0, toNumber(activeDetail.value.likeTotal) + delta))
  }
}

const readError = (error: unknown, fallback: string) => {
  if (error instanceof Error) return error.message || fallback
  return fallback
}

const showMessage = (type: 'success' | 'error', message: string) => {
  if (feedbackTimer) {
    window.clearTimeout(feedbackTimer)
  }

  successMessage.value = type === 'success' ? message : ''
  errorMessage.value = type === 'error' ? message : ''

  feedbackTimer = window.setTimeout(() => {
    successMessage.value = ''
    errorMessage.value = ''
    feedbackTimer = null
  }, 3600)
}

const loadChannels = async () => {
  channels.value = await getChannels()

  if (!selectedChannelId.value && channels.value.length > 0) {
    selectedChannelId.value = channels.value[0].id
  }
}

const loadCurrentUser = async () => {
  if (!isLoggedIn.value) {
    currentProfile.value = null
    currentUserId.value = null
    setFollowingIds([])
    return
  }

  loadingProfile.value = true

  try {
    currentProfile.value = await getCurrentProfile()

    if (currentProfile.value?.redbookId) {
      const response = await searchUsers(currentProfile.value.redbookId, 1)
      currentUserId.value =
        response.data?.find((user) => user.redbookId === currentProfile.value?.redbookId)?.userId ||
        response.data?.[0]?.userId ||
        null
    }

    if (currentUserId.value) {
      const followingResponse = await getFollowingList(currentUserId.value, 1)
      setFollowingIds(followingResponse.data || [])
    }
  } catch (error) {
    showMessage('error', readError(error, '读取当前用户失败'))
  } finally {
    loadingProfile.value = false
  }
}

const loadFeed = async () => {
  loadingFeed.value = true

  try {
    const response = await getFeed(selectedChannelId.value, 1)
    feed.value = response.data || []
    if (!normalizedSearchKeyword.value) {
      noteSearchResults.value = []
    }
  } catch (error) {
    showMessage('error', readError(error, '信息流加载失败'))
  } finally {
    loadingFeed.value = false
  }
}

const runSearch = async () => {
  if (!normalizedSearchKeyword.value) {
    noteSearchResults.value = []
    userSearchResults.value = []
    await loadFeed()
    return
  }

  searching.value = true

  try {
    if (searchMode.value === 'note') {
      const response = await searchNotes(normalizedSearchKeyword.value, 1)
      noteSearchResults.value = (response.data || []).map(mapSearchNote)
      userSearchResults.value = []
    } else {
      const response = await searchUsers(normalizedSearchKeyword.value, 1)
      userSearchResults.value = response.data || []
      noteSearchResults.value = []
    }
  } catch (error) {
    showMessage('error', readError(error, '搜索失败'))
  } finally {
    searching.value = false
  }
}

const clearSearch = async () => {
  searchKeyword.value = ''
  noteSearchResults.value = []
  userSearchResults.value = []
  await loadFeed()
}

const selectChannel = async (channelId: number | null) => {
  selectedChannelId.value = channelId
  searchKeyword.value = ''
  noteSearchResults.value = []
  userSearchResults.value = []
  await loadFeed()
}

const onImageChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0] || null

  if (imagePreview.value) {
    URL.revokeObjectURL(imagePreview.value)
  }

  imageFile.value = file
  imagePreview.value = file ? URL.createObjectURL(file) : ''

  if (file) {
    imageUrlInput.value = ''
  }
}

const resetPublisher = () => {
  title.value = ''
  content.value = ''
  imageFile.value = null
  imageUrlInput.value = ''

  if (imagePreview.value) {
    URL.revokeObjectURL(imagePreview.value)
  }

  imagePreview.value = ''
}

const submitPost = async () => {
  if (!isLoggedIn.value) {
    showMessage('error', '请先登录')
    return
  }

  if (!title.value.trim()) {
    showMessage('error', '请填写标题')
    return
  }

  if (!imageFile.value && !imageUrlInput.value.trim()) {
    showMessage('error', '请选择图片或填写图片地址')
    return
  }

  publishing.value = true

  try {
    const imageUrl = imageFile.value ? await uploadFile(imageFile.value) : imageUrlInput.value.trim()

    await publishNote({
      type: 0,
      imgUris: [imageUrl],
      title: title.value.trim(),
      content: content.value.trim(),
      topics: topicList.value,
      channelId: selectedChannelId.value || channels.value[0]?.id || 1,
    })

    showMessage('success', '发布成功，信息流已刷新')
    resetPublisher()
    await loadFeed()
  } catch (error) {
    showMessage('error', readError(error, '发布失败'))
  } finally {
    publishing.value = false
  }
}

const loadCommentsForActiveNote = async () => {
  if (!activeNote.value) return

  loadingComments.value = true

  try {
    const response = await getComments(activeNote.value.id, 1)
    const remoteComments = response.data || []
    const pendingComments = comments.value.filter((comment) => comment.commentId < 0)
    const unresolvedPendingComments = pendingComments.filter((pendingComment) => {
      return !remoteComments.some((remoteComment) => remoteComment.content === pendingComment.content)
    })

    comments.value = [...unresolvedPendingComments, ...remoteComments]
  } catch (error) {
    showMessage('error', readError(error, '评论加载失败'))
  } finally {
    loadingComments.value = false
  }
}

const openNote = async (note: FeedNote) => {
  activeNote.value = note
  activeDetail.value = null
  comments.value = []
  commentText.value = ''
  confirmDeleteNote.value = false
  activeLiked.value = false
  activeFollowing.value = false
  loadingDetail.value = true

  try {
    activeDetail.value = await getNoteDetail(note.id)
    const state = await getNoteInteractionState(note.id)
    activeLiked.value = state.isLiked
    activeFollowing.value = isFollowing(activeDetail.value.creatorId)
  } catch (error) {
    showMessage('error', readError(error, '详情加载失败'))
  } finally {
    loadingDetail.value = false
  }

  await loadCommentsForActiveNote()
}

const closeNote = () => {
  activeNote.value = null
  activeDetail.value = null
  comments.value = []
  confirmDeleteNote.value = false
  activeLiked.value = false
  activeFollowing.value = false
}

const submitComment = async () => {
  const nextComment = commentText.value.trim()
  if (!activeNote.value || !nextComment) return

  commenting.value = true

  try {
    await publishComment(activeNote.value.id, nextComment)
    comments.value = [
      {
        commentId: -Date.now(),
        userId: 0,
        nickname: '我',
        content: nextComment,
        createTime: '刚刚',
      },
      ...comments.value,
    ]
    commentText.value = ''
    showMessage('success', '评论已发布')
    window.setTimeout(() => {
      void loadCommentsForActiveNote()
    }, 1200)
  } catch (error) {
    showMessage('error', readError(error, '评论失败'))
  } finally {
    commenting.value = false
  }
}

const toggleLike = async () => {
  if (!activeNote.value) return

  togglingLike.value = true

  try {
    if (activeLiked.value) {
      await unlikeNote(activeNote.value.id)
      activeLiked.value = false
      updateFeedLikeTotal(activeNote.value.id, -1)
      showMessage('success', '已取消点赞')
    } else {
      await likeNote(activeNote.value.id)
      activeLiked.value = true
      updateFeedLikeTotal(activeNote.value.id, 1)
      showMessage('success', '已点赞')
    }
  } catch (error) {
    showMessage('error', readError(error, activeLiked.value ? '取消点赞失败' : '点赞失败'))
  } finally {
    togglingLike.value = false
  }
}

const toggleFollow = async (userId: number) => {
  if (!currentUserId.value || currentUserId.value === userId) return

  togglingFollowId.value = userId

  try {
    if (isFollowing(userId)) {
      await unfollowUser(userId)
      const next = new Set(followingIds.value)
      next.delete(userId)
      followingIds.value = next
      activeFollowing.value = false
      showMessage('success', '已取消关注')
    } else {
      await followUser(userId)
      const next = new Set(followingIds.value)
      next.add(userId)
      followingIds.value = next
      activeFollowing.value = activeCreatorId.value === userId
      showMessage('success', '已关注')
    }
  } catch (error) {
    showMessage('error', readError(error, isFollowing(userId) ? '取消关注失败' : '关注失败'))
  } finally {
    togglingFollowId.value = null
  }
}

const removeActiveNote = async () => {
  if (!activeNote.value) return

  if (!confirmDeleteNote.value) {
    confirmDeleteNote.value = true
    showMessage('error', '再次点击确认删除帖子')
    return
  }

  deletingNote.value = true

  try {
    await deleteNote(activeNote.value.id)
    closeNote()
    showMessage('success', '帖子已删除')
    await loadFeed()
  } catch (error) {
    confirmDeleteNote.value = false
    showMessage('error', readError(error, '删除帖子失败'))
  } finally {
    deletingNote.value = false
  }
}

const removeComment = async (commentId: number) => {
  if (commentId < 0) {
    comments.value = comments.value.filter((comment) => comment.commentId !== commentId)
    return
  }

  deletingCommentId.value = commentId

  try {
    await deleteComment(commentId)
    showMessage('success', '评论已删除')
    await loadCommentsForActiveNote()
  } catch (error) {
    showMessage('error', readError(error, '删除评论失败'))
  } finally {
    deletingCommentId.value = null
  }
}

const syncTokenState = () => {
  token.value = localStorage.getItem('authToken') || ''
  void loadCurrentUser()
}

onMounted(async () => {
  window.addEventListener('auth-token-updated', syncTokenState)
  window.addEventListener('storage', syncTokenState)

  try {
    await loadChannels()
    await loadCurrentUser()
    await loadFeed()
  } catch (error) {
    showMessage('error', readError(error, '初始化失败'))
  }
})

onUnmounted(() => {
  window.removeEventListener('auth-token-updated', syncTokenState)
  window.removeEventListener('storage', syncTokenState)

  if (feedbackTimer) {
    window.clearTimeout(feedbackTimer)
  }

  if (imagePreview.value) {
    URL.revokeObjectURL(imagePreview.value)
  }
})
</script>

<template>
  <main class="redbook-home">
    <section class="home-head">
      <div>
        <p class="eyebrow">Campus Feed</p>
        <h1>校园发现</h1>
        <span>{{ selectedChannelName }}频道 · {{ isLoggedIn ? '登录态可用' : '未登录' }}</span>
      </div>

      <div class="head-stats" aria-label="当前信息流统计">
        <strong>{{ feedStats.noteTotal }}</strong>
        <span>篇内容</span>
        <strong>{{ feedStats.likeTotal }}</strong>
        <span>次点赞</span>
      </div>
    </section>

    <section class="workspace">
      <aside id="publish" class="publisher" data-testid="publish-form">
        <div class="panel-title">
          <div>
            <span>发布工具</span>
            <h2>新建图文</h2>
          </div>
          <strong>{{ selectedChannelName }}</strong>
        </div>

        <label class="field">
          <span>标题</span>
          <input v-model.trim="title" data-testid="post-title" type="text" placeholder="例如：图书馆晚霞" />
        </label>

        <label class="field">
          <span>正文</span>
          <textarea
            v-model.trim="content"
            data-testid="post-content"
            rows="5"
            placeholder="记录今天值得分享的一刻"
          />
        </label>

        <div class="field-row">
          <label class="field">
            <span>频道</span>
            <select v-model.number="selectedChannelId" data-testid="post-channel">
              <option v-for="channel in channels" :key="channel.id" :value="channel.id">
                {{ channel.name }}
              </option>
            </select>
          </label>

          <label class="field">
            <span>话题</span>
            <input v-model.trim="topicInput" data-testid="post-topics" type="text" placeholder="校园,生活" />
          </label>
        </div>

        <label class="upload-box" :class="{ filled: imageSource }">
          <input data-testid="post-file" type="file" accept="image/*" @change="onImageChange" />
          <img v-if="imageSource" :src="imageSource" alt="待发布图片预览" />
          <span v-else>选择图片</span>
        </label>

        <label class="field compact">
          <span>图片地址</span>
          <input v-model.trim="imageUrlInput" data-testid="post-image-url" type="url" placeholder="https://..." />
        </label>

        <div class="topic-preview" aria-label="话题预览">
          <span v-for="topic in topicList" :key="topic">#{{ topic }}</span>
        </div>

        <button class="primary-action" type="button" :disabled="publishing" @click="submitPost">
          {{ publishing ? '发布中...' : '发布图文' }}
        </button>
      </aside>

      <section class="feed-surface">
        <div class="feed-toolbar">
          <div>
            <p class="eyebrow">Discover</p>
            <h2>{{ normalizedSearchKeyword ? '搜索结果' : '最新动态' }}</h2>
          </div>

          <button type="button" class="ghost-action" :disabled="loadingFeed" @click="loadFeed">
            {{ loadingFeed ? '刷新中...' : '刷新' }}
          </button>
        </div>

        <div class="search-bar" role="search">
          <div class="segmented-control" aria-label="搜索类型">
            <button type="button" :class="{ active: searchMode === 'note' }" @click="searchMode = 'note'">笔记</button>
            <button type="button" :class="{ active: searchMode === 'user' }" @click="searchMode = 'user'">用户</button>
          </div>
          <input
            v-model.trim="searchKeyword"
            data-testid="search-input"
            type="search"
            placeholder="搜索标题、内容或小红书号"
            @keyup.enter="runSearch"
          />
          <button type="button" class="primary-action compact" :disabled="searching" @click="runSearch">
            {{ searching ? '搜索中...' : '搜索' }}
          </button>
          <button v-if="normalizedSearchKeyword" type="button" class="ghost-action compact" @click="clearSearch">清空</button>
        </div>

        <div class="channel-tabs" role="tablist" aria-label="频道">
          <button
            v-for="channel in channels"
            :key="channel.id"
            type="button"
            :class="{ active: selectedChannelId === channel.id }"
            @click="selectChannel(channel.id)"
          >
            {{ channel.name }}
          </button>
        </div>

        <p v-if="errorMessage" class="message error">{{ errorMessage }}</p>
        <p v-else-if="successMessage" class="message success">{{ successMessage }}</p>

        <div v-if="searchMode === 'user' && normalizedSearchKeyword" class="user-results" data-testid="user-results">
          <div v-if="searching" class="empty-state">搜索中...</div>
          <div v-else-if="userSearchResults.length === 0" class="empty-state">没有找到相关用户</div>
          <article v-for="user in userSearchResults" v-else :key="user.userId" class="user-result">
            <span class="avatar">{{ getDisplayName(user.nickname).slice(0, 1) }}</span>
            <div>
              <strong>{{ getDisplayName(user.nickname) }}</strong>
              <span>小红书号 {{ user.redbookId || user.userId }} · {{ user.noteTotal || 0 }} 篇笔记</span>
            </div>
            <button
              type="button"
              class="ghost-action compact"
              :disabled="loadingProfile || !currentUserId || currentUserId === user.userId || togglingFollowId === user.userId"
              @click="toggleFollow(user.userId)"
            >
              {{ currentUserId === user.userId ? '自己' : isFollowing(user.userId) ? '已关注' : '关注' }}
            </button>
          </article>
        </div>

        <div v-else-if="loadingFeed || searching" class="empty-state">加载中...</div>

        <div v-else-if="visibleFeed.length === 0" class="empty-state">
          {{ normalizedSearchKeyword ? '没有找到相关笔记' : '当前频道暂无帖子' }}
        </div>

        <div v-else class="feed-grid" data-testid="feed-grid">
          <article
            v-for="note in visibleFeed"
            :key="note.id"
            class="note-card"
            data-testid="note-card"
            @click="openNote(note)"
          >
            <div class="cover">
              <img v-if="note.cover" :src="note.cover" :alt="note.title" />
              <span v-else>无封面</span>
            </div>
            <div class="note-meta">
              <h3>{{ note.title }}</h3>
              <div class="author-row">
                <span class="avatar">{{ getDisplayName(note.nickname).slice(0, 1) }}</span>
                <span>{{ getDisplayName(note.nickname) }}</span>
                <strong>{{ note.likeTotal || 0 }} 赞</strong>
              </div>
            </div>
          </article>
        </div>
      </section>
    </section>

    <section v-if="activeNote" class="detail-backdrop" @click.self="closeNote">
      <article class="detail-panel" data-testid="detail-panel">
        <div class="detail-media">
          <div v-if="loadingDetail" class="media-loading">详情加载中...</div>
          <template v-else>
            <img v-for="image in detailImages" :key="image" :src="image" :alt="activeDetail?.title || activeNote.title" />
            <div v-if="detailImages.length === 0" class="media-loading">暂无图片</div>
          </template>
        </div>

        <div class="detail-side">
          <div class="detail-actions">
            <button
              type="button"
              class="social-action"
              :class="{ active: activeLiked }"
              :disabled="togglingLike"
              @click="toggleLike"
            >
              {{ activeLiked ? '已点赞' : '点赞' }}
            </button>
            <button type="button" class="danger-action" :disabled="deletingNote" @click="removeActiveNote">
              {{ confirmDeleteNote ? '确认删除' : '删除' }}
            </button>
            <button type="button" class="close-action" aria-label="关闭详情" @click="closeNote">×</button>
          </div>

          <template v-if="activeDetail">
            <div class="detail-content">
              <div class="author-block">
                <div>
                  <span class="author-name">{{ getDisplayName(activeDetail.nickname) }}</span>
                  <small v-if="activeCreatorId">作者 ID {{ activeCreatorId }}</small>
                </div>
                <button
                  v-if="canFollowActiveCreator"
                  type="button"
                  class="ghost-action compact"
                  :disabled="loadingProfile || togglingFollowId === activeCreatorId"
                  @click="activeCreatorId && toggleFollow(activeCreatorId)"
                >
                  {{ activeFollowing || isFollowing(activeCreatorId || undefined) ? '已关注' : '关注' }}
                </button>
              </div>
              <h2>{{ activeDetail.title }}</h2>
              <p>{{ activeDetail.content || '暂无正文' }}</p>
              <div class="detail-counts">
                <strong>{{ activeDetail.likeTotal || 0 }} 赞</strong>
                <strong>{{ activeDetail.commentTotal || comments.length }} 评论</strong>
                <strong>{{ activeDetail.collectTotal || 0 }} 收藏</strong>
              </div>
            </div>
          </template>

          <div v-else-if="loadingDetail" class="empty-state">正在读取帖子详情...</div>

          <div class="comment-composer">
            <textarea
              v-model.trim="commentText"
              data-testid="comment-input"
              rows="3"
              placeholder="写下你的评论"
            />
            <button
              type="button"
              data-testid="comment-submit"
              :disabled="commenting || !commentText"
              @click="submitComment"
            >
              {{ commenting ? '发送中...' : '发表评论' }}
            </button>
          </div>

          <div class="comment-list" data-testid="comment-list">
            <div class="comment-head">
              <strong>评论</strong>
              <span>{{ comments.length }}</span>
            </div>

            <div v-if="loadingComments" class="empty-state">评论加载中...</div>
            <div v-else-if="comments.length === 0" class="empty-state">还没有评论</div>
            <article v-for="comment in comments" v-else :key="comment.commentId" class="comment-item">
              <div>
                <span class="avatar small">{{ getDisplayName(comment.nickname).slice(0, 1) }}</span>
                <strong>{{ getDisplayName(comment.nickname) }}</strong>
                <button
                  type="button"
                  class="text-action"
                  :disabled="deletingCommentId === comment.commentId"
                  @click="removeComment(comment.commentId)"
                >
                  {{ deletingCommentId === comment.commentId ? '删除中' : '删除' }}
                </button>
              </div>
              <p>{{ comment.content }}</p>
              <span>{{ comment.createTime || '刚刚' }}</span>
            </article>
          </div>
        </div>
      </article>
    </section>
  </main>
</template>

<style scoped>
.redbook-home {
  padding-bottom: 58px;
}

.home-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 22px;
  padding: 22px 0 8px;
}

.eyebrow {
  color: var(--color-accent);
  font-size: 0.76rem;
  font-weight: 900;
  line-height: 1.2;
  text-transform: uppercase;
}

.home-head h1 {
  margin-top: 2px;
  color: var(--color-heading);
  font-size: 2.35rem;
  font-weight: 950;
  line-height: 1.08;
}

.home-head span {
  display: inline-block;
  margin-top: 8px;
  color: var(--color-muted);
  font-weight: 700;
}

.head-stats {
  display: grid;
  grid-template-columns: auto auto auto auto;
  align-items: baseline;
  gap: 8px;
  min-height: 54px;
  border: 1px solid rgba(19, 133, 117, 0.18);
  border-radius: 8px;
  background: var(--color-surface);
  color: var(--color-muted);
  padding: 10px 16px;
}

.head-stats strong {
  color: var(--color-primary);
  font-size: 1.28rem;
  font-weight: 950;
}

.workspace {
  display: grid;
  grid-template-columns: minmax(300px, 376px) 1fr;
  gap: 22px;
  align-items: start;
}

.publisher,
.feed-surface {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: var(--shadow-card);
}

.publisher {
  position: sticky;
  top: 18px;
  display: grid;
  gap: 15px;
  padding: 20px;
}

.panel-title,
.feed-toolbar,
.detail-actions,
.comment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.panel-title span {
  color: var(--color-muted);
  font-size: 0.82rem;
  font-weight: 800;
}

.panel-title h2,
.feed-toolbar h2 {
  color: var(--color-heading);
  font-size: 1.4rem;
  font-weight: 950;
  line-height: 1.18;
}

.panel-title strong {
  border-radius: 999px;
  background: var(--color-accent-soft);
  color: var(--color-accent);
  font-size: 0.82rem;
  font-weight: 900;
  padding: 6px 10px;
}

.field,
.field-row {
  display: grid;
  gap: 7px;
}

.field-row {
  grid-template-columns: minmax(104px, 0.8fr) 1.2fr;
  gap: 10px;
}

.field span {
  color: #526174;
  font-size: 0.9rem;
  font-weight: 800;
}

.field.compact {
  gap: 6px;
}

input,
textarea,
select {
  width: 100%;
  border: 1px solid var(--color-border-strong);
  border-radius: 6px;
  background: #ffffff;
  color: var(--color-heading);
  outline: 0;
  padding: 10px 12px;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease;
}

select {
  min-height: 43px;
}

textarea {
  resize: vertical;
}

input:focus,
textarea:focus,
select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 63, 82, 0.13);
}

.upload-box {
  position: relative;
  display: grid;
  min-height: 216px;
  place-items: center;
  overflow: hidden;
  border: 1px dashed var(--color-border-strong);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(226, 244, 240, 0.7), rgba(253, 232, 235, 0.65)),
    #f8fbfc;
  cursor: pointer;
}

.upload-box.filled {
  border-style: solid;
}

.upload-box input {
  display: none;
}

.upload-box span {
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  color: var(--color-heading);
  font-weight: 900;
  padding: 10px 16px;
}

.upload-box img {
  width: 100%;
  height: 244px;
  object-fit: cover;
}

.topic-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.topic-preview span {
  border-radius: 999px;
  background: var(--color-surface-muted);
  color: var(--color-muted);
  font-size: 0.82rem;
  font-weight: 800;
  padding: 4px 9px;
}

button {
  min-height: 42px;
  border: 0;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 900;
  transition:
    background-color 0.18s ease,
    border-color 0.18s ease,
    color 0.18s ease,
    transform 0.18s ease;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.58;
}

.primary-action {
  background: var(--color-primary);
  color: #ffffff;
}

.primary-action.compact,
.ghost-action.compact {
  min-height: 40px;
  padding: 0 14px;
}

.primary-action:hover:not(:disabled),
.comment-composer button:hover:not(:disabled) {
  transform: translateY(-1px);
}

.ghost-action {
  border: 1px solid var(--color-border-strong);
  background: #ffffff;
  color: var(--color-heading);
  padding: 0 14px;
}

.ghost-action:hover:not(:disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary-strong);
}

.feed-surface {
  min-height: 680px;
  padding: 22px;
}

.feed-toolbar {
  margin-bottom: 18px;
}

.search-bar {
  display: grid;
  grid-template-columns: auto minmax(180px, 1fr) auto auto;
  gap: 10px;
  align-items: center;
  margin-bottom: 16px;
}

.segmented-control {
  display: inline-grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px;
  min-width: 126px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-surface-muted);
  padding: 4px;
}

.segmented-control button {
  min-height: 32px;
  background: transparent;
  color: var(--color-muted);
  padding: 0 10px;
}

.segmented-control button.active {
  background: #ffffff;
  color: var(--color-primary-strong);
  box-shadow: 0 2px 10px rgba(23, 32, 43, 0.08);
}

.channel-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 18px;
}

.channel-tabs button {
  min-height: 38px;
  background: var(--color-surface-muted);
  color: var(--color-muted);
  padding: 0 14px;
}

.channel-tabs button.active {
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
}

.message,
.empty-state {
  border-radius: 8px;
  margin-bottom: 16px;
  padding: 14px;
}

.message.error {
  border: 1px solid rgba(223, 63, 82, 0.18);
  background: #fff3f4;
  color: var(--color-primary-strong);
}

.message.success {
  border: 1px solid rgba(19, 133, 117, 0.18);
  background: var(--color-accent-soft);
  color: var(--color-accent);
}

.empty-state,
.media-loading {
  display: grid;
  min-height: 74px;
  place-items: center;
  border: 1px dashed var(--color-border);
  border-radius: 8px;
  background: #f9fbfc;
  color: var(--color-muted);
  font-weight: 800;
}

.feed-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(206px, 1fr));
  gap: 16px;
}

.user-results {
  display: grid;
  gap: 10px;
}

.user-result {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: #ffffff;
  padding: 12px;
}

.user-result strong {
  display: block;
  color: var(--color-heading);
  font-weight: 950;
}

.user-result span {
  color: var(--color-muted);
  font-size: 0.86rem;
  font-weight: 700;
}

.note-card {
  overflow: hidden;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(23, 32, 43, 0.04);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.note-card:hover {
  border-color: rgba(223, 63, 82, 0.42);
  box-shadow: var(--shadow-card);
  transform: translateY(-2px);
}

.cover {
  display: grid;
  aspect-ratio: 4 / 5;
  place-items: center;
  background: #eaf0f4;
  color: var(--color-muted);
  font-weight: 900;
}

.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-meta {
  display: grid;
  gap: 12px;
  padding: 13px;
}

.note-meta h3 {
  min-height: 44px;
  color: var(--color-heading);
  font-size: 1rem;
  font-weight: 900;
  line-height: 1.36;
}

.author-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 8px;
  color: var(--color-muted);
  font-size: 0.88rem;
  font-weight: 700;
}

.author-row strong {
  color: var(--color-primary);
  font-weight: 900;
}

.avatar {
  display: grid;
  width: 28px;
  height: 28px;
  place-items: center;
  border-radius: 999px;
  background: var(--color-accent-soft);
  color: var(--color-accent);
  font-size: 0.82rem;
  font-weight: 950;
}

.avatar.small {
  width: 24px;
  height: 24px;
  font-size: 0.76rem;
}

.detail-backdrop {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(17, 24, 39, 0.46);
  padding: 22px;
  z-index: 30;
}

.detail-panel {
  display: grid;
  grid-template-columns: minmax(320px, 1.12fr) minmax(360px, 0.88fr);
  width: min(1120px, 100%);
  max-height: min(92vh, 860px);
  overflow: hidden;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 24px 80px rgba(8, 13, 20, 0.28);
}

.detail-media {
  display: grid;
  align-content: center;
  gap: 12px;
  min-height: 560px;
  overflow: auto;
  background: #111827;
  padding: 18px;
}

.detail-media img {
  width: 100%;
  max-height: 78vh;
  border-radius: 8px;
  object-fit: contain;
  background: #0f172a;
}

.detail-side {
  display: grid;
  grid-template-rows: auto auto auto 1fr;
  gap: 18px;
  min-height: 560px;
  overflow: auto;
  padding: 20px;
}

.close-action {
  display: grid;
  width: 38px;
  height: 38px;
  min-height: 38px;
  place-items: center;
  background: var(--color-surface-muted);
  color: var(--color-heading);
  font-size: 1.35rem;
}

.danger-action {
  min-height: 38px;
  background: #fff3f4;
  color: var(--color-primary-strong);
  padding: 0 12px;
}

.social-action {
  min-height: 38px;
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
  padding: 0 14px;
}

.social-action.active {
  background: var(--color-primary);
  color: #ffffff;
}

.detail-content {
  display: grid;
  gap: 10px;
}

.author-block {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.author-block small {
  display: block;
  margin-top: 3px;
  color: var(--color-muted);
  font-size: 0.78rem;
  font-weight: 800;
}

.author-name {
  color: var(--color-accent);
  font-weight: 900;
}

.detail-content h2 {
  color: var(--color-heading);
  font-size: 1.54rem;
  font-weight: 950;
  line-height: 1.28;
}

.detail-content p,
.comment-item p {
  color: #334155;
  line-height: 1.75;
  white-space: pre-wrap;
}

.detail-counts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.detail-counts strong {
  border-radius: 999px;
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
  font-size: 0.84rem;
  font-weight: 900;
  padding: 5px 10px;
}

.comment-composer {
  display: grid;
  gap: 10px;
}

.comment-composer button {
  justify-self: end;
  background: var(--color-accent);
  color: #ffffff;
  padding: 0 18px;
}

.comment-list {
  display: grid;
  align-content: start;
  gap: 12px;
}

.comment-head {
  padding-bottom: 4px;
}

.comment-head strong {
  color: var(--color-heading);
  font-size: 1rem;
  font-weight: 950;
}

.comment-head span {
  border-radius: 999px;
  background: var(--color-surface-muted);
  color: var(--color-muted);
  font-size: 0.82rem;
  font-weight: 900;
  padding: 3px 9px;
}

.comment-item {
  display: grid;
  gap: 7px;
  border-top: 1px solid #edf1f5;
  padding-top: 12px;
}

.comment-item > div {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 8px;
}

.comment-item strong {
  color: var(--color-heading);
  font-weight: 900;
}

.comment-item > span {
  color: var(--color-muted);
  font-size: 0.82rem;
}

.text-action {
  min-height: 30px;
  background: transparent;
  color: var(--color-muted);
  font-size: 0.82rem;
  padding: 0 6px;
}

.text-action:hover:not(:disabled) {
  color: var(--color-primary-strong);
}

@media (max-width: 980px) {
  .home-head {
    align-items: start;
    flex-direction: column;
  }

  .workspace {
    grid-template-columns: 1fr;
  }

  .publisher {
    position: static;
  }

  .detail-panel {
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .detail-media,
  .detail-side {
    min-height: unset;
  }

  .detail-media {
    max-height: 48vh;
  }
}

@media (max-width: 620px) {
  .redbook-home {
    padding-bottom: 36px;
  }

  .home-head h1 {
    font-size: 2rem;
  }

  .head-stats,
  .field-row {
    grid-template-columns: 1fr 1fr;
    width: 100%;
  }

  .feed-surface,
  .publisher,
  .detail-side {
    padding: 16px;
  }

  .feed-toolbar {
    align-items: start;
    flex-direction: column;
  }

  .search-bar {
    grid-template-columns: 1fr;
  }

  .user-result {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .user-result button {
    grid-column: 1 / -1;
  }

  .detail-backdrop {
    padding: 10px;
  }

  .detail-panel {
    max-height: 94vh;
  }
}
</style>
