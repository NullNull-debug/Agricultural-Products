<template>
  <div
    :class="['message-item', { active: isActive, hover: isHover }]"
    @click="handleClick"
    @mouseenter="isHover = true"
    @mouseleave="isHover = false"
  >
    <div class="avatar">
      <span class="avatar-icon">{{ avatarText }}</span>
    </div>
    <div class="message-content">
      <div class="message-name">{{ name }}</div>
      <div class="message-preview">{{ preview }}</div>
    </div>
    <div class="message-meta">
      <div class="message-time">{{ time }}</div>
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'MessageListItem',
  props: {
    name: {
      type: String,
      required: true
    },
    preview: {
      type: String,
      default: ''
    },
    time: {
      type: String,
      default: ''
    },
    unreadCount: {
      type: Number,
      default: 0
    },
    isActive: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  setup(props, { emit }) {
    const isHover = ref(false)
    
    const avatarText = computed(() => {
      return props.name.charAt(0)
    })

    const handleClick = () => {
      emit('click')
    }

    return {
      isHover,
      avatarText,
      handleClick
    }
  }
}
</script>

<style scoped>
.message-item {
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

.unread-badge {
  background-color: #ff4444;
  color: white;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}
</style>