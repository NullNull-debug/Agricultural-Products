<template>
  <div class="empty-state">
    <div class="empty-icon">
      <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="60" cy="45" r="20" stroke="#ddd" stroke-width="2" fill="none"/>
        <path d="M50 45 L55 50 L70 35" stroke="#ddd" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <rect x="30" y="70" width="60" height="35" rx="5" stroke="#ddd" stroke-width="2" fill="none"/>
        <line x1="38" y1="82" x2="82" y2="82" stroke="#ddd" stroke-width="2"/>
        <line x1="38" y1="92" x2="70" y2="92" stroke="#ddd" stroke-width="2"/>
      </svg>
    </div>
    <p class="empty-text">{{ text }}</p>
  </div>
</template>

<script>
export default {
  name: 'MessageEmpty',
  props: {
    text: {
      type: String,
      default: '快找小伙伴聊天吧 (｡･ω･｡)ﾉ'
    }
  }
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  text-align: center;
}
</style>