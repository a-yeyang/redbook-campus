import axios from 'axios'

const parseJsonWithLongIds = (payload: string) => {
  if (!payload) return payload

  const normalizedPayload = payload.replace(
    /("(?:id|noteId|creatorId|commentId|replyCommentId|userId|fansUserId|followingUserId)"\s*:\s*)(\d{16,})/g,
    '$1"$2"',
  )

  return JSON.parse(normalizedPayload)
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://127.0.0.1:8000',
  timeout: 15000,
  transformResponse: [
    (data) => {
      if (typeof data !== 'string') return data

      try {
        return parseJsonWithLongIds(data)
      } catch {
        return data
      }
    },
  ],
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export interface ApiResponse<T> {
  success: boolean
  message: string | null
  errorCode: string | null
  data: T
  pageNo?: number
  total?: number
  totalCount?: number
  totalPage?: number
}

export interface Channel {
  id: number
  name: string
}

export interface FeedNote {
  id: string | number
  title: string
  type: number
  cover?: string
  videoUri?: string
  creatorId?: number
  nickname?: string
  avatar?: string
  likeTotal?: string
}

export interface NoteDetail {
  id: string | number
  type: number
  title: string
  content?: string
  imgUris?: string[] | string
  videoUri?: string
  creatorId?: number
  nickname?: string
  avatar?: string
  likeTotal?: number | string
  collectTotal?: number | string
  commentTotal?: number | string
}

export interface CommentItem {
  commentId: number
  userId: number
  avatar?: string
  nickname?: string
  content: string
  imageUrl?: string
  createTime?: string
  likeTotal?: number
  childCommentTotal?: number
}

export interface PublishNotePayload {
  type: number
  imgUris: string[]
  title: string
  content: string
  topics: string[]
  channelId: number
}

export interface NoteInteractionState {
  noteId: string | number
  isLiked: boolean
  isCollected: boolean
}

export interface SearchNoteItem {
  noteId: string | number
  cover?: string
  title: string
  highlightTitle?: string
  avatar?: string
  nickname?: string
  updateTime?: string
  commentTotal?: string
  collectTotal?: string
  likeTotal?: string
}

export interface SearchUserItem {
  userId: number
  nickname?: string
  highlightNickname?: string
  avatar?: string
  redbookId?: string
  noteTotal?: number
  fansTotal?: string
}

export interface FollowingUserItem {
  userId: number
  avatar?: string
  nickname?: string
  introduction?: string
}

export interface UserProfile {
  avatar?: string
  nickname?: string
  redbookId?: string
  introduction?: string
}

const unwrap = <T>(response: ApiResponse<T>) => {
  if (!response.success) {
    throw new Error(response.message || response.errorCode || '请求失败')
  }
  return response
}

export const getChannels = async () => {
  const { data } = await api.post<ApiResponse<Channel[]>>('/note/channel/list', {})
  return unwrap(data).data
}

export const getFeed = async (channelId: number | null, pageNo = 1) => {
  const { data } = await api.post<ApiResponse<FeedNote[]>>('/note/discover/note/list', {
    channelId,
    pageNo,
  })
  return unwrap(data)
}

export const getNoteDetail = async (id: string | number) => {
  const { data } = await api.post<ApiResponse<NoteDetail>>('/note/note/detail', { id })
  return unwrap(data).data
}

export const getNoteInteractionState = async (noteId: string | number) => {
  const { data } = await api.post<ApiResponse<NoteInteractionState>>('/note/note/isLikedAndCollectedData', {
    noteId,
  })
  return unwrap(data).data
}

export const likeNote = async (id: string | number) => {
  const { data } = await api.post<ApiResponse<null>>('/note/note/like', { id })
  return unwrap(data)
}

export const unlikeNote = async (id: string | number) => {
  const { data } = await api.post<ApiResponse<null>>('/note/note/unlike', { id })
  return unwrap(data)
}

export const uploadFile = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post<ApiResponse<string>>('/oss/file/upload', formData)
  return unwrap(data).data
}

export const publishNote = async (payload: PublishNotePayload) => {
  const { data } = await api.post<ApiResponse<null>>('/note/note/publish', payload)
  return unwrap(data)
}

export const deleteNote = async (id: string | number) => {
  const { data } = await api.post<ApiResponse<null>>('/note/note/delete', { id })
  return unwrap(data)
}

export const getComments = async (noteId: string | number, pageNo = 1) => {
  const { data } = await api.post<ApiResponse<CommentItem[]>>('/comment/list', { noteId, pageNo })
  return unwrap(data)
}

export const publishComment = async (noteId: string | number, content: string, imageUrl = '') => {
  const { data } = await api.post<ApiResponse<null>>('/comment/publish', {
    noteId,
    content,
    imageUrl,
    replyCommentId: null,
  })
  return unwrap(data)
}

export const deleteComment = async (commentId: string | number) => {
  const { data } = await api.post<ApiResponse<null>>('/comment/delete', { commentId })
  return unwrap(data)
}

export const searchNotes = async (keyword: string, pageNo = 1) => {
  const { data } = await api.post<ApiResponse<SearchNoteItem[]>>('/search/note', { keyword, pageNo })
  return unwrap(data)
}

export const searchUsers = async (keyword: string, pageNo = 1) => {
  const { data } = await api.post<ApiResponse<SearchUserItem[]>>('/search/user', { keyword, pageNo })
  return unwrap(data)
}

export const getCurrentProfile = async () => {
  const { data } = await api.post<ApiResponse<UserProfile>>('/user/user/profile', {})
  return unwrap(data).data
}

export const followUser = async (followUserId: number) => {
  const { data } = await api.post<ApiResponse<null>>('/relation/follow', { followUserId })
  return unwrap(data)
}

export const unfollowUser = async (unfollowUserId: number) => {
  const { data } = await api.post<ApiResponse<null>>('/relation/unfollow', { unfollowUserId })
  return unwrap(data)
}

export const getFollowingList = async (userId: number, pageNo = 1) => {
  const { data } = await api.post<ApiResponse<FollowingUserItem[]>>('/relation/following/list', {
    userId,
    pageNo,
  })
  return unwrap(data)
}
