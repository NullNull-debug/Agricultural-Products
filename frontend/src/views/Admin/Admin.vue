<template>
  <div class="admin">
    <div class="admin-sidebar">
      <h3>后台管理</h3>
      <ul>
        <li>
          <router-link to="/admin/products">商品管理</router-link>
        </li>
        <li>
          <router-link to="/admin/announcements">公告管理</router-link>
        </li>
        <li>
          <router-link to="/admin/orders">订单管理</router-link>
        </li>
        <li>
          <router-link to="/admin/messages">客服消息</router-link>
        </li>
      </ul>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'Admin'
}
</script>

<style scoped>
.admin {
  display: flex;
  min-height: 600px;
}

.admin-sidebar {
  width: 200px;
  background-color: #333;
  color: white;
  padding: 20px 0;
}

.admin-sidebar h3 {
  padding: 0 20px;
  margin-bottom: 20px;
  font-size: 18px;
}

.admin-sidebar ul {
  list-style: none;
}

.admin-sidebar li {
  margin: 10px 0;
}

.admin-sidebar a {
  display: block;
  padding: 10px 20px;
  color: white;
  text-decoration: none;
  transition: background-color 0.3s;
}

.admin-sidebar a:hover {
  background-color: #4CAF50;
}

.admin-sidebar a:active {
  background-color: #388E3C;
}

.admin-sidebar a.router-link-active {
  background-color: #4CAF50;
  font-weight: bold;
}

.admin-content {
  flex: 1;
  padding: 20px;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0,0,0,0.1);
}
</style>
