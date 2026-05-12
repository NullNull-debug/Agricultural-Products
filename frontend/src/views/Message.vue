<template>
  <div class="message-page">
    <MessageLayout
      @mark-all-read="handleMarkAllRead"
      @clear-all="handleClearAll"
    >
      <template #sidebar>
        <div class="sidebar">
          <MessageFilter
            :active-tab="activeTab"
            :tabs="filterTabs"
            @tab-change="handleTabChange"
          />
          <div class="message-list">
            <div 
              v-if="privateContacts.length > 0"
              :class="['message-item', { active: selectedId === 'private', hover: isPrivateHover, 'private-expanded': privateDropdownOpen }]"
              @click="togglePrivateDropdown"
              @mouseenter="isPrivateHover = true"
              @mouseleave="isPrivateHover = false"
            >
              <div class="avatar">
                <span class="avatar-icon">📩</span>
              </div>
              <div class="message-content">
                <div class="message-name">私信</div>
                <div class="message-preview">{{ privateDropdownOpen ? '选择聊天对象' : '点击展开联系人列表' }}</div>
              </div>
              <div class="message-meta">
                <span :class="['dropdown-arrow', { rotated: privateDropdownOpen }]">▼</span>
              </div>
              
              <div v-if="privateDropdownOpen" class="private-dropdown">
                <div
                  v-for="contact in privateContacts"
                  :key="contact.id"
                  class="dropdown-contact"
                  @click.stop="selectPrivateContact(contact)"
                >
                  <div class="contact-avatar">{{ contact.name.charAt(0) }}</div>
                  <div class="contact-info">
                    <div class="contact-name">{{ contact.name }}</div>
                    <div class="contact-status">在线</div>
                  </div>
                </div>
              </div>
            </div>
            
            <MessageListItem
              v-for="item in filteredMessageList"
              :key="item.id"
              :name="item.name"
              :preview="item.preview"
              :time="item.time"
              :unread-count="item.unreadCount"
              :is-active="selectedId === item.id"
              @click="selectMessage(item)"
            />
            <div v-if="filteredMessageList.length === 0 && privateContacts.length === 0" class="no-messages">
              <p>暂无消息</p>
            </div>
          </div>
        </div>
      </template>

      <template #content>
        <div class="content-area">
          <MessageEmpty v-if="!selectedItem" text="请选择一条消息查看详情" />
          <div v-else class="message-detail">
            <div class="detail-header">
              <h3>{{ selectedItem.name }}</h3>
              <span class="detail-time">{{ selectedItem.time }}</span>
            </div>
            <div class="detail-content">
              <div v-if="selectedItem.type === 'system' || selectedItem.type === 'announcement'" class="announcement-content">
                <p>{{ selectedItem.content }}</p>
              </div>
              <div v-else-if="selectedItem.type === 'private' || selectedItem.type === 'service'" class="chat-content">
                <div ref="chatMessagesRef" class="chat-messages">
                  <div
                    v-for="(msg, index) in currentChatMessages"
                    :key="index"
                    :class="['message-bubble', { 'self': isMessageSelf(msg) }]"
                  >
                    <div class="avatar-small" v-if="!isMessageSelf(msg)">
                      <span>{{ getAvatarText(msg) }}</span>
                    </div>
                    <div class="bubble-wrapper">
                      <div class="bubble-content">{{ msg.content }}</div>
                      <span class="bubble-time">{{ msg.time || msg.messageTime }}</span>
                    </div>
                    <div class="avatar-small self-avatar" v-if="isMessageSelf(msg)">
                      <span>{{ getCurrentUserAvatar() }}</span>
                    </div>
                  </div>
                </div>
                <div class="chat-input-area">
                  <input
                    v-model="inputText"
                    type="text"
                    class="message-input"
                    placeholder="输入消息..."
                    :disabled="!isConnected"
                    @keyup.enter="handleSendClick"
                  />
                  <button class="send-btn" @click="handleSendClick" :disabled="!isConnected || sending">
                    {{ sending ? '发送中...' : '发送' }}
                  </button>
                </div>
                <div v-if="!isConnected" class="connection-error">
                  消息服务暂时不可用，请稍后再试
                </div>
              </div>
              <div v-else class="simple-content">
                <p>{{ selectedItem.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>
    </MessageLayout>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import MessageLayout from '../components/message/MessageLayout.vue'
import MessageFilter from '../components/message/MessageFilter.vue'
import MessageListItem from '../components/message/MessageListItem.vue'
import MessageEmpty from '../components/message/MessageEmpty.vue'
import messageApi from '../api/message'

export default {
  name: 'Message',
  components: {
    MessageLayout,
    MessageFilter,
    MessageListItem,
    MessageEmpty
  },
  setup() {
    const selectedId = ref(null)
    const activeTab = ref('all')
    const inputText = ref('')
    const sending = ref(false)
    const isConnected = ref(false)
    const chatMessagesRef = ref(null)
    const privateDropdownOpen = ref(false)
    const isPrivateHover = ref(false)
    let ws = null
    let reconnectTimer = null

    const messageList = ref([])
    const currentChatMessages = ref([])

    const selectedItem = computed(() => {
      const item = messageList.value.find(item => item.id === selectedId.value)
      return item
    })

    const selectedMessage = computed(() => {
      return messageList.value.find(item => item.id === selectedId.value)
    })

    const user = computed(() => {
      const userInfo = localStorage.getItem('user')
      return userInfo ? JSON.parse(userInfo) : null
    })

    const privateContacts = computed(() => {
      if (!user.value) return []
      
      if (user.value.role === 1) {
        return [{ id: 'user', name: 'user' }]
      } else {
        return [{ id: 'admin', name: 'admin' }]
      }
    })

    const filterTabs = computed(() => {
      const tabs = [
        { key: 'all', label: '全部消息', count: messageList.value.length },
        { key: 'system', label: '系统通知', count: messageList.value.filter(m => m.type === 'system' || m.type === 'announcement').length },
        { key: 'interaction', label: '互动消息', count: messageList.value.filter(m => m.type === 'order').length },
        { key: 'private', label: '私信', count: messageList.value.filter(m => m.type === 'private').length },
        { key: 'order', label: '订单物流', count: messageList.value.filter(m => m.type === 'order').length },
        { key: 'promotion', label: '优惠活动', count: messageList.value.filter(m => m.type === 'promotion').length },
        { key: 'service', label: '客服消息', count: messageList.value.filter(m => m.type === 'service').length }
      ]
      return tabs
    })

    const filteredMessageList = computed(() => {
      if (activeTab.value === 'all') {
        return messageList.value.filter(m => m.type !== 'private')
      } else if (activeTab.value === 'system') {
        return messageList.value.filter(m => m.type === 'system' || m.type === 'announcement')
      } else if (activeTab.value === 'interaction') {
        return messageList.value.filter(m => m.type === 'order' || m.type === 'review')
      } else if (activeTab.value === 'private') {
        return messageList.value.filter(m => m.type === 'private')
      } else if (activeTab.value === 'order') {
        return messageList.value.filter(m => m.type === 'order')
      } else if (activeTab.value === 'promotion') {
        return messageList.value.filter(m => m.type === 'promotion')
      } else if (activeTab.value === 'service') {
        return messageList.value.filter(m => m.type === 'service')
      }
      return messageList.value.filter(m => m.type !== 'private')
    })

    const getCurrentUserId = () => {
      const userInfo = localStorage.getItem('user')
      if (userInfo) {
        const userData = JSON.parse(userInfo)
        return userData.id
      }
      return null
    }

    const isMessageSelf = (msg) => {
      const currentUserId = getCurrentUserId()
      const senderId = msg.userId || msg.senderId || msg.sendUserId
      return String(senderId) === String(currentUserId)
    }

    const getAvatarText = (msg) => {
      const senderId = msg.userId || msg.senderId || msg.sendUserId
      if (senderId === 1 || String(senderId) === '1') {
        return '管'
      }
      return '用'
    }

    const getCurrentUserAvatar = () => {
      const currentUserId = getCurrentUserId()
      if (currentUserId === 1 || String(currentUserId) === '1') {
        return '管'
      }
      return '用'
    }

    const connectWebSocket = () => {
      if (!user.value) return

      try {
        console.log('正在连接WebSocket，用户ID:', user.value.id)
        ws = new WebSocket(`ws://localhost:8080/ws/message?userId=${user.value.id}`)

        ws.onopen = () => {
          console.log('WebSocket连接成功')
          isConnected.value = true
          if (reconnectTimer) {
            clearTimeout(reconnectTimer)
            reconnectTimer = null
          }
        }

        ws.onmessage = (event) => {
          try {
            console.log('收到WebSocket消息:', event.data)
            const data = JSON.parse(event.data)
            if (data.type === 'new_message') {
              console.log('收到新消息:', data)
              console.log('当前用户ID:', user.value?.id)
              console.log('消息中的userId:', data.userId)
              console.log('消息中的targetUserId:', data.targetUserId)
              
              if (data.isSelf) {
                console.log('这是自己发送的消息，跳过（已通过乐观更新显示）')
                return
              }
              
              let shouldDisplay = false
              const currentSessionId = Number(selectedId.value)
              
              console.log('当前选中会话ID:', currentSessionId, '类型:', typeof currentSessionId)
              console.log('消息中的sessionId:', data.sessionId, '类型:', typeof data.sessionId)
              
              if (data.sessionId === currentSessionId) {
                shouldDisplay = true
              } else if (data.targetUserId === user.value?.id) {
                const session = messageList.value.find(
                  m => m.type === 'private' && m.name === (data.userId === 1 ? 'admin' : 'user')
                )
                if (session) {
                  console.log('找到匹配的会话:', session)
                  console.log('会话ID:', session.id, '类型:', typeof session.id)
                  if (session.id === currentSessionId) {
                    shouldDisplay = true
                  }
                }
              }
              
              if (shouldDisplay) {
                currentChatMessages.value.push({
                  content: data.content,
                  time: data.time,
                  userId: data.userId,
                  isSelf: false
                })
                scrollToBottom()
              } else {
                console.log('消息不匹配当前会话，不显示')
              }
            }
          } catch (e) {
            console.error('解析消息失败:', e)
          }
        }

        ws.onclose = () => {
          console.log('WebSocket连接关闭')
          isConnected.value = false
          reconnectTimer = setTimeout(() => {
            connectWebSocket()
          }, 3000)
        }

        ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          isConnected.value = false
        }
      } catch (e) {
        console.error('WebSocket连接失败:', e)
        isConnected.value = false
        reconnectTimer = setTimeout(() => {
          connectWebSocket()
        }, 3000)
      }
    }

    const loadMessages = async () => {
      if (!user.value) {
        console.log('用户未登录')
        return
      }
      try {
        console.log('正在加载消息列表，用户ID:', user.value.id)
        const res = await messageApi.getMessageList(user.value.id)
        console.log('消息列表响应:', res.data)
        if (res.data.code === 200) {
          const messages = (res.data.data.messages || []).map(msg => ({
            ...msg,
            type: msg.type || 'system'
          }))
          const sessions = (res.data.data.sessions || []).map(session => ({
            ...session,
            name: session.targetName,
            preview: session.lastMessage,
            time: session.lastTime ? formatDateTime(session.lastTime) : '未知',
            type: session.sessionType
          }))
          messageList.value = [...messages, ...sessions]
          console.log('消息列表已更新:', messageList.value)
        }
      } catch (e) {
        console.error('加载消息失败:', e)
      }
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '未知'
      const date = new Date(dateTime)
      const now = new Date()
      const diff = now.getTime() - date.getTime()
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString('zh-CN')
    }

    const loadChatMessages = async (sessionId) => {
      try {
        console.log('正在加载聊天记录，sessionId:', sessionId)
        const res = await messageApi.getChatMessages(sessionId)
        console.log('聊天记录响应:', res.data)
        if (res.data.code === 200) {
          currentChatMessages.value = res.data.data.map(msg => ({
            id: msg.id,
            content: msg.content,
            time: msg.messageTime,
            userId: msg.userId || msg.senderId,
            isSelf: msg.isSelf
          }))
          console.log('聊天消息已更新:', currentChatMessages.value)
          await nextTick()
          scrollToBottom()
        }
      } catch (e) {
        console.error('加载聊天记录失败:', e)
      }
    }

    const scrollToBottom = () => {
      if (chatMessagesRef.value) {
        chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
      }
    }

    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }

    const togglePrivateDropdown = () => {
      privateDropdownOpen.value = !privateDropdownOpen.value
    }

    const selectPrivateContact = async (contact) => {
      console.log('选择联系人:', contact)
      privateDropdownOpen.value = false
      
      let privateSession = messageList.value.find(m => m.type === 'private' && m.name === contact.name)
      
      if (privateSession) {('找到已有会话:', privateSession)
        selectMessage(privateSession)
      } else {
        try {
          console.log('创建新会话，userId:', user.value.id, 'contactId:', contact.id)
          const res = await messageApi.createPrivateSession(user.value.id, contact.id)
          console.log('创建会话响应:', res.data)
          if (res.data.code === 200) {
            const newSession = {
              id: res.data.data.sessionId,
              name: contact.name,
              type: 'private',
              preview: '开始聊天吧',
              time: '刚刚',
              unreadCount: 0
            }
            console.log('新会话:', newSession)
            messageList.value.unshift(newSession)
            selectMessage(newSession)
          }
        } catch (e) {
          console.error('创建会话失败:', e)
        }
      }
    }

    const handleTabChange = (tabKey) => {
      activeTab.value = tabKey
    }

    const selectMessage = async (item) => {
      console.log('选择消息项:', item)
      selectedId.value = item.id

      if (item.unreadCount > 0) {
        const index = messageList.value.findIndex(m => m.id === item.id)
        if (index !== -1) {
          messageList.value[index].unreadCount = 0
        }
        await messageApi.markRead(item.id, user.value.id)
      }

      if (item.type === 'private' || item.type === 'service') {
        await loadChatMessages(item.id)
      }
    }

    const handleMarkAllRead = async () => {
      if (!user.value) return
      try {
        await messageApi.markAllRead(user.value.id)
        messageList.value.forEach(msg => {
          msg.unreadCount = 0
        })
        alert('已全部标记为已读')
      } catch (e) {
        console.error('标记已读失败:', e)
        alert('操作失败')
      }
    }

    const handleClearAll = async () => {
      if (!user.value) return
      if (confirm('确定要清空所有消息吗？此操作不可恢复！')) {
        try {
          await messageApi.clearAll(user.value.id)
          messageList.value = []
          selectedId.value = null
          alert('消息已清空')
        } catch (e) {
          console.error('清空消息失败:', e)
          alert('操作失败')
        }
      }
    }

    const handleSendClick = () => {
      console.log('=== 发送按钮被点击 ===')
      console.log('isConnected:', isConnected.value)
      console.log('sending:', sending.value)
      console.log('inputText:', inputText.value)
      console.log('user:', user.value)
      console.log('selectedMessage:', selectedMessage.value)
      console.log('selectedId:', selectedId.value)
      console.log('selectedItem:', selectedItem.value)
      
      if (!isConnected.value) {
        console.log('未连接到消息服务')
        return
      }
      
      if (!inputText.value.trim()) {
        console.log('输入内容为空')
        return
      }
      
      if (!selectedMessage.value) {
        console.log('没有选中的消息会话')
        return
      }
      
      sendMessage()
    }

    const sendMessage = async () => {
      console.log('=== sendMessage 被调用 ===')
      console.log('inputText:', inputText.value)
      console.log('user:', user.value)
      console.log('selectedMessage:', selectedMessage.value)
      
      if (!inputText.value.trim() || !user.value || !selectedMessage.value) {
        console.log('发送条件不满足')
        return
      }

      sending.value = true
      try {
        const sessionId = selectedMessage.value.id
        const sendUserId = user.value.id
        const content = inputText.value
        
        console.log('发送消息参数:', { sessionId, sendUserId, content })
        
        const res = await messageApi.sendMessage(sessionId, sendUserId, content)
        
        console.log('发送消息响应:', res)
        console.log('响应数据:', res.data)
        console.log('响应code:', res.data.code)

        if (res.data.code === 200) {
          console.log('消息发送成功，添加到聊天列表')
          const newMsg = {
            content: content,
            time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
            userId: sendUserId,
            isSelf: true
          }
          console.log('新消息对象:', newMsg)
          currentChatMessages.value.push(newMsg)
          console.log('当前聊天消息列表:', currentChatMessages.value)
          
          if (selectedMessage.value) {
            selectedMessage.value.preview = content
          }
          inputText.value = ''
          await nextTick()
          scrollToBottom()
          console.log('消息已添加到UI')
        } else {
          console.log('发送失败:', res.data.message)
          alert('发送失败: ' + res.data.message)
        }
      } catch (e) {
        console.error('发送消息异常:', e)
        alert('发送失败，请重试')
      } finally {
        sending.value = false
      }
    }

    const handleClickOutside = (event) => {
      if (!event.target.closest('.message-item') && !event.target.closest('.private-dropdown')) {
        privateDropdownOpen.value = false
      }
    }

    onMounted(async () => {
      console.log('Message组件已挂载')
      await loadMessages()
      connectWebSocket()
      document.addEventListener('click', handleClickOutside)
    })

    onUnmounted(() => {
      if (ws) {
        ws.close()
      }
      if (reconnectTimer) {
        clearTimeout(reconnectTimer)
      }
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      messageList,
      selectedId,
      selectedItem: selectedMessage,
      activeTab,
      filterTabs,
      filteredMessageList,
      inputText,
      sending,
      isConnected,
      currentChatMessages,
      chatMessagesRef,
      privateDropdownOpen,
      isPrivateHover,
      privateContacts,
      isMessageSelf,
      getAvatarText,
      getCurrentUserAvatar,
      handleTabChange,
      togglePrivateDropdown,
      selectPrivateContact,
      selectMessage,
      handleMarkAllRead,
      handleClearAll,
      handleSendClick,
      formatTime
    }
  }
}
</script>

<style scoped>
.message-page {
  width: 90%;
  max-width: 1200px;
  min-width: 800px;
  height: 85vh;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.message-page :deep(.message-layout) {
  width: 100%;
  height: 100%;
  background-color: white;
  display: flex;
  flex-direction: column;
}

.message-page :deep(.message-main) {
  width: 100%;
  height: calc(100% - 60px);
}

.sidebar {
  width: 30%;
  min-width: 280px;
  max-width: 400px;
  height: 100%;
  background-color: white;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
}

.message-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
  background-color: white;
}

.message-item:hover,
.message-item.hover {
  background-color: #f9f9f9;
}

.message-item.active {
  background-color: #e8f5e9;
  border-left: 3px solid #4CAF50;
}

.message-item.private-expanded {
  background-color: #f5f5f5;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-icon {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.message-content {
  flex: 1;
  padding: 0 12px;
  overflow: hidden;
}

.message-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-preview {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.message-time {
  font-size: 12px;
  color: #bbb;
}

.dropdown-arrow {
  font-size: 10px;
  color: #999;
  transition: transform 0.2s;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.private-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid #eee;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  z-index: 100;
}

.dropdown-contact {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-contact:hover {
  background-color: #f5f5f5;
}

.contact-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: bold;
  margin-right: 12px;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 2px;
}

.contact-status {
  font-size: 12px;
  color: #4CAF50;
}

.no-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100px;
  color: #999;
  font-size: 14px;
}

.content-area {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  height: 100%;
  overflow: hidden;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.detail-time {
  font-size: 13px;
  color: #999;
}

.detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.announcement-content,
.simple-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  overflow-y: auto;
  flex: 1;
}

.chat-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  height: calc(100vh - 220px);
}

.message-bubble {
  display: flex;
  align-items: flex-end;
  margin-bottom: 15px;
  max-width: 70%;
}

.message-bubble.self {
  flex-direction: row-reverse;
  margin-left: auto;
}

.message-bubble:not(.self) {
  margin-right: auto;
}

.avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
  margin-right: 10px;
}

.avatar-small.self-avatar {
  margin-right: 0;
  margin-left: 10px;
}

.bubble-wrapper {
  display: flex;
  flex-direction: column;
}

.bubble-content {
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message-bubble.self .bubble-content {
  background-color: #4CAF50;
  color: white;
  border-radius: 18px 4px 18px 18px;
}

.message-bubble:not(.self) .bubble-content {
  background-color: #f5f5f5;
  color: #333;
  border-radius: 4px 18px 18px 18px;
}

.bubble-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.message-bubble:not(.self) .bubble-time {
  text-align: left;
}

.chat-input-area {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  flex-shrink: 0;
}

.message-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.message-input:focus {
  border-color: #4CAF50;
}

.message-input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.send-btn {
  padding: 10px 24px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.send-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.send-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.connection-error {
  color: #ff4444;
  font-size: 12px;
  text-align: center;
  padding-top: 10px;
}

@media (max-width: 800px) {
  .message-page {
    width: 100%;
    min-width: 100%;
    height: 100vh;
    border-radius: 0;
  }

  .sidebar {
    width: 100%;
    max-width: 100%;
    margin-bottom: 15px;
  }

  .content-area {
    margin-left: 0;
    min-height: 400px;
  }
}
</style>