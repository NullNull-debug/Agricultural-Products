<template>
  <div class="message-layout">
    <header class="message-header">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
        返回
      </button>
      <h1 class="page-title">我的消息</h1>
      <div class="header-actions">
        <button class="action-btn" @click="markAllRead">全部已读</button>
        <button class="action-btn" @click="clearAll">清空消息</button>
      </div>
    </header>
    <main class="message-main">
      <slot name="sidebar"></slot>
      <slot name="content"></slot>
    </main>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'MessageLayout',
  emits: ['mark-all-read', 'clear-all'],
  setup(props, { emit }) {
    const router = useRouter()

    const goBack = () => {
      router.push('/')
    }

    const markAllRead = () => {
      emit('mark-all-read')
    }

    const clearAll = () => {
      emit('clear-all')
    }

    return {
      goBack,
      markAllRead,
      clearAll
    }
  }
}
</script>

<style scoped>
.message-layout {
  height: 100%;
  background-color: white;
  display: flex;
  flex-direction: column;
}

.message-header {
  background-color: #4CAF50;
  color: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.back-icon {
  font-size: 18px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.message-main {
  flex: 1;
  display: flex;
  padding: 20px;
  overflow: hidden;
}
</style>