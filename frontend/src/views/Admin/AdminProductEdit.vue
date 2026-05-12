<template>
  <div class="admin-product-edit">
    <h2>{{ editingProduct ? '编辑商品' : '添加商品' }}</h2>
    <div class="product-form">
      <form @submit.prevent="saveProduct">
        <div class="form-group">
          <label>商品名称</label>
          <input type="text" v-model="form.name" required>
        </div>
        <div class="form-group">
          <label>商品描述</label>
          <textarea v-model="form.description" required></textarea>
        </div>
        <div class="form-group">
          <label>价格</label>
          <input type="number" v-model.number="form.price" required>
        </div>
        <div class="form-group">
          <label>库存</label>
          <input type="number" v-model.number="form.stock" required>
        </div>
        <div class="form-group">
          <label>图片</label>
          <input type="file" accept="image/*" @change="handleImageUpload">
          <div v-if="imagePreview" class="image-preview">
            <img :src="imagePreview" alt="预览" style="max-width: 200px; max-height: 200px;">
          </div>
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="form.category" required>
            <option value="">请选择分类</option>
            <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="form.status">
            <option value="1">上架</option>
            <option value="0">下架</option>
          </select>
        </div>
        <div class="form-actions">
          <button type="submit" class="save-btn">保存</button>
          <button type="button" @click="cancelForm" class="cancel-btn">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productApi } from '../../api'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'AdminProductEdit',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const editingProduct = ref(null)
    const form = ref({
      name: '',
      description: '',
      price: 0,
      stock: 0,
      image: '',
      category: '',
      status: 1
    })
    const categories = ref(['蔬菜', '水果', '肉禽蛋', '粮油', '干货', '饮品', '调味品', '坚果'])
    const imagePreview = ref('')

    onMounted(() => {
      const productId = route.params.id
      if (productId) {
        loadProduct(productId)
      }
    })

    const loadProduct = async (id) => {
      try {
        const product = await productApi.get(id)
        if (product) {
          editingProduct.value = product
          form.value = { ...product }
          imagePreview.value = product.image
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        alert('加载商品失败，请稍后重试')
      }
    }

    const saveProduct = async () => {
      try {
        if (editingProduct.value) {
          await productApi.update({
            ...form.value,
            id: editingProduct.value.id
          })
          alert('商品更新成功！')
        } else {
          await productApi.add(form.value)
          alert('商品添加成功！')
        }
        router.push('/admin/products')
      } catch (error) {
        console.error('保存商品失败:', error)
        alert('保存失败，请稍后重试')
      }
    }

    const cancelForm = () => {
      router.push('/admin/products')
    }

    const handleImageUpload = (event) => {
      const file = event.target.files[0]
      if (file) {
        // 检查文件类型
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif']
        if (!allowedTypes.includes(file.type)) {
          alert('只支持 JPG、PNG、GIF 格式的图片')
          return
        }
        
        // 生成预览
        const reader = new FileReader()
        reader.onload = (e) => {
          imagePreview.value = e.target.result
          // 这里可以添加上传到服务器的逻辑
          // 暂时使用 base64 作为图片路径
          form.value.image = e.target.result
        }
        reader.readAsDataURL(file)
      }
    }

    return {
      editingProduct,
      form,
      categories,
      imagePreview,
      saveProduct,
      cancelForm,
      handleImageUpload
    }
  }
}
</script>

<style scoped>
.admin-product-edit {
  padding: 20px;
}

.product-form {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.image-preview {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
}

.image-preview img {
  border-radius: 4px;
}

.form-actions {
  margin-top: 20px;
}

.save-btn,
.cancel-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-right: 10px;
  transition: background-color 0.2s ease;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.save-btn:hover {
  background-color: #45a049;
}

.save-btn:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.cancel-btn {
  background-color: #999;
  color: white;
}

.cancel-btn:hover {
  background-color: #757575;
}

.cancel-btn:active {
  background-color: #616161;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
</style>