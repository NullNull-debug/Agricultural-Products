<template>
  <div class="product-review">
    <h2>商品评价</h2>
    <div v-if="product" class="review-content">
      <div class="product-info">
        <img :src="product.image" :alt="product.name" class="product-image">
        <div class="product-details">
          <h3>{{ product.name }}</h3>
          <p class="product-price">¥{{ product.price }}</p>
        </div>
      </div>
      <form @submit.prevent="submitReview" class="review-form">
        <div class="form-group">
          <label>评分</label>
          <div class="rating">
            <span 
              v-for="star in 5" 
              :key="star"
              class="star" 
              :class="{ 'active': rating >= star }"
              @click="rating = star"
            >★</span>
          </div>
        </div>
        <div class="form-group">
          <label>评价内容</label>
          <textarea v-model="reviewContent" rows="5" placeholder="请输入您的评价..." required></textarea>
        </div>
        <div class="form-actions">
          <button type="button" class="skip-btn" @click="skipReview">跳过评价</button>
          <button type="submit" class="submit-btn">提交评价</button>
        </div>
      </form>
    </div>
    <div v-else class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productApi, reviewApi, orderApi } from '../api'

export default {
  name: 'ProductReview',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const product = ref(null)
    const rating = ref(5)
    const reviewContent = ref('')

    onMounted(async () => {
      const productId = route.params.id
      const orderId = route.params.orderId
      if (productId) {
        await loadProduct(productId)
      }
    })

    const loadProduct = async (id) => {
      try {
        const response = await productApi.get(id)
        if (response) {
          product.value = response
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        alert('加载商品失败，请稍后重试')
        router.push('/orders')
      }
    }

    const submitReview = async () => {
      if (!product.value) {
        alert('商品信息有误')
        return
      }

      try {
        const userInfo = localStorage.getItem('user')
        if (!userInfo) {
          alert('请先登录')
          router.push('/login')
          return
        }
        const user = JSON.parse(userInfo)

        // 提交评价
        await reviewApi.add({
          productId: product.value.id,
          userId: user.id,
          rating: rating.value,
          content: reviewContent.value
        })

        // 更新订单状态为已评价
        const orderId = route.params.orderId
        if (orderId) {
          await orderApi.update({
            id: orderId,
            status: 3 // 已评价
          })
        }

        alert('评价提交成功！')
        router.push('/orders')
      } catch (error) {
        console.error('提交评价失败:', error)
        alert('提交评价失败，请稍后重试')
      }
    }

    const skipReview = async () => {
      // 更新订单状态为已评价
      const orderId = route.params.orderId
      if (orderId) {
        try {
          await orderApi.update({
            id: orderId,
            status: 3 // 已评价
          })
        } catch (error) {
          console.error('更新订单状态失败:', error)
        }
      }
      router.push('/orders')
    }

    return {
      product,
      rating,
      reviewContent,
      submitReview,
      skipReview
    }
  }
}
</script>

<style scoped>
.product-review {
  padding: 20px 0;
}

.review-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.product-info {
  display: flex;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 20px;
}

.product-details {
  flex: 1;
}

.product-details h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.review-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: bold;
  color: #333;
}

.rating {
  display: flex;
  gap: 10px;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s ease;
}

.star.active {
  color: #f39c12;
}

.star:hover {
  color: #f39c12;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  resize: vertical;
  transition: border-color 0.2s ease;
}

textarea:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.skip-btn,
.submit-btn {
  padding: 10px 30px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.skip-btn {
  background-color: #999;
  color: white;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.skip-btn:hover {
  background-color: #757575;
}

.submit-btn:hover {
  background-color: #45a049;
}

.skip-btn:active {
  background-color: #616161;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.submit-btn:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.loading {
  text-align: center;
  padding: 100px 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  font-size: 18px;
  color: #666;
}
</style>