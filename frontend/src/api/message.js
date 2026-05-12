import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(config => {
  console.log('请求拦截器 - URL:', config.url)
  console.log('请求拦截器 - params:', config.params)
  const userInfo = localStorage.getItem('user')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    config.params = config.params || {}
    config.params.userId = user.id
  }
  console.log('请求拦截器 - 最终params:', config.params)
  return config
})

api.interceptors.response.use(
  response => {
    console.log('响应拦截器 - 状态:', response.status)
    console.log('响应拦截器 - data:', response.data)
    return response
  },
  error => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

export default {
  getMessageList(userId) {
    return api.get('/message/list', { params: { userId } })
  },

  getChatMessages(sessionId, page = 1, size = 20) {
    return api.get(`/message/chat/${sessionId}`, { params: { page, size } })
  },

  sendMessage(sessionId, userId, content) {
    console.log('=== 发送消息 API ===')
    console.log('sessionId:', sessionId, 'userId:', userId, 'content:', content)
    return api.post('/message/send', null, {
      params: { sessionId, userId, content }
    })
  },

  markRead(sessionId, userId) {
    return api.post('/message/mark-read', null, {
      params: { sessionId, userId }
    })
  },

  markAllRead(userId) {
    return api.post('/message/mark-all-read', null, {
      params: { userId }
    })
  },

  clearAll(userId) {
    return api.delete('/message/clear', { params: { userId } })
  },

  getUnreadCount(userId) {
    return api.get('/message/unread-count', { params: { userId } })
  },

  getServiceSession(userId) {
    return api.get('/message/service-session', { params: { userId } })
  },

  getAdminSessions() {
    return api.get('/message/admin/sessions')
  },

  adminSendMessage(sessionId, adminId, content) {
    return api.post('/message/admin/send', null, {
      params: { sessionId, adminId, content }
    })
  },

  createPrivateSession(userId, contactId) {
    return api.post('/message/private-session', null, {
      params: { userId, contactId }
    })
  }
}