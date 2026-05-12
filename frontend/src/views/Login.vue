<template>
  <div class="login">
    <div class="login-form">
      <h2>登录</h2>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="form.username" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="form.password" required>
        </div>
        <button type="submit" class="login-btn">登录</button>
        <p class="register-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { userApi } from '../api'
import { useRouter } from 'vue-router'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const form = ref({
      username: '',
      password: ''
    })

    const login = async () => {
      try {
        const response = await userApi.login(form.value)
        if (response.success) {
          localStorage.setItem('user', JSON.stringify(response.data))
          alert('登录成功！')
          window.location.href = '/'
        } else {
          alert('登录失败: ' + (response.message || '请检查用户名和密码'))
        }
      } catch (error) {
        console.error('登录失败:', error)
        if (error.response && error.response.data && error.response.data.message) {
          alert('登录失败: ' + error.response.data.message)
        } else {
          alert('登录失败，请稍后重试')
        }
      }
    }

    return {
      form,
      login
    }
  }
}
</script>

<style scoped>
.login {
  padding: 60px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 40px;
  width: 400px;
}

.login-form h2 {
  font-size: 24px;
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.login-btn:hover {
  background-color: #45a049;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #4CAF50;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
