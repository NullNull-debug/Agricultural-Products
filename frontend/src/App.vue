<template>
  <div id="app">
    <header class="header" v-if="!isMessagePage">
      <div class="container clearfix">
        <div class="logo">特色农产品销售平台</div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/products">商品列表</router-link>
          <router-link to="/cart">购物车</router-link>
          <router-link to="/orders">订单</router-link>
          <router-link to="/admin" v-if="isLogin && user.role === 1">后台管理</router-link>
          <div v-if="isLogin" class="user-dropdown">
            <span class="user-info" @click="toggleDropdown">
              {{ user.username }}
              <span class="dropdown-icon">▼</span>
            </span>
            <div class="dropdown-menu" :class="{ 'show': showDropdown }" @click.stop>
              <router-link to="/user/profile">个人信息</router-link>
              <router-link to="/message">我的消息</router-link>
              <a href="#" @click.prevent="logout">注销</a>
            </div>
          </div>
          <router-link to="/login" v-else>登录/注册</router-link>
        </nav>
      </div>
    </header>
    <main class="main" :class="{ 'message-main': isMessagePage }">
      <div class="container" v-if="!isMessagePage">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
      <router-view v-else />
    </main>
    <footer class="footer" v-if="!isMessagePage">
      <div class="container">
        <p>© 2026 特色农产品销售平台</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const isLogin = ref(false)
    const user = ref({})
    const showDropdown = ref(false)
    const isMessagePage = ref(false)
    let dropdownTimer = null

    const checkMessagePage = () => {
      isMessagePage.value = router.currentRoute.value.path === '/message'
    }

    onMounted(() => {
      const userInfo = localStorage.getItem('user')
      if (userInfo) {
        user.value = JSON.parse(userInfo)
        isLogin.value = true
      }
      document.addEventListener('click', handleClickOutside)
      checkMessagePage()
      router.afterEach(checkMessagePage)
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
      if (dropdownTimer) {
        clearTimeout(dropdownTimer)
      }
    })

    const toggleDropdown = () => {
      showDropdown.value = !showDropdown.value
      if (showDropdown.value) {
        startDropdownTimer()
      } else {
        clearDropdownTimer()
      }
    }

    const startDropdownTimer = () => {
      clearDropdownTimer()
      dropdownTimer = setTimeout(() => {
        showDropdown.value = false
      }, 10000)
    }

    const clearDropdownTimer = () => {
      if (dropdownTimer) {
        clearTimeout(dropdownTimer)
        dropdownTimer = null
      }
    }

    const handleClickOutside = (event) => {
      const dropdown = document.querySelector('.user-dropdown')
      if (dropdown && !dropdown.contains(event.target)) {
        showDropdown.value = false
        clearDropdownTimer()
      }
    }

    const logout = () => {
      localStorage.removeItem('user')
      isLogin.value = false
      user.value = {}
      showDropdown.value = false
      alert('注销成功！')
      router.push('/')
    }

    return {
      isLogin,
      user,
      showDropdown,
      isMessagePage,
      toggleDropdown,
      logout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

body {
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
}

#app {
  width: 100%;
  height: 100%;
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header {
  background-color: #4CAF50;
  color: white;
  padding: 20px 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  min-height: 80px;
  display: flex;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  float: left;
  line-height: 40px;
}

.nav {
  float: right;
  line-height: 40px;
}

.nav a {
  color: white;
  text-decoration: none;
  margin-left: 20px;
  font-size: 16px;
  display: inline-block;
  padding: 0 5px;
}

.nav .user-info {
  margin-left: 20px;
  font-size: 16px;
  display: inline-block;
  padding: 0 5px;
  cursor: pointer;
  position: relative;
}

.user-info:hover {
  text-decoration: underline;
}

.dropdown-icon {
  font-size: 12px;
  margin-left: 5px;
  transition: transform 0.3s ease;
}

.user-dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: white;
  min-width: 150px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 1000;
  margin-top: 5px;
  border-radius: 4px;
  overflow: hidden;
  opacity: 0;
  transform: translateY(-10px);
  transition: opacity 0.3s ease, transform 0.3s ease;
  pointer-events: none;
}

.dropdown-menu.show {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.dropdown-menu a {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  font-size: 14px;
  margin-left: 0;
  transition: background-color 0.2s ease;
}

.dropdown-menu a:hover {
  background-color: #f5f5f5;
}

.dropdown-menu router-link {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  font-size: 14px;
  margin-left: 0;
  transition: background-color 0.2s ease;
}

.dropdown-menu router-link:hover {
  background-color: #f5f5f5;
}

.nav a:hover {
  text-decoration: underline;
}

.main {
  padding: 40px 0;
  min-height: 600px;
}

.main.message-main {
  padding: 0;
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  padding: 20px 0;
  margin-top: 40px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.clearfix::after {
  content: '';
  display: table;
  clear: both;
}
</style>