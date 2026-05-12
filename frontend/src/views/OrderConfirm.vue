<template>
  <div class="order-confirm">
    <h2>订单确认</h2>
    <div v-if="orderItem" class="order-content">
      <div class="order-item">
        <div class="item-image">
          <img :src="orderItem.image" :alt="orderItem.name">
        </div>
        <div class="item-info">
          <h3>{{ orderItem.name }}</h3>
          <p class="item-price">¥{{ orderItem.price }}</p>
          <p class="item-quantity">数量: {{ orderItem.quantity }}</p>
        </div>
      </div>
      <div class="order-summary">
        <h3>订单汇总</h3>
        <div class="summary-row">
          <span>商品金额:</span>
          <span>¥{{ orderItem.price * orderItem.quantity }}</span>
        </div>
        <div class="summary-row">
          <span>运费:</span>
          <span>¥0.00</span>
        </div>
        <div class="summary-row total">
          <span>总计:</span>
          <span>¥{{ orderItem.price * orderItem.quantity }}</span>
        </div>
      </div>
      <div class="order-actions">
        <button class="submit-order" @click="submitOrder">提交订单</button>
        <button class="cancel-order" @click="cancelOrder">取消</button>
      </div>
    </div>
    <div v-else class="empty-order">
      <p>订单信息为空</p>
      <button @click="goBack">返回商品页面</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api'

export default {
  name: 'OrderConfirm',
  setup() {
    const router = useRouter()
    const orderItem = ref(null)

    onMounted(() => {
      const buyNowItem = localStorage.getItem('buyNowItem')
      if (buyNowItem) {
        orderItem.value = JSON.parse(buyNowItem)
      }
    })

    const submitOrder = async () => {
      if (!orderItem.value) {
        alert('订单信息为空')
        return
      }

      try {
        // 获取用户信息（这里简化处理，实际应该从登录状态获取）
        const userInfo = localStorage.getItem('user')
        if (!userInfo) {
          alert('请先登录')
          router.push('/login')
          return
        }
        const user = JSON.parse(userInfo)

        // 创建订单
        const orderData = {
          userId: user.id,
          totalAmount: orderItem.value.price * orderItem.value.quantity,
          status: 0, // 待付款
          orderItems: [{
            productId: orderItem.value.id,
            quantity: orderItem.value.quantity,
            price: orderItem.value.price
          }]
        }

        await orderApi.add(orderData)
        alert('订单提交成功！')
        localStorage.removeItem('buyNowItem')
        router.push('/orders')
      } catch (error) {
        console.error('提交订单失败:', error)
        alert('提交订单失败，请稍后重试')
      }
    }

    const cancelOrder = () => {
      localStorage.removeItem('buyNowItem')
      router.push('/')
    }

    const goBack = () => {
      router.push('/')
    }

    return {
      orderItem,
      submitOrder,
      cancelOrder,
      goBack
    }
  }
}
</script>

<style scoped>
.order-confirm {
  padding: 20px 0;
}

.order-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.order-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.item-image {
  width: 100px;
  height: 100px;
  margin-right: 20px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.item-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 5px;
}

.item-quantity {
  font-size: 14px;
  color: #666;
}

.order-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.order-summary h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #333;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.summary-row.total {
  font-weight: bold;
  font-size: 16px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ddd;
}

.order-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.submit-order,
.cancel-order {
  padding: 10px 30px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-left: 10px;
  transition: background-color 0.2s ease;
}

.submit-order {
  background-color: #4CAF50;
  color: white;
}

.submit-order:hover {
  background-color: #45a049;
}

.submit-order:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.cancel-order {
  background-color: #999;
  color: white;
}

.cancel-order:hover {
  background-color: #757575;
}

.cancel-order:active {
  background-color: #616161;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.empty-order {
  text-align: center;
  padding: 100px 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.empty-order p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.empty-order button {
  padding: 10px 30px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.empty-order button:hover {
  background-color: #45a049;
}

.empty-order button:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
</style>