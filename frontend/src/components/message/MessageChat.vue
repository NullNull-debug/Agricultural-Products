<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="chat-avatar">
        <span class="avatar-icon">{{ chatName.charAt(0) }}</span>
      </div>
      <div class="chat-info">
        <h3 class="chat-name">{{ chatName }}</h3>
        <p class="chat-status">{{ status }}</p>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message-bubble', { 'self': message.isSelf }]"
      >
        <div class="bubble-content">{{ message.content }}</div>
        <span class="bubble-time">{{ message.time }}</span>
      </div>
    </div>
    
    <div class="chat-input-area">
      <input
        v-model="inputText"
        type="text"
        class="message-input"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
      />
      <button class="send-btn" @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
import { ref, nextTick } from 'vue'

export default {
  name: 'MessageChat',
  props: {
    chatName: {
      type: String,
      default: ''
    },
    status: {
      type: String,
      default: '在线'
    },
    initialMessages: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const inputText = ref('')
    const messagesContainer = ref(null)
    
    const messages = ref([
      ...props.initialMessages,
      { content: '您好！请问有什么可以帮助您的？', time: '10:30', isSelf: false },
      { content: '我想咨询一下订单物流信息', time: '10:31', isSelf: true },
      { content: '好的，请您提供一下订单号，我帮您查询', time: '10:32', isSelf: false }
    ])

    const sendMessage = () => {
      if (!inputText.value.trim()) return
      
      messages.value.push({
        content: inputText.value,
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        isSelf: true
      })
      
      inputText.value = ''
      
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    return {
      inputText,
      messages,
      messagesContainer,
      sendMessage
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  background-color: #fafafa;
  border-radius: 8px 8px 0 0;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.avatar-icon {
  color: white;
  font-size: 16px;
  font-weight: bold;
}

.chat-info {
  flex: 1;
}

.chat-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chat-status {
  margin: 4px 0 0;
  font-size: 13px;
  color: #666;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f8f9fa;
}

.message-bubble {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  max-width: 70%;
}

.message-bubble.self {
  align-items: flex-end;
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
  border-radius: 18px 4px 18px 18px;
}

.message-bubble:not(.self) .bubble-content {
  background-color: white;
  color: #333;
  border: 1px solid #eee;
  border-radius: 4px 18px 18px 18px;
}

.bubble-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.message-bubble.self .bubble-time {
  text-align: right;
}

.chat-input-area {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
  background-color: white;
  border-radius: 0 0 8px 8px;
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

.send-btn:hover {
  background-color: #45a049;
}

.send-btn:active {
  background-color: #3d8b40;
}
</style>