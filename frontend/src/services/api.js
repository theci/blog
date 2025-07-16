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
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api