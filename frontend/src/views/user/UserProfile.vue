<template>
  <div class="user-profile">
    <h2>修改个人信息</h2>
    <form @submit.prevent="updateProfile">
      <div class="form-group">
        <label>用户名</label>
        <input type="text" v-model="form.username" disabled>
      </div>
      <div class="form-group">
        <label>姓名</label>
        <input type="text" v-model="form.name" required>
      </div>
      <div class="form-group">
        <label>手机号</label>
        <input type="tel" v-model="form.phone" required>
      </div>
      <div class="form-group">
        <label>地址</label>
        <input type="text" v-model="form.address" required>
      </div>
      <div class="form-group">
        <label>密码</label>
        <input type="password" v-model="form.password" placeholder="留空表示不修改密码">
      </div>
      <div class="form-actions">
        <button type="submit" class="save-btn">保存修改</button>
        <router-link to="/" class="cancel-btn">取消</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { userApi } from '../../api'

export default {
  name: 'UserProfile',
  setup() {
    const form = ref({
      id: '',
      username: '',
      name: '',
      phone: '',
      address: '',
      password: '',
      role: 0
    })

    const loadUserInfo = () => {
      const userInfo = localStorage.getItem('user')
      if (userInfo) {
        const user = JSON.parse(userInfo)
        form.value = {
          ...user,
          password: ''
        }
      }
    }

    const updateProfile = async () => {
      try {
        const response = await userApi.update(form.value)
        // 更新本地存储的用户信息
        const updatedUser = { ...response, password: null }
        localStorage.setItem('user', JSON.stringify(updatedUser))
        alert('个人信息更新成功！')
      } catch (error) {
        console.error('更新个人信息失败:', error)
        alert('更新失败，请稍后重试')
      }
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      form,
      updateProfile
    }
  }
}
</script>

<style scoped>
.user-profile {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.user-profile h2 {
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.save-btn,
.cancel-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  text-decoration: none;
  text-align: center;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
  flex: 1;
}

.cancel-btn {
  background-color: #999;
  color: white;
  flex: 1;
}
</style>
