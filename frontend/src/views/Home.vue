<template>
  <div class="home">
    <div class="banner">
      <h1>欢迎来到特色农产品销售平台</h1>
      <p>新鲜、健康、绿色的农产品，从田间直达餐桌</p>
    </div>
    
    <div class="category">
      <h2>商品分类</h2>
      <div class="category-list">
        <div class="category-item" v-for="cat in categories" :key="cat">
          <router-link :to="`/products?category=${cat}`">
            <div class="category-icon">{{ cat.charAt(0) }}</div>
            <span>{{ cat }}</span>
          </router-link>
        </div>
      </div>
    </div>
    
    <div class="hot-products">
      <h2>热门商品</h2>
      <div class="product-list">
        <div class="product-item" v-for="product in hotProducts" :key="product.id">
          <router-link :to="`/product/${product.id}`">
            <div class="product-image">
              <img :src="product.image" :alt="product.name">
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-stock">库存: {{ product.stock }}</p>
            </div>
          </router-link>
        </div>
      </div>
    </div>
    
    <div class="announcements">
      <h2>最新公告</h2>
      <div class="announcement-list">
        <div class="announcement-item" v-for="announcement in announcements" :key="announcement.id">
          <div class="announcement-header">
            <h3>{{ announcement.title }}</h3>
            <span class="announcement-date">{{ formatDate(announcement.createTime) }}</span>
          </div>
          <p class="announcement-content">{{ truncateContent(announcement.content) }}</p>
        </div>
      </div>
      <div class="announcement-more">
        <a href="#" @click.prevent="viewMoreAnnouncements">查看更多公告</a>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productApi, announcementApi } from '../api'

export default {
  name: 'Home',
  setup() {
    const categories = ref(['蔬菜', '水果', '肉禽蛋', '粮油', '干货', '饮品'])
    const hotProducts = ref([])
    const announcements = ref([])

    onMounted(async () => {
      // 加载商品数据
      try {
        const response = await productApi.list()
        hotProducts.value = response.slice(0, 6)
      } catch (error) {
        console.error('获取商品列表失败:', error)
      }
      
      // 加载公告数据
      try {
        const response = await announcementApi.listPublished()
        announcements.value = response.slice(0, 3) // 只显示最新的3条公告
      } catch (error) {
        console.error('获取公告列表失败:', error)
      }
    })

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString()
    }

    const truncateContent = (content) => {
      if (!content) return ''
      return content.length > 100 ? content.substring(0, 100) + '...' : content
    }

    const viewMoreAnnouncements = () => {
      // 这里可以跳转到公告列表页面，暂时用alert代替
      alert('查看更多公告功能开发中...')
    }

    return {
      categories,
      hotProducts,
      announcements,
      formatDate,
      truncateContent,
      viewMoreAnnouncements
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.banner {
  background-color: #4CAF50;
  color: white;
  text-align: center;
  padding: 80px 0;
  margin-bottom: 40px;
  border-radius: 8px;
}

.banner h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.banner p {
  font-size: 20px;
  opacity: 0.9;
}

.category {
  margin-bottom: 40px;
}

.category h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.category-list {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.category-item {
  flex: 1;
  min-width: 150px;
  text-align: center;
  margin: 10px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.category-item:hover {
  transform: translateY(-5px);
}

.category-icon {
  width: 60px;
  height: 60px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 auto 10px;
}

.category-item a {
  text-decoration: none;
  color: #333;
}

.hot-products {
  margin-bottom: 40px;
}

.hot-products h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-item {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.product-item:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-info h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 5px;
}

.product-stock {
  font-size: 14px;
  color: #666;
}

.product-item a {
  text-decoration: none;
  color: inherit;
}

.announcements {
  margin-bottom: 40px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.announcements h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.announcement-list {
  margin-bottom: 20px;
}

.announcement-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.announcement-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.announcement-date {
  font-size: 14px;
  color: #999;
}

.announcement-content {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.announcement-more {
  text-align: right;
  margin-top: 10px;
}

.announcement-more a {
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
}

.announcement-more a:hover {
  text-decoration: underline;
}
</style>