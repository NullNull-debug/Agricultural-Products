import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('../views/Products.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue')
  },
  {
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: () => import('../views/OrderConfirm.vue')
  },
  {
    path: '/order/pay/:id',
    name: 'Payment',
    component: () => import('../views/Payment.vue')
  },
  {
    path: '/product/review/:id',
    name: 'ProductReview',
    component: () => import('../views/ProductReview.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../views/admin/AdminProducts.vue')
      },
      {
        path: 'products/add',
        name: 'AdminProductAdd',
        component: () => import('../views/admin/AdminProductEdit.vue')
      },
      {
        path: 'products/edit/:id',
        name: 'AdminProductEdit',
        component: () => import('../views/admin/AdminProductEdit.vue')
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('../views/admin/AdminAnnouncements.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/AdminOrders.vue')
      },
      {
        path: 'messages',
        name: 'AdminMessages',
        component: () => import('../views/Admin/AdminMessages.vue')
      }
    ]
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/user/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('../views/Message.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('../views/Test.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('user')
  const user = userInfo ? JSON.parse(userInfo) : null

  if (to.meta.requiresAuth && !user) {
    alert('请先登录后再访问此页面')
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && user && user.role !== 1) {
    alert('您没有权限访问此页面')
    next('/')
    return
  }

  next()
})

export default router