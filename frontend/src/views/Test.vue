<template>
  <div class="test">
    <h2>测试页面</h2>
    <button @click="testConsoleLog">测试console.log</button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Test',
  setup() {
    console.log('Test组件初始化')

    onMounted(() => {
      console.log('Test组件挂载完成')
    })

    const testConsoleLog = () => {
      console.log('测试按钮点击')
    }

    return {
      testConsoleLog
    }
  }
}
</script>

<style scoped>
.test {
  padding: 20px 0;
}

.test h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>