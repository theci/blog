import { reactive } from 'vue'
import { authService } from '../services/api'

export const authStore = reactive({
  user: null,
  token: localStorage.getItem('token'),
  isAuthenticated: false,

  init() {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    
    if (storedUser && storedToken) {
      this.user = JSON.parse(storedUser)
      this.token = storedToken
      this.isAuthenticated = true
    }
  },

  async login(credentials) {
    const response = await authService.login(credentials)
    const { token, user } = response.data
    
    this.token = token
    this.user = user
    this.isAuthenticated = true
    
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    return response.data
  },

  async register(userData) {
    const response = await authService.register(userData)
    const { token, user } = response.data
    
    this.token = token
    this.user = user
    this.isAuthenticated = true
    
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    return response.data
  },

  async logout() {
    try {
      await authService.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      this.user = null
      this.token = null
      this.isAuthenticated = false
      
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  },

  async getCurrentUser() {
    try {
      const response = await authService.getCurrentUser()
      this.user = response.data
      return response.data
    } catch (error) {
      this.logout()
      throw error
    }
  }
})

authStore.init()