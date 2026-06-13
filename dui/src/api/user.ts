import request from '@/utils/request'

// 示例 API
export const userApi = {
  // 获取用户信息
  getUser(id: number) {
    return request.get(`/user/${id}`)
  },

  // 获取用户列表
  getUserList(params?: { page?: number; size?: number }) {
    return request.get('/users', { params })
  },

  // 创建用户
  createUser(data: { name: string; email: string }) {
    return request.post('/user', data)
  },

  // 更新用户
  updateUser(id: number, data: { name?: string; email?: string }) {
    return request.put(`/user/${id}`, data)
  },

  // 删除用户
  deleteUser(id: number) {
    return request.delete(`/user/${id}`)
  }
}