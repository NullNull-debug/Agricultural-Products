<template>
  <div class="payment">
    <h2>订单支付</h2>
    <div v-if="order" class="payment-content">
      <div class="order-info">
        <h3>订单信息</h3>
        <div class="info-row">
          <span>订单号:</span>
          <span>{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span>订单金额:</span>
          <span class="amount">¥{{ order.totalAmount }}</span>
        </div>
        <div class="info-row">
          <span>下单时间:</span>
          <span>{{ formatDate(order.createTime) }}</span>
        </div>
      </div>
      <div class="payment-methods">
        <h3>支付方式</h3>
        <div class="method-item" v-for="method in paymentMethods" :key="method.id" @click="selectPaymentMethod(method)">
          <div class="method-info">
            <div class="method-name">{{ method.name }}</div>
            <div class="method-desc">{{ method.description }}</div>
          </div>
          <div class="method-radio" :class="{ 'selected': selectedMethod.id === method.id }">
            <div class="radio-inner"></div>
          </div>
        </div>
      </div>
      <div class="payment-actions">
        <button class="cancel-btn" @click="cancelPayment">取消支付</button>
        <button class="pay-btn" @click="submitPayment">确认支付</button>
      </div>
    </div>
    <div v-else class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { orderApi } from '../api'

export default {
  name: 'Payment',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const order = ref(null)
    const selectedMethod = ref({
      id: 'wechat',
      name: '微信支付',
      description: '使用微信扫码支付'
    })
    const paymentMethods = ref([
      {
        id: 'wechat',
        name: '微信支付',
        description: '使用微信扫码支付'
      },
      {
        id: 'alipay',
        name: '支付宝',
        description: '使用支付宝扫码支付'
      },
      {
        id: 'card',
        name: '银行卡',
        description: '使用银行卡支付'
      }
    ])

    onMounted(async () => {
      const orderId = route.params.id
      if (orderId) {
        await loadOrder(orderId)
      }
    })

    const loadOrder = async (id) => {
      try {
        const response = await orderApi.get(id)
        if (response) {
          order.value = response
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        alert('加载订单失败，请稍后重试')
        router.push('/orders')
      }
    }

    const selectPaymentMethod = (method) => {
      selectedMethod.value = method
    }

    const submitPayment = async () => {
      if (!order.value) {
        alert('订单信息有误')
        return
      }

      try {
        // 模拟支付过程
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 更新订单状态为已支付
        await orderApi.update({
          ...order.value,
          status: 1 // 已支付
        })
        
        alert('支付成功！')
        router.push('/orders')
      } catch (error) {
        console.error('支付失败:', error)
        alert('支付失败，请稍后重试')
      }
    }

    const cancelPayment = () => {
      router.push('/orders')
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    return {
      order,
      selectedMethod,
      paymentMethods,
      selectPaymentMethod,
      submitPayment,
      cancelPayment,
      formatDate
    }
  }
}
</script>

<style scoped>
.payment {
  padding: 20px 0;
}

.payment-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.order-info {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.order-info h3,
.payment-methods h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #333;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.payment-methods {
  margin-bottom: 30px;
}

.method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.method-item:hover {
  border-color: #4CAF50;
}

.method-item.selected {
  border-color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.05);
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.method-desc {
  font-size: 14px;
  color: #666;
}

.method-radio {
  width: 20px;
  height: 20px;
  border: 2px solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.method-radio.selected {
  border-color: #4CAF50;
}

.method-radio.selected .radio-inner {
  width: 10px;
  height: 10px;
  background-color: #4CAF50;
  border-radius: 50%;
}

.payment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn,
.pay-btn {
  padding: 10px 30px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-btn {
  background-color: #999;
  color: white;
}

.pay-btn {
  background-color: #4CAF50;
  color: white;
}

.cancel-btn:hover {
  background-color: #757575;
}

.pay-btn:hover {
  background-color: #45a049;
}

.cancel-btn:active {
  background-color: #616161;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.pay-btn:active {
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