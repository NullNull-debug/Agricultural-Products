<template>
  <div class="orders">
    <h2>我的订单</h2>
    <div v-if="orders.length > 0" class="order-list">
      <div class="order-item" v-for="order in orders" :key="order.id">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-status">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-content">
          <div class="order-items">
            <!-- 这里可以展示订单中的商品 -->
          </div>
          <div class="order-total">
            <span>总计：</span>
            <span class="total-price">¥{{ order.totalAmount }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-time">{{ formatDate(order.createTime) }}</span>
          <button v-if="order.status === 0" class="pay-btn" @click="payOrder(order.id)">去支付</button>
          <button v-else-if="order.status === 2" class="confirm-btn" @click="confirmReceive(order.id)">确认收货</button>
        </div>
      </div>
    </div>
    <div v-else class="empty-orders">
      <p>暂无订单</p>
      <router-link to="/products" class="go-shopping">去购物</router-link>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onActivated } from 'vue'
import { orderApi } from '../api'
import { useRouter } from 'vue-router'

export default {
  name: 'Orders',
  setup() {
    const router = useRouter()
    const orders = ref([])

    const loadOrders = async () => {
      try {
        const userInfo = localStorage.getItem('user')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          const response = await orderApi.listByUser(user.id)
          orders.value = response
        }
      } catch (error) {
        console.error('获取订单列表失败:', error)
      }
    }

    onMounted(async () => {
      await loadOrders()
    })

    onActivated(async () => {
      await loadOrders()
    })

    const getStatusText = (status) => {
      const statusMap = {
        0: '未支付',
        1: '已支付',
        2: '已发货',
        3: '已评价',
        4: '已取消'
      }
      return statusMap[status] || '未知状态'
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    const payOrder = (orderId) => {
      // 跳转到支付页面
      router.push(`/order/pay/${orderId}`)
    }

    const confirmReceive = async (orderId) => {
      // 确认收货，直接跳转到评价页面
      router.push(`/product/review/1?orderId=${orderId}`) // 假设商品ID为1，实际应该从订单详情中获取
    }

    return {
      orders,
      getStatusText,
      formatDate,
      payOrder,
      confirmReceive
    }
  }
}
</script>

<style scoped>
.orders {
  padding: 20px 0;
}

.orders h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
  color: #e74c3c;
}

.order-content {
  padding: 20px;
}

.order-items {
  margin-bottom: 20px;
}

.order-total {
  text-align: right;
  font-size: 16px;
  font-weight: bold;
}

.total-price {
  color: #e74c3c;
  margin-left: 10px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

.order-time {
  font-size: 14px;
  color: #666;
}

.pay-btn,
.confirm-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.pay-btn {
  background-color: #f39c12;
  color: white;
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
}

.pay-btn:hover {
  background-color: #e67e22;
}

.confirm-btn:hover {
  background-color: #45a049;
}

.empty-orders {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 100px 0;
  text-align: center;
}

.empty-orders p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.go-shopping {
  display: inline-block;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 16px;
}

.go-shopping:hover {
  background-color: #45a049;
}
</style>