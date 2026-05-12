<template>
  <div class="admin-products">
    <h2>商品管理</h2>
    <div class="admin-actions">
      <button @click="addProduct" class="add-btn">添加商品</button>
    </div>
    
    <div class="product-list">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>价格</th>
            <th>库存</th>
            <th>分类</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>{{ product.id }}</td>
            <td>{{ product.name }}</td>
            <td>{{ product.price }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.category }}</td>
            <td>{{ product.status === 1 ? '上架' : '下架' }}</td>
            <td>
              <button @click="editProduct(product)" class="edit-btn">编辑</button>
              <button @click="deleteProduct(product.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productApi } from '../../api'
import { useRouter } from 'vue-router'

export default {
  name: 'AdminProducts',
  setup() {
    const router = useRouter()
    const products = ref([])

    const loadProducts = async () => {
      try {
        const response = await productApi.list()
        products.value = response
      } catch (error) {
        console.error('加载商品失败:', error)
        alert('加载商品失败，请稍后重试')
      }
    }

    const addProduct = () => {
      router.push('/admin/products/add')
    }

    const editProduct = (product) => {
      router.push(`/admin/products/edit/${product.id}`)
    }

    const deleteProduct = async (id) => {
      if (confirm('确定要删除这个商品吗？')) {
        try {
          await productApi.delete(id)
          alert('商品删除成功！')
          loadProducts()
        } catch (error) {
          console.error('删除商品失败:', error)
          alert('删除失败，请稍后重试')
        }
      }
    }

    onMounted(() => {
      loadProducts()
    })

    return {
      products,
      addProduct,
      editProduct,
      deleteProduct
    }
  }
}
</script>

<style scoped>
.admin-products {
  padding: 20px;
}

.admin-actions {
  margin-bottom: 20px;
}

.add-btn {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.add-btn:hover {
  background-color: #45a049;
}

.add-btn:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.product-list {
  margin-bottom: 20px;
}

.product-list table {
  width: 100%;
  border-collapse: collapse;
}

.product-list th,
.product-list td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.product-list th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.edit-btn,
.delete-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
  transition: background-color 0.2s ease;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976D2;
}

.edit-btn:active {
  background-color: #1565C0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #D32F2F;
}

.delete-btn:active {
  background-color: #B71C1C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
</style>
