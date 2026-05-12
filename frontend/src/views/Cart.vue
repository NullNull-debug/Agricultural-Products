<template>
  <div class="cart">
    <h2>购物车</h2>
    <div v-if="cart.length > 0" class="cart-content">
      <table class="cart-table">
        <thead>
          <tr>
            <th>商品图片</th>
            <th>商品名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in cart" :key="item.id">
            <td><img :src="item.image" :alt="item.name" class="product-image"></td>
            <td>{{ item.name }}</td>
            <td>¥{{ item.price }}</td>
            <td>
              <div class="quantity">
                <button @click="decreaseQuantity(index)" :disabled="item.quantity <= 1">-</button>
                <input type="number" v-model="item.quantity" min="1" @change="updateCart">
                <button @click="increaseQuantity(index)">+</button>
              </div>
            </td>
            <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
            <td><button class="delete-btn" @click="removeItem(index)">删除</button></td>
          </tr>
        </tbody>
      </table>
      <div class="cart-summary">
        <div class="total">
          <span>总计：</span>
          <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="checkout">去结算</button>
      </div>
    </div>
    <div v-else class="empty-cart">
      <p>购物车为空</p>
      <router-link to="/products" class="go-shopping">去购物</router-link>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'

export default {
  name: 'Cart',
  setup() {
    const cart = ref([])

    onMounted(() => {
      const savedCart = localStorage.getItem('cart')
      if (savedCart) {
        cart.value = JSON.parse(savedCart)
      }
    })

    const totalPrice = computed(() => {
      return cart.value.reduce((total, item) => {
        return total + item.price * item.quantity
      }, 0)
    })

    const updateCart = () => {
      localStorage.setItem('cart', JSON.stringify(cart.value))
    }

    const decreaseQuantity = (index) => {
      if (cart.value[index].quantity > 1) {
        cart.value[index].quantity--
        updateCart()
      }
    }

    const increaseQuantity = (index) => {
      cart.value[index].quantity++
      updateCart()
    }

    const removeItem = (index) => {
      cart.value.splice(index, 1)
      updateCart()
    }

    const checkout = () => {
      // 结算逻辑
      alert('跳转到结算页面')
    }

    return {
      cart,
      totalPrice,
      updateCart,
      decreaseQuantity,
      increaseQuantity,
      removeItem,
      checkout
    }
  }
}
</script>

<style scoped>
.cart {
  padding: 20px 0;
}

.cart h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.cart-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.cart-table {
  width: 100%;
  border-collapse: collapse;
}

.cart-table th,
.cart-table td {
  padding: 15px;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.cart-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.quantity {
  display: flex;
  align-items: center;
  justify-content: center;
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

.delete-btn {
  padding: 5px 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f5f5f5;
}

.total {
  font-size: 18px;
  font-weight: bold;
}

.total-price {
  color: #e74c3c;
  font-size: 24px;
  margin-left: 10px;
}

.checkout-btn {
  padding: 10px 30px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.checkout-btn:hover {
  background-color: #45a049;
}

.empty-cart {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 100px 0;
  text-align: center;
}

.empty-cart p {
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