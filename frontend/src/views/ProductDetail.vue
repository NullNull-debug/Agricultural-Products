<template>
  <div class="product-detail">
    <div v-if="product" class="detail-content">
      <div class="product-image">
        <img :src="product.image" :alt="product.name">
      </div>
      <div class="product-info">
        <h1>{{ product.name }}</h1>
        <p class="product-price">¥{{ product.price }}</p>
        <p class="product-stock">库存: {{ product.stock }}</p>
        <p class="product-category">分类: {{ product.category }}</p>
        <div class="product-description">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>
        <div class="buy-section">
          <div class="quantity">
            <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
            <input type="number" v-model="quantity" min="1" :max="product.stock">
            <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
          </div>
          <button class="add-to-cart" @click="addToCart">加入购物车</button>
          <button class="buy-now" @click="buyNow">立即购买</button>
        </div>
      </div>
    </div>
    <div v-else class="loading">加载中...</div>
    
    <!-- 商品评价部分 -->
    <div v-if="product" class="product-reviews">
      <h2>商品评价</h2>
      <div v-if="reviews.length > 0" class="review-list">
        <div class="review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <div class="review-rating">
              <span 
                v-for="star in 5" 
                :key="star"
                class="star" 
                :class="{ 'active': review.rating >= star }"
              >★</span>
            </div>
            <div class="review-date">{{ formatDate(review.createTime) }}</div>
          </div>
          <div class="review-content">{{ review.content }}</div>
        </div>
      </div>
      <div v-else class="no-reviews">
        <p>暂无评价</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productApi, reviewApi } from '../api'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const product = ref(null)
    const quantity = ref(1)
    const reviews = ref([])

    onMounted(async () => {
      try {
        const response = await productApi.get(route.params.id)
        product.value = response
        // 加载商品评价
        await loadReviews(route.params.id)
      } catch (error) {
        console.error('获取商品详情失败:', error)
      }
    })

    const loadReviews = async (productId) => {
      try {
        const response = await reviewApi.listByProduct(productId)
        reviews.value = response
      } catch (error) {
        console.error('加载商品评价失败:', error)
      }
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }

    const increaseQuantity = () => {
      if (product.value && quantity.value < product.value.stock) {
        quantity.value++
      }
    }

    const addToCart = () => {
      // 加入购物车逻辑
      let cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const existingItem = cart.find(item => item.id === product.value.id)
      if (existingItem) {
        existingItem.quantity += quantity.value
      } else {
        cart.push({
          id: product.value.id,
          name: product.value.name,
          price: product.value.price,
          image: product.value.image,
          quantity: quantity.value
        })
      }
      localStorage.setItem('cart', JSON.stringify(cart))
      alert('加入购物车成功！')
    }

    const buyNow = () => {
      // 立即购买逻辑
      const orderItem = {
        id: product.value.id,
        name: product.value.name,
        price: product.value.price,
        image: product.value.image,
        quantity: quantity.value
      }
      localStorage.setItem('buyNowItem', JSON.stringify(orderItem))
      // 跳转到订单确认页面
      router.push('/order/confirm')
    }

    return {
      product,
      quantity,
      reviews,
      decreaseQuantity,
      increaseQuantity,
      addToCart,
      buyNow,
      loadReviews,
      formatDate
    }
  }
}
</script>

<style scoped>
.product-detail {
  padding: 20px 0;
}

.detail-content {
  display: flex;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.product-image {
  flex: 1;
  max-width: 500px;
  padding: 20px;
}

.product-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.product-info {
  flex: 1;
  padding: 40px;
}

.product-info h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.product-price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 10px;
}

.product-stock,
.product-category {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.product-description {
  margin-top: 30px;
  margin-bottom: 30px;
}

.product-description h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.product-description p {
  font-size: 16px;
  line-height: 1.6;
  color: #666;
}

.buy-section {
  display: flex;
  align-items: center;
}

.quantity {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
}

.quantity input {
  width: 60px;
  height: 30px;
  border: 1px solid #ddd;
  text-align: center;
  font-size: 16px;
}

.add-to-cart,
.buy-now {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-right: 10px;
}

.add-to-cart {
  background-color: #f39c12;
  color: white;
}

.buy-now {
  background-color: #e74c3c;
  color: white;
}

.add-to-cart:hover {
  background-color: #e67e22;
}

.buy-now:hover {
  background-color: #c0392b;
}

.loading {
  text-align: center;
  padding: 100px 0;
  font-size: 18px;
  color: #666;
}

.product-reviews {
  margin-top: 40px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.product-reviews h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-rating {
  display: flex;
  gap: 5px;
}

.review-rating .star {
  font-size: 16px;
  color: #ddd;
}

.review-rating .star.active {
  color: #f39c12;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.no-reviews {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 16px;
}
</style>