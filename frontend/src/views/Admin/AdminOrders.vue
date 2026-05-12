<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    <div class="admin-actions">
      <div class="search-bar">
        <input type="text" v-model="searchKeyword" placeholder="按订单号搜索" class="search-input">
        <button @click="searchOrders" class="search-btn">搜索</button>
      </div>
    </div>
    
    <div class="order-list">
      <table>
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户ID</th>
            <th>总金额</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.userId }}</td>
            <td>{{ order.totalAmount }}</td>
            <td>
              <select v-model="order.status" @change="updateOrderStatus(order)" class="status-select">
                <option value="0">未支付</option>
                <option value="1">已支付</option>
                <option value="2">已发货</option>
                <option value="3">已评价</option>
                <option value="4">已取消</option>
              </select>
            </td>
            <td>{{ formatDate(order.createTime) }}</td>
            <td>
              <button @click="viewOrderDetail(order)" class="view-btn">查看详情</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 订单详情弹窗 -->
    <div v-if="selectedOrder" class="order-detail-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button @click="selectedOrder = null" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="order-info">
            <p><strong>订单号:</strong> {{ selectedOrder.orderNo }}</p>
            <p><strong>用户ID:</strong> {{ selectedOrder.userId }}</p>
            <p><strong>总金额:</strong> {{ selectedOrder.totalAmount }}</p>
            <p><strong>状态:</strong> 
              <select v-model="selectedOrder.status" @change="updateOrderStatus(selectedOrder)" class="status-select">
                <option value="0">未支付</option>
                <option value="1">已支付</option>
                <option value="2">已发货</option>
                <option value="3">已评价</option>
                <option value="4">已取消</option>
              </select>
            </p>
            <p><strong>创建时间:</strong> {{ formatDate(selectedOrder.createTime) }}</p>
            <p><strong>更新时间:</strong> {{ formatDate(selectedOrder.updateTime) }}</p>
          </div>
          <div class="order-items">
            <h4>商品列表</h4>
            <table>
              <thead>
                <tr>
                  <th>商品ID</th>
                  <th>数量</th>
                  <th>单价</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in orderItems" :key="item.id">
                  <td>{{ item.productId }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ item.price }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="selectedOrder = null" class="close-btn">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'

export default {
  name: 'AdminOrders',
  setup() {
    const orders = ref([])
    const selectedOrder = ref(null)
    const orderItems = ref([])
    const searchKeyword = ref('')

    const loadOrders = async () => {
      try {
        const response = await orderApi.list()
        orders.value = response
      } catch (error) {
        console.error('加载订单失败:', error)
      }
    }

    const searchOrders = async () => {
      try {
        const response = await orderApi.list()
        if (searchKeyword.value) {
          orders.value = response.filter(order => 
            order.orderNo.includes(searchKeyword.value)
          )
        } else {
          orders.value = response
        }
      } catch (error) {
        console.error('搜索订单失败:', error)
      }
    }

    const viewOrderDetail = async (order) => {
      selectedOrder.value = order
      // 这里应该调用订单详情API获取订单项，暂时使用模拟数据
      orderItems.value = [
        { id: 1, orderId: order.id, productId: 1, quantity: 1, price: 99.9 },
        { id: 2, orderId: order.id, productId: 2, quantity: 1, price: 68.0 }
      ]
    }

    const updateOrderStatus = async (order) => {
      try {
        await orderApi.update(order)
        alert('订单状态更新成功！')
        loadOrders()
      } catch (error) {
        console.error('更新订单状态失败:', error)
        alert('更新订单状态失败，请稍后重试')
        // 恢复原来的状态
        loadOrders()
      }
    }

    const getOrderStatusText = (status) => {
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
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    onMounted(() => {
      loadOrders()
    })

    return {
      orders,
      selectedOrder,
      orderItems,
      searchKeyword,
      searchOrders,
      viewOrderDetail,
      updateOrderStatus,
      getOrderStatusText,
      formatDate
    }
  }
}
</script>

<style scoped>
.admin-orders {
  padding: 20px;
}

.admin-actions {
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.search-btn {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.search-btn:hover {
  background-color: #45a049;
}

.search-btn:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.2s ease;
}

.search-input:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.order-list {
  margin-bottom: 20px;
}

.order-list table {
  width: 100%;
  border-collapse: collapse;
}

.order-list th,
.order-list td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.order-list th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.view-btn {
  padding: 5px 10px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.view-btn:hover {
  background-color: #1976D2;
}

.view-btn:active {
  background-color: #1565C0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.status-select {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.status-select:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.order-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 4px;
  width: 80%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #666;
}

.close-btn:active {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.order-info {
  margin-bottom: 20px;
}

.order-info p {
  margin: 10px 0;
}

.order-items {
  margin-top: 20px;
}

.order-items table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.order-items th,
.order-items td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.order-items th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #ddd;
  text-align: right;
}

.modal-footer .close-btn {
  padding: 10px 20px;
  background-color: #999;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.modal-footer .close-btn:hover {
  background-color: #757575;
}

.modal-footer .close-btn:active {
  background-color: #616161;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
</style>
