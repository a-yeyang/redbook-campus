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
  getNoteDetail,
  getNoteInteractionState,
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
const selectedChannelId = ref<number | null>(null)
const activeTopic = ref('推荐')
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
const composerOpen = ref(false)

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
const topicInput = ref('AI科研,顶会论文,论文速读')
const imageFile = ref<File | null>(null)
const imagePreview = ref('')
const imageUrlInput = ref('')
const commentText = ref('')

let feedbackTimer: number | null = null

const editorialTopics = ['推荐', '论文速读', '多模态', 'Agent', '具身智能', 'AI4Science', '工程复现', '科研日常', '工具流', '数据集']

const isLoggedIn = computed(() => Boolean(token.value))
const normalizedSearchKeyword = computed(() => searchKeyword.value.trim())
const isSearchingNotes = computed(() => Boolean(normalizedSearchKeyword.value) && searchMode.value === 'note')
const visibleFeed = computed(() => (isSearchingNotes.value ? noteSearchResults.value : feed.value))
const currentName = computed(() => currentProfile.value?.nickname || '科研同学')
const currentInitial = computed(() => currentName.value.slice(0, 1))
const activeCreatorId = computed(() => activeDetail.value?.creatorId || activeNote.value?.creatorId || null)
const canFollowActiveCreator = computed(() =>
  Boolean(activeCreatorId.value && currentUserId.value && activeCreatorId.value !== currentUserId.value),
)

const topicList = computed(() =>
  topicInput.value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean),
)

const imageSource = computed(() => imagePreview.value || imageUrlInput.value.trim())

const detailImages = computed(() => {
  const images = activeDetail.value?.imgUris

  if (Array.isArray(images)) return images.filter(Boolean)
  if (typeof images === 'string') {
    return images
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
  }

  return activeNote.value?.cover ? [activeNote.value.cover] : []
})

const channelTabs = computed(() => [
  { id: null as number | null, name: '推荐' },
  ...channels.value.map((channel) => ({
    id: channel.id,
    name:
      channel.name === 'campus'
        ? '校园'
        : channel.name === 'study'
          ? '学习'
          : channel.name === 'life'
            ? '生活'
            : channel.name,
  })),
])

const feedStats = computed(() => ({
  noteTotal: visibleFeed.value.length,
  likeTotal: visibleFeed.value.reduce((total, note) => total + Number(note.likeTotal || 0), 0),
}))

const getDisplayName = (nickname?: string) => nickname || '小红薯'
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
  if (feedbackTimer) window.clearTimeout(feedbackTimer)

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
    const pages = await Promise.all([1, 2, 3, 4, 5].map((pageNo) => getFeed(selectedChannelId.value, pageNo)))
    feed.value = pages.flatMap((page) => page.data || [])
    if (!normalizedSearchKeyword.value) noteSearchResults.value = []
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

const selectChannel = async (channelId: number | null, label: string) => {
  selectedChannelId.value = channelId
  activeTopic.value = label
  searchKeyword.value = ''
  noteSearchResults.value = []
  userSearchResults.value = []
  await loadFeed()
}

const selectEditorialTopic = async (topic: string) => {
  activeTopic.value = topic
  selectedChannelId.value = null
  searchKeyword.value = ''
  noteSearchResults.value = []
  userSearchResults.value = []
  await loadFeed()
}

const openComposer = () => {
  if (!isLoggedIn.value) {
    showMessage('error', '请先登录')
    return
  }

  composerOpen.value = true
}

const closeComposer = () => {
  if (!publishing.value) composerOpen.value = false
}

const handleHashIntent = () => {
  if (window.location.hash === '#publish') {
    openComposer()
    history.replaceState(null, '', `${window.location.pathname}${window.location.search}`)
  }
}

const onImageChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0] || null

  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value)
  imageFile.value = file
  imagePreview.value = file ? URL.createObjectURL(file) : ''

  if (file) imageUrlInput.value = ''
}

const resetPublisher = () => {
  title.value = ''
  content.value = ''
  imageFile.value = null
  imageUrlInput.value = ''

  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value)
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
    composerOpen.value = false
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
  window.addEventListener('hashchange', handleHashIntent)

  try {
    await loadChannels()
    await loadCurrentUser()
    await loadFeed()
    handleHashIntent()
  } catch (error) {
    showMessage('error', readError(error, '初始化失败'))
  }
})

onUnmounted(() => {
  window.removeEventListener('auth-token-updated', syncTokenState)
  window.removeEventListener('storage', syncTokenState)
  window.removeEventListener('hashchange', handleHashIntent)

  if (feedbackTimer) window.clearTimeout(feedbackTimer)
  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value)
})
</script>

<template>
  <section class="discover-page">
    <header class="discover-header">
      <form class="search-bar" @submit.prevent="runSearch">
        <div class="search-tabs">
          <button type="button" :class="{ active: searchMode === 'note' }" @click="searchMode = 'note'">笔记</button>
          <button type="button" :class="{ active: searchMode === 'user' }" @click="searchMode = 'user'">用户</button>
        </div>
        <input v-model.trim="searchKeyword" type="search" placeholder="搜索探索更多内容" />
        <button type="submit" aria-label="搜索">⌕</button>
        <button v-if="normalizedSearchKeyword" type="button" class="clear-button" @click="clearSearch">清空</button>
      </form>

      <div class="header-links">
        <a href="/#creator">创作中心</a>
        <a href="/#business">业务合作</a>
        <button type="button" class="publish-button" @click="openComposer">发布</button>
      </div>
    </header>

    <nav class="channel-tabs" aria-label="频道">
      <button
        v-for="tab in channelTabs"
        :key="tab.name"
        type="button"
        :class="{ active: selectedChannelId === tab.id && activeTopic === tab.name }"
        @click="selectChannel(tab.id, tab.name)"
      >
        {{ tab.name }}
      </button>
      <button
        v-for="topic in editorialTopics.slice(1)"
        :key="topic"
        type="button"
        :class="{ active: activeTopic === topic }"
        @click="selectEditorialTopic(topic)"
      >
        {{ topic }}
      </button>
    </nav>

    <p v-if="errorMessage" class="toast error">{{ errorMessage }}</p>
    <p v-else-if="successMessage" class="toast success">{{ successMessage }}</p>

    <section v-if="searchMode === 'user' && normalizedSearchKeyword" class="user-results">
      <div v-if="searching" class="empty-state">搜索中...</div>
      <div v-else-if="userSearchResults.length === 0" class="empty-state">没有找到相关用户</div>
      <article v-for="user in userSearchResults" v-else :key="user.userId" class="user-card">
        <span class="avatar">{{ getDisplayName(user.nickname).slice(0, 1) }}</span>
        <div>
          <strong>{{ getDisplayName(user.nickname) }}</strong>
          <p>小红书号 {{ user.redbookId || user.userId }} · {{ user.noteTotal || 0 }} 篇笔记</p>
        </div>
        <button
          type="button"
          :disabled="loadingProfile || !currentUserId || currentUserId === user.userId || togglingFollowId === user.userId"
          @click="toggleFollow(user.userId)"
        >
          {{ currentUserId === user.userId ? '自己' : isFollowing(user.userId) ? '已关注' : '关注' }}
        </button>
      </article>
    </section>

    <section v-else class="feed-wrap">
      <div v-if="loadingFeed" class="empty-state">正在加载科研笔记...</div>
      <div v-else-if="visibleFeed.length === 0" class="empty-state">当前频道暂无帖子</div>
      <div v-else class="masonry-feed">
        <article v-for="note in visibleFeed" :key="note.id" class="note-card" @click="openNote(note)">
          <div class="cover">
            <img v-if="note.cover" :src="note.cover" :alt="note.title" />
            <span v-else>AI NOTE</span>
          </div>
          <div class="note-body">
            <h2>{{ note.title }}</h2>
            <div class="note-footer">
              <span class="avatar small">{{ getDisplayName(note.nickname).slice(0, 1) }}</span>
              <span>{{ getDisplayName(note.nickname) }}</span>
              <strong>♡ {{ note.likeTotal || 0 }}</strong>
            </div>
          </div>
        </article>
      </div>
    </section>

    <div class="floating-stats">
      <span>{{ feedStats.noteTotal }} 篇</span>
      <span>{{ feedStats.likeTotal }} 赞</span>
    </div>
  </section>

  <section v-if="composerOpen" class="modal-backdrop" @click.self="closeComposer">
    <form class="composer-panel" @submit.prevent="submitPost">
      <div class="panel-head">
        <div>
          <span>NEW NOTE</span>
          <h2>发布图文</h2>
        </div>
        <button type="button" class="icon-button" aria-label="关闭发布弹层" @click="closeComposer">×</button>
      </div>

      <label class="field">
        <span>标题</span>
        <input v-model.trim="title" type="text" placeholder="比如：这篇 NeurIPS poster 真的讲透了 Agent 记忆" />
      </label>

      <label class="field">
        <span>正文</span>
        <textarea v-model.trim="content" rows="6" placeholder="写一点方法直觉、实验亮点和复现心得" />
      </label>

      <div class="field-grid">
        <label class="field">
          <span>频道</span>
          <select v-model.number="selectedChannelId">
            <option v-for="channel in channels" :key="channel.id" :value="channel.id">{{ channel.name }}</option>
          </select>
        </label>

        <label class="field">
          <span>话题</span>
          <input v-model.trim="topicInput" type="text" placeholder="AI科研,顶会论文" />
        </label>
      </div>

      <label class="upload-box" :class="{ filled: imageSource }">
        <input type="file" accept="image/*" @change="onImageChange" />
        <img v-if="imageSource" :src="imageSource" alt="待发布图片预览" />
        <span v-else>选择图片</span>
      </label>

      <label class="field">
        <span>图片地址</span>
        <input v-model.trim="imageUrlInput" type="url" placeholder="https://..." />
      </label>

      <div class="topic-preview">
        <span v-for="topic in topicList" :key="topic">#{{ topic }}</span>
      </div>

      <button class="primary-submit" type="submit" :disabled="publishing">
        {{ publishing ? '发布中...' : '发布图文' }}
      </button>
    </form>
  </section>

  <section v-if="activeNote" class="modal-backdrop detail-backdrop" @click.self="closeNote">
    <article class="detail-panel">
      <div class="detail-media">
        <div v-if="loadingDetail" class="media-loading">详情加载中...</div>
        <template v-else>
          <img v-for="image in detailImages" :key="image" :src="image" :alt="activeDetail?.title || activeNote.title" />
          <div v-if="detailImages.length === 0" class="media-loading">暂无图片</div>
        </template>
      </div>

      <div class="detail-side">
        <button type="button" class="icon-button close-detail" aria-label="关闭详情" @click="closeNote">×</button>

        <template v-if="activeDetail">
          <div class="author-block">
            <span class="avatar">{{ getDisplayName(activeDetail.nickname).slice(0, 1) }}</span>
            <div>
              <strong>{{ getDisplayName(activeDetail.nickname) }}</strong>
              <p v-if="activeCreatorId">作者 ID {{ activeCreatorId }}</p>
            </div>
            <button
              v-if="canFollowActiveCreator"
              type="button"
              class="follow-button"
              :disabled="loadingProfile || togglingFollowId === activeCreatorId"
              @click="activeCreatorId && toggleFollow(activeCreatorId)"
            >
              {{ activeFollowing || isFollowing(activeCreatorId || undefined) ? '已关注' : '关注' }}
            </button>
          </div>

          <h1>{{ activeDetail.title }}</h1>
          <p class="detail-content">{{ activeDetail.content || '暂无正文' }}</p>

          <div class="detail-counts">
            <span>{{ activeDetail.likeTotal || 0 }} 赞</span>
            <span>{{ activeDetail.commentTotal || comments.length }} 评论</span>
            <span>{{ activeDetail.collectTotal || 0 }} 收藏</span>
          </div>
        </template>

        <div v-else-if="loadingDetail" class="empty-state">正在读取帖子详情...</div>

        <div class="action-row">
          <button type="button" class="like-button" :class="{ active: activeLiked }" :disabled="togglingLike" @click="toggleLike">
            {{ activeLiked ? '♥ 已赞' : '♡ 点赞' }}
          </button>
          <button type="button" class="delete-button" :disabled="deletingNote" @click="removeActiveNote">
            {{ confirmDeleteNote ? '确认删除' : '删除' }}
          </button>
        </div>

        <div class="comment-composer">
          <textarea v-model.trim="commentText" rows="3" placeholder="写下你的评论" />
          <button type="button" :disabled="commenting || !commentText" @click="submitComment">
            {{ commenting ? '发送中...' : '发送' }}
          </button>
        </div>

        <div class="comment-list">
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
              <button type="button" :disabled="deletingCommentId === comment.commentId" @click="removeComment(comment.commentId)">
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
</template>

<style scoped>
.discover-page {
  position: relative;
  width: min(100% - 72px, 1480px);
  margin: 0 auto;
  padding: 30px 0 80px;
}

.discover-header {
  position: sticky;
  top: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: minmax(320px, 560px) auto;
  gap: 28px;
  align-items: center;
  justify-content: center;
  min-height: 76px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(18px);
}

.search-bar {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 8px;
  height: 48px;
  border-radius: 999px;
  background: #f6f6f6;
  padding: 0 12px 0 8px;
}

.search-tabs {
  display: flex;
  gap: 4px;
}

.search-tabs button,
.search-bar > button,
.clear-button,
.header-links button,
.channel-tabs button,
.user-card button,
.follow-button,
.like-button,
.delete-button,
.comment-composer button,
.primary-submit {
  border: 0;
  cursor: pointer;
  font-weight: 850;
}

.search-tabs button {
  height: 34px;
  border-radius: 999px;
  background: transparent;
  color: #777777;
  padding: 0 12px;
}

.search-tabs button.active {
  background: #ffffff;
  color: #ff2442;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
}

.search-bar input {
  min-width: 0;
  border: 0;
  background: transparent;
  color: #111111;
  outline: 0;
}

.search-bar > button {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 50%;
  background: transparent;
  color: #333333;
  font-size: 1.25rem;
}

.clear-button {
  width: auto !important;
  color: #999999 !important;
  font-size: 0.82rem !important;
  padding: 0 6px;
}

.header-links {
  display: flex;
  align-items: center;
  gap: 24px;
  justify-self: end;
  color: #555555;
  font-weight: 760;
}

.publish-button {
  min-height: 40px;
  border-radius: 999px;
  background: #ff2442;
  color: #ffffff;
  padding: 0 18px;
}

.channel-tabs {
  display: flex;
  gap: 22px;
  align-items: center;
  overflow-x: auto;
  padding: 22px 0 18px;
}

.channel-tabs button {
  flex: 0 0 auto;
  height: 34px;
  border-radius: 999px;
  background: transparent;
  color: #555555;
  font-size: 0.98rem;
  padding: 0 4px;
}

.channel-tabs button.active {
  color: #111111;
  font-weight: 950;
}

.channel-tabs button.active::after {
  display: block;
  width: 18px;
  height: 3px;
  margin: 3px auto 0;
  border-radius: 999px;
  background: #ff2442;
  content: '';
}

.toast,
.empty-state {
  border-radius: 12px;
  margin-bottom: 16px;
  padding: 16px;
}

.toast.error {
  background: #fff2f4;
  color: #e60033;
}

.toast.success {
  background: #f0fdf4;
  color: #087f5b;
}

.empty-state {
  display: grid;
  min-height: 110px;
  place-items: center;
  border: 1px dashed #e8e8e8;
  color: #777777;
  font-weight: 850;
}

.masonry-feed {
  column-count: 5;
  column-gap: 22px;
}

.note-card {
  display: inline-block;
  width: 100%;
  overflow: hidden;
  break-inside: avoid;
  border-radius: 12px;
  background: #ffffff;
  cursor: pointer;
  margin: 0 0 22px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.note-card:hover {
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.12);
  transform: translateY(-3px);
}

.cover {
  position: relative;
  display: grid;
  min-height: 220px;
  place-items: center;
  overflow: hidden;
  border-radius: 12px;
  background: #f4f4f4;
  color: #999999;
  font-weight: 950;
}

.cover img {
  width: 100%;
  height: auto;
  min-height: 100%;
  object-fit: cover;
}

.note-body {
  display: grid;
  gap: 10px;
  padding: 10px 6px 4px;
}

.note-body h2 {
  display: -webkit-box;
  overflow: hidden;
  color: #222222;
  font-size: 0.96rem;
  font-weight: 760;
  line-height: 1.45;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.note-footer {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 7px;
  color: #666666;
  font-size: 0.82rem;
}

.note-footer span:nth-child(2) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.note-footer strong {
  color: #777777;
  font-weight: 760;
}

.avatar {
  display: grid;
  width: 40px;
  height: 40px;
  place-items: center;
  border-radius: 50%;
  background: #ffe9ee;
  color: #ff2442;
  font-weight: 950;
}

.avatar.small {
  width: 24px;
  height: 24px;
  font-size: 0.78rem;
}

.user-results {
  display: grid;
  gap: 12px;
  width: min(720px, 100%);
}

.user-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 14px;
  border: 1px solid #eeeeee;
  border-radius: 14px;
  padding: 14px;
}

.user-card strong {
  font-weight: 950;
}

.user-card p {
  color: #777777;
  font-size: 0.88rem;
}

.user-card button,
.follow-button {
  min-height: 34px;
  border-radius: 999px;
  background: #ff2442;
  color: #ffffff;
  padding: 0 14px;
}

.floating-stats {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 10;
  display: flex;
  gap: 8px;
}

.floating-stats span {
  border: 1px solid #eeeeee;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.86);
  color: #555555;
  font-size: 0.82rem;
  font-weight: 850;
  padding: 7px 10px;
  backdrop-filter: blur(12px);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 60;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.42);
  padding: 24px;
}

.composer-panel,
.detail-panel {
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.18);
}

.composer-panel {
  display: grid;
  gap: 14px;
  width: min(640px, 100%);
  max-height: 92vh;
  overflow: auto;
  padding: 24px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-head span {
  color: #ff2442;
  font-size: 0.75rem;
  font-weight: 950;
}

.panel-head h2 {
  color: #111111;
  font-size: 1.5rem;
  font-weight: 950;
}

.icon-button {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border: 0;
  border-radius: 50%;
  background: #f5f5f5;
  color: #222222;
  cursor: pointer;
  font-size: 1.32rem;
}

.field,
.field-grid {
  display: grid;
  gap: 7px;
}

.field-grid {
  grid-template-columns: 0.8fr 1.2fr;
  gap: 10px;
}

.field span {
  color: #555555;
  font-weight: 850;
}

.field input,
.field textarea,
.field select,
.comment-composer textarea {
  width: 100%;
  border: 1px solid #e5e5e5;
  border-radius: 12px;
  background: #ffffff;
  color: #111111;
  outline: 0;
  padding: 12px;
}

.field textarea,
.comment-composer textarea {
  resize: vertical;
}

.field input:focus,
.field textarea:focus,
.field select:focus,
.comment-composer textarea:focus {
  border-color: #ff2442;
  box-shadow: 0 0 0 3px rgba(255, 36, 66, 0.1);
}

.upload-box {
  position: relative;
  display: grid;
  min-height: 260px;
  place-items: center;
  overflow: hidden;
  border: 1px dashed #dddddd;
  border-radius: 14px;
  background: #fafafa;
  cursor: pointer;
}

.upload-box input {
  display: none;
}

.upload-box img {
  width: 100%;
  max-height: 340px;
  object-fit: cover;
}

.upload-box span {
  border-radius: 999px;
  background: #ffffff;
  color: #333333;
  font-weight: 900;
  padding: 10px 16px;
}

.topic-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.topic-preview span {
  border-radius: 999px;
  background: #f6f6f6;
  color: #777777;
  font-size: 0.82rem;
  font-weight: 850;
  padding: 6px 10px;
}

.primary-submit {
  min-height: 46px;
  border-radius: 999px;
  background: #ff2442;
  color: #ffffff;
}

.detail-panel {
  display: grid;
  grid-template-columns: minmax(420px, 1fr) minmax(420px, 0.82fr);
  width: min(1180px, 100%);
  max-height: min(92vh, 900px);
  overflow: hidden;
}

.detail-media {
  display: grid;
  align-content: center;
  min-height: 680px;
  overflow: auto;
  background: #111111;
  padding: 18px;
}

.detail-media img {
  width: 100%;
  max-height: 82vh;
  border-radius: 12px;
  object-fit: contain;
}

.media-loading {
  color: #ffffff;
  text-align: center;
}

.detail-side {
  position: relative;
  display: grid;
  grid-template-rows: auto auto auto auto auto 1fr;
  gap: 16px;
  min-height: 680px;
  overflow: auto;
  padding: 24px;
}

.close-detail {
  position: absolute;
  top: 18px;
  right: 18px;
}

.author-block {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding-right: 52px;
}

.author-block strong {
  font-weight: 950;
}

.author-block p {
  color: #777777;
  font-size: 0.84rem;
}

.detail-side h1 {
  color: #111111;
  font-size: 1.35rem;
  font-weight: 950;
  line-height: 1.35;
}

.detail-content,
.comment-item p {
  color: #333333;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-counts,
.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.detail-counts span {
  color: #777777;
  font-size: 0.88rem;
}

.like-button,
.delete-button {
  min-height: 38px;
  border-radius: 999px;
  padding: 0 16px;
}

.like-button {
  background: #fff1f3;
  color: #ff2442;
}

.like-button.active {
  background: #ff2442;
  color: #ffffff;
}

.delete-button {
  background: #f6f6f6;
  color: #555555;
}

.comment-composer {
  display: grid;
  gap: 10px;
}

.comment-composer button {
  justify-self: end;
  min-height: 36px;
  border-radius: 999px;
  background: #ff2442;
  color: #ffffff;
  padding: 0 18px;
}

.comment-list {
  display: grid;
  align-content: start;
  gap: 12px;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-head strong {
  font-weight: 950;
}

.comment-head span {
  border-radius: 999px;
  background: #f6f6f6;
  color: #777777;
  font-size: 0.8rem;
  font-weight: 900;
  padding: 3px 8px;
}

.comment-item {
  display: grid;
  gap: 6px;
  border-top: 1px solid #f1f1f1;
  padding-top: 12px;
}

.comment-item > div {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 8px;
  align-items: center;
}

.comment-item strong {
  font-size: 0.88rem;
}

.comment-item button {
  border: 0;
  background: transparent;
  color: #999999;
  cursor: pointer;
  font-size: 0.78rem;
}

.comment-item > span {
  color: #999999;
  font-size: 0.78rem;
}

@media (max-width: 1320px) {
  .masonry-feed {
    column-count: 4;
  }
}

@media (max-width: 1080px) {
  .discover-page {
    width: min(100% - 32px, 1480px);
  }

  .discover-header {
    grid-template-columns: 1fr;
  }

  .header-links {
    justify-self: start;
  }

  .masonry-feed {
    column-count: 3;
  }

  .detail-panel {
    grid-template-columns: 1fr;
    overflow: auto;
  }
}

@media (max-width: 700px) {
  .search-bar {
    grid-template-columns: 1fr auto;
    height: auto;
    border-radius: 18px;
    padding: 10px;
  }

  .search-tabs,
  .clear-button {
    grid-column: 1 / -1;
  }

  .masonry-feed {
    column-count: 2;
    column-gap: 14px;
  }

  .note-card {
    margin-bottom: 16px;
  }

  .field-grid,
  .author-block {
    grid-template-columns: 1fr;
  }

  .modal-backdrop {
    padding: 10px;
  }
}
</style>
