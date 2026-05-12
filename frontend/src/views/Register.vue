<template>
  <div class="register">
    <div class="register-form">
      <h2>注册</h2>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="form.username" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="form.password" required>
        </div>
        <div class="form-group">
          <label for="name">姓名</label>
          <input type="text" id="name" v-model="form.name" required>
        </div>
        <div class="form-group">
          <label for="phone">手机号</label>
          <input type="tel" id="phone" v-model="form.phone" required>
        </div>
        <div class="form-group">
          <label for="address">地址</label>
          <input type="text" id="address" v-model="form.address" required>
        </div>
        <button type="submit" class="register-btn">注册</button>
        <p class="login-link">已有账号？<router-link to="/login">立即登录</router-link></p>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { userApi } from '../api'
import { useRouter } from 'vue-router'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const form = ref({
      username: '',
      password: '',
      name: '',
      phone: '',
      address: '',
      role: 0
    })

    const register = async () => {
      try {
        const response = await userApi.register(form.value)
        if (response) {
          alert('注册成功！')
          router.push('/login')
        } else {
          alert('注册失败，请稍后重试')
        }
      } catch (error) {
        console.error('注册失败:', error)
        // 显示后端返回的具体错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert('注册失败: ' + error.response.data.message)
        } else {
          alert('注册失败，请稍后重试')
        }
      }
    }

    return {
      form,
      register
    }
  }
}
</script>

<style scoped>
.register {
  padding: 60px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-form {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 40px;
  width: 400px;
}

.register-form h2 {
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

.register-btn {
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

.register-btn:hover {
  background-color: #45a049;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #4CAF50;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>