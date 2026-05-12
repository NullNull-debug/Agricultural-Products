<template>
  <div class="admin-announcements">
    <h2>公告管理</h2>
    <div class="admin-actions">
      <button @click="showAddForm = true" class="add-btn">发布公告</button>
    </div>
    
    <div class="announcement-list">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="announcement in announcements" :key="announcement.id">
            <td>{{ announcement.id }}</td>
            <td>{{ announcement.title }}</td>
            <td>{{ announcement.status === 1 ? '已发布' : '未发布' }}</td>
            <td>{{ formatDate(announcement.createTime) }}</td>
            <td>
              <button @click="editAnnouncement(announcement)" class="edit-btn">编辑</button>
              <button @click="deleteAnnouncement(announcement.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 添加/编辑公告表单 -->
    <div v-if="showAddForm || editingAnnouncement" class="announcement-form">
      <h3>{{ editingAnnouncement ? '编辑公告' : '发布公告' }}</h3>
      <form @submit.prevent="saveAnnouncement">
        <div class="form-group">
          <label>公告标题</label>
          <input type="text" v-model="form.title" required>
        </div>
        <div class="form-group">
          <label>公告内容</label>
          <textarea v-model="form.content" rows="10" required></textarea>
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="form.status">
            <option value="1">已发布</option>
            <option value="0">未发布</option>
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
import { announcementApi } from '../../api'

export default {
  name: 'AdminAnnouncements',
  setup() {
    const announcements = ref([])
    const showAddForm = ref(false)
    const editingAnnouncement = ref(null)
    const form = ref({
      title: '',
      content: '',
      status: 1
    })

    const loadAnnouncements = async () => {
      try {
        const response = await announcementApi.list()
        announcements.value = response
      } catch (error) {
        console.error('加载公告失败:', error)
      }
    }

    const saveAnnouncement = async () => {
      try {
        if (editingAnnouncement.value) {
          await announcementApi.update({
            ...form.value,
            id: editingAnnouncement.value.id
          })
          alert('公告更新成功！')
        } else {
          await announcementApi.add(form.value)
          alert('公告发布成功！')
        }
        loadAnnouncements()
        cancelForm()
      } catch (error) {
        console.error('保存公告失败:', error)
        alert('保存失败，请稍后重试')
      }
    }

    const editAnnouncement = (announcement) => {
      editingAnnouncement.value = announcement
      form.value = { ...announcement }
    }

    const deleteAnnouncement = async (id) => {
      if (confirm('确定要删除这个公告吗？')) {
        try {
          await announcementApi.delete(id)
          alert('公告删除成功！')
          loadAnnouncements()
        } catch (error) {
          console.error('删除公告失败:', error)
          alert('删除失败，请稍后重试')
        }
      }
    }

    const cancelForm = () => {
      showAddForm.value = false
      editingAnnouncement.value = null
      form.value = {
        title: '',
        content: '',
        status: 1
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    onMounted(() => {
      loadAnnouncements()
      // 重置表单状态，确保导航时表单被隐藏
      showAddForm.value = false
      editingAnnouncement.value = null
    })

    return {
      announcements,
      showAddForm,
      editingAnnouncement,
      form,
      saveAnnouncement,
      editAnnouncement,
      deleteAnnouncement,
      cancelForm,
      formatDate
    }
  }
}
</script>

<style scoped>
.admin-announcements {
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
}

.add-btn:hover {
  background-color: #45a049;
}

.add-btn:active {
  background-color: #388E3C;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.announcement-list {
  margin-bottom: 20px;
}

.announcement-list table {
  width: 100%;
  border-collapse: collapse;
}

.announcement-list th,
.announcement-list td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.announcement-list th {
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

.announcement-form {
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

.form-group textarea {
  resize: vertical;
  min-height: 200px;
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
