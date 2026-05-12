import request from './request'

// 用户API
export const userApi = {
  register: (data) => request.post('/user/register', data),
  login: (data) => request.post('/user/login', data),
  list: () => request.get('/user/list'),
  update: (data) => request.put('/user/update', data),
  delete: (id) => request.delete(`/user/delete/${id}`)
}

// 产品API
export const productApi = {
  add: (data) => request.post('/product/add', data),
  list: () => request.get('/product/list'),
  listByCategory: (category) => request.get(`/product/listByCategory/${category}`),
  listByStatus: (status) => request.get(`/product/listByStatus/${status}`),
  get: (id) => request.get(`/product/get/${id}`),
  update: (data) => request.put('/product/update', data),
  delete: (id) => request.delete(`/product/delete/${id}`)
}

// 订单API
export const orderApi = {
  add: (data) => request.post('/order/add', data),
  list: () => request.get('/order/list'),
  listByUser: (userId) => request.get(`/order/listByUser/${userId}`),
  get: (id) => request.get(`/order/get/${id}`),
  update: (data) => request.put('/order/update', data),
  delete: (id) => request.delete(`/order/delete/${id}`)
}

// 公告API
export const announcementApi = {
  add: (data) => request.post('/announcement/add', data),
  update: (data) => request.put('/announcement/update', data),
  delete: (id) => request.delete(`/announcement/delete/${id}`),
  get: (id) => request.get(`/announcement/get/${id}`),
  list: () => request.get('/announcement/list'),
  listPublished: () => request.get('/announcement/listPublished')
}

// 评价API
export const reviewApi = {
  add: (data) => request.post('/review/add', data),
  listByProduct: (productId) => request.get(`/review/listByProduct/${productId}`),
  listByUser: (userId) => request.get(`/review/listByUser/${userId}`),
  get: (id) => request.get(`/review/get/${id}`),
  delete: (id) => request.delete(`/review/delete/${id}`)
}