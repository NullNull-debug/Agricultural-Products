<template>
  <div class="admin-messages">
    <div class="header">
      <h2>客服消息</h2>
    </div>
    <div class="content">
      <div class="session-list">
        <div class="list-header">
          <span>用户会话列表</span>
        </div>
        <div class="sessions">
          <div
            v-for="session in sessions"
            :key="session.id"
            :class="['session-item', { active: selectedSessionId === session.id }]"
            @click="selectSession(session)"
          >
            <div class="session-info">
              <span class="user-name">{{ session.userId }}号用户</span>
              <span v-if="session.unreadCount > 0" class="unread-badge">{{ session.unreadCount }}</span>
            </div>
            <div class="last-message">{{ session.lastMessage || '暂无消息' }}</div>
            <div class="last-time">{{ formatTime(session.lastTime) }}</div>
          </div>
          <div v-if="sessions.length === 0" class="no-data">
            暂无会话记录
          </div>
        </div>
      </div>

      <div class="chat-area">
        <div v-if="!selectedSessionId" class="empty-state">
          <p>请选择一个会话开始聊天</p>
        </div>
        <template v-else>
          <div class="chat-header">
            <span>{{ selectedUserId }}号用户</span>
            <span class="status">在线</span>
          </div>
          <div ref="chatMessagesRef" class="chat-messages">
            <div
              v-for="(msg, index) in chatMessages"
              :key="index"
              :class="['message-bubble', { self: msg.isSelf }]"
            >
              <div class="bubble-content">{{ msg.content }}</div>
              <span class="bubble-time">{{ formatTime(msg.messageTime) }}</span>
            </div>
          </div>
          <div class="chat-input">
            <input
              v-model="inputText"
              type="text"
              placeholder="输入回复..."
              @keyup.enter="sendMessage"
            />
            <button @click="sendMessage" :disabled="sending">
              {{ sending ? '发送中...' : '发送' }}
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import axios from 'axios'

export default {
  name: 'AdminMessages',
  setup() {
    const sessions = ref([])
    const selectedSessionId = ref(null)
    const selectedUserId = ref(null)
    const chatMessages = ref([])
    const inputText = ref('')
    const sending = ref(false)
    const chatMessagesRef = ref(null)
    let ws = null
    let reconnectTimer = null

    const adminId = 1

    const loadSessions = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/message/admin/sessions')
        if (res.data.code === 200) {
          sessions.value = res.data.data
        }
      } catch (e) {
        console.error('Load sessions error:', e)
      }
    }

    const selectSession = async (session) => {
      selectedSessionId.value = session.id
      selectedUserId.value = session.userId

      try {
        const res = await axios.get(`http://localhost:8080/api/message/chat/${session.id}`)
        if (res.data.code === 200) {
          chatMessages.value = res.data.data.map(msg => ({
            id: msg.id,
            content: msg.content,
            messageTime: msg.messageTime,
            isSelf: msg.isSelf
          }))
          await nextTick()
          scrollToBottom()
        }

        if (session.unreadCount > 0) {
          await axios.post('http://localhost:8080/api/message/mark-read', null, {
            params: { sessionId: session.id, userId: session.userId }
          })
          session.unreadCount = 0
        }
      } catch (e) {
        console.error('Load messages error:', e)
      }
    }

    let lastMessageId = null

    const sendMessage = async () => {
      if (!inputText.value.trim() || !selectedSessionId.value) return

      sending.value = true
      try {
        const res = await axios.post('http://localhost:8080/api/message/admin/send', null, {
          params: {
            sessionId: selectedSessionId.value,
            adminId: adminId,
            content: inputText.value
          }
        })

        if (res.data.code === 200) {
          lastMessageId = Date.now().toString()
          chatMessages.value.push({
            id: lastMessageId,
            content: inputText.value,
            messageTime: new Date(),
            isSelf: true
          })
          inputText.value = ''
          await nextTick()
          scrollToBottom()
          await loadSessions()
        }
      } catch (e) {
        console.error('Send message error:', e)
        alert('发送失败，请重试')
      } finally {
        sending.value = false
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
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    let receivedMessageIds = new Set()

    const connectWebSocket = () => {
      try {
        ws = new WebSocket(`ws://localhost:8080/ws/message?userId=${adminId}`)

        ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            if (data.type === 'new_message' && data.sessionId === selectedSessionId.value) {
              const messageId = data.messageId || data.id
              if (messageId && receivedMessageIds.has(messageId)) {
                return
              }
              if (messageId) {
                receivedMessageIds.add(messageId)
              }
              chatMessages.value.push({
                id: messageId || Date.now().toString(),
                content: data.content,
                messageTime: new Date(),
                isSelf: false
              })
              scrollToBottom()
              loadSessions()
            }
          } catch (e) {
            console.error('Parse error:', e)
          }
        }

        ws.onclose = () => {
          reconnectTimer = setTimeout(() => {
            connectWebSocket()
          }, 3000)
        }
      } catch (e) {
        reconnectTimer = setTimeout(() => {
          connectWebSocket()
        }, 3000)
      }
    }

    onMounted(() => {
      loadSessions()
      connectWebSocket()
    })

    onUnmounted(() => {
      if (ws) ws.close()
      if (reconnectTimer) clearTimeout(reconnectTimer)
    })

    return {
      sessions,
      selectedSessionId,
      selectedUserId,
      chatMessages,
      inputText,
      sending,
      chatMessagesRef,
      selectSession,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.admin-messages {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.header {
  background-color: #4CAF50;
  color: white;
  padding: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.session-list {
  width: 300px;
  background-color: white;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  font-weight: bold;
  background-color: #fafafa;
}

.sessions {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.session-item:hover {
  background-color: #f5f5f5;
}

.session-item.active {
  background-color: #e8f5e9;
  border-left: 3px solid #4CAF50;
}

.session-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.user-name {
  font-weight: bold;
  color: #333;
}

.unread-badge {
  background-color: #ff4444;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.last-message {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.last-time {
  font-size: 12px;
  color: #999;
}

.no-data {
  padding: 40px;
  text-align: center;
  color: #999;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
}

.chat-header .status {
  color: #4CAF50;
  font-size: 13px;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message-bubble {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  max-width: 70%;
}

.message-bubble.self {
  align-items: flex-end;
  margin-left: auto;
}

.message-bubble:not(.self) {
  align-items: flex-start;
}

.bubble-content {
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
}

.message-bubble.self .bubble-content {
  background-color: #4CAF50;
  color: white;
}

.message-bubble:not(.self) .bubble-content {
  background-color: #f5f5f5;
  color: #333;
}

.bubble-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  background-color: #fafafa;
}

.chat-input input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
}

.chat-input input:focus {
  border-color: #4CAF50;
}

.chat-input button {
  padding: 10px 24px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
}

.chat-input button:hover:not(:disabled) {
  background-color: #45a049;
}

.chat-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>