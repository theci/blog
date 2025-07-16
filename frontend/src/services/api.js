import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const postService = {
  getAllPosts() {
    return api.get('/posts')
  },
  
  getPostById(id) {
    return api.get(`/posts/${id}`)
  },
  
  createPost(post) {
    return api.post('/posts', post)
  },
  
  updatePost(id, post) {
    return api.put(`/posts/${id}`, post)
  },
  
  deletePost(id) {
    return api.delete(`/posts/${id}`)
  },
  
  searchPosts(keyword, searchType = 'all') {
    return api.get(`/posts/search?keyword=${encodeURIComponent(keyword)}&searchType=${searchType}`)
  },
  
  incrementViewCount(id) {
    return api.post(`/posts/${id}/view`)
  },
  
  getPostsByCategory(category) {
    return api.get(`/posts/category/${category}`)
  },
  
  toggleLike(id, type) {
    return api.post(`/posts/${id}/like?type=${type}`)
  },
  
  getPostsSorted(sortBy = 'recent', category = 'all') {
    return api.get(`/posts/sorted?sortBy=${sortBy}&category=${category}`)
  }
}

export const adminService = {
  getAllUsers() {
    return api.get('/admin/users')
  },
  
  getAllPosts() {
    return api.get('/admin/posts')
  },
  
  suspendUser(userId, days, reason) {
    return api.post(`/admin/users/${userId}/suspend`, { days, reason })
  },
  
  unsuspendUser(userId) {
    return api.post(`/admin/users/${userId}/unsuspend`)
  },
  
  hidePost(postId, reason) {
    return api.post(`/admin/posts/${postId}/hide`, { reason })
  },
  
  unhidePost(postId) {
    return api.post(`/admin/posts/${postId}/unhide`)
  },
  
  deletePost(postId) {
    return api.delete(`/admin/posts/${postId}`)
  },
  
  getUserSuspension(userId) {
    return api.get(`/admin/users/${userId}/suspension`)
      .catch(error => {
        // 404 에러는 정지 정보가 없다는 뜻이므로 조용히 처리
        if (error.response?.status === 404) {
          return { data: null }
        }
        throw error
      })
  }
}

export const fileService = {
  uploadFiles(postId, files) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    
    return axios.post(`${API_BASE_URL}/files/upload/${postId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      }
    })
  }
}

export const commentService = {
  getCommentsByPostId(postId) {
    return api.get(`/comments/post/${postId}`)
  },
  
  createComment(postId, comment) {
    return api.post(`/comments/post/${postId}`, comment)
  },
  
  updateComment(commentId, comment) {
    return api.put(`/comments/${commentId}`, comment)
  },
  
  deleteComment(commentId) {
    return api.delete(`/comments/${commentId}`)
  },
  
  getCommentCount(postId) {
    return api.get(`/comments/post/${postId}/count`)
  }
}

export const authService = {
  register(userData) {
    return api.post('/auth/register', userData)
  },
  
  login(credentials) {
    return api.post('/auth/login', credentials)
  },
  
  logout() {
    return api.post('/auth/logout')
  },
  
  getCurrentUser() {
    return api.get('/auth/me')
  }
}

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('API Request:', config.method?.toUpperCase(), config.url, 'with token')
    } else {
      console.log('API Request:', config.method?.toUpperCase(), config.url, 'without token')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 404 에러는 정상적인 상황일 수 있으므로 로그 레벨을 낮춤
    if (error.response?.status === 404) {
      console.debug('API 404:', error.config?.url);
    } else if (error.response?.status !== 401 && error.response?.status !== 403) {
      console.log('API Error:', error.response?.status, error.response?.data);
    }
    
    if (error.response?.status === 401) {
      // 토큰이 만료되었거나 무효한 경우
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // authStore도 초기화
      if (typeof window !== 'undefined' && window.authStore) {
        window.authStore.user = null
        window.authStore.token = null
        window.authStore.isAuthenticated = false
      }
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else if (error.response?.status === 403) {
      // 권한 없음 - 현재 페이지에서 에러 처리
      console.error('Access denied:', error.response.data)
      alert('권한이 없습니다.')
    }
    return Promise.reject(error)
  }
)

export default api