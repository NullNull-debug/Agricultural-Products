<template>
  <div class="products">
    <div class="filter">
      <div class="category-filter">
        <label>分类：</label>
        <select v-model="selectedCategory" @change="filterProducts">
          <option value="">全部</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
      </div>
    </div>
    
    <div class="product-list">
      <div class="product-item" v-for="product in filteredProducts" :key="product.id">
        <router-link :to="`/product/${product.id}`">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
          </div>
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            <p class="product-price">¥{{ product.price }}</p>
            <p class="product-stock">库存: {{ product.stock }}</p>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { productApi } from '../api'

export default {
  name: 'Products',
  setup() {
    const categories = ref(['蔬菜', '水果', '肉禽蛋', '粮油', '干货', '饮品'])
    const products = ref([])
    const selectedCategory = ref('')

    onMounted(async () => {
      try {
        const response = await productApi.list()
        products.value = response
      } catch (error) {
        console.error('获取商品列表失败:', error)
      }
    })

    const filteredProducts = computed(() => {
      if (!selectedCategory.value) {
        return products.value
      }
      return products.value.filter(product => product.category === selectedCategory.value)
    })

    const filterProducts = () => {
      // 筛选逻辑已在computed中处理
    }

    return {
      categories,
      products,
      selectedCategory,
      filteredProducts,
      filterProducts
    }
  }
}
</script>

<style scoped>
.products {
  padding: 20px 0;
}

.filter {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.category-filter {
  margin-bottom: 10px;
}

.category-filter label {
  margin-right: 10px;
  font-weight: bold;
}

.category-filter select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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

.product-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.4;
  height: 42px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
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
</style>