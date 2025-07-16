import { createRouter, createWebHistory } from 'vue-router'
import PostList from '../views/PostList.vue'
import PostDetail from '../views/PostDetail.vue'
import PostCreate from '../views/PostCreate.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import Admin from '../views/Admin.vue'
import { authStore } from '../store/auth'

const routes = [
  {
    path: '/',
    name: 'PostList',
    component: PostList
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: PostDetail
  },
  {
    path: '/create',
    name: 'PostCreate',
    component: PostCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guest: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const isAuthenticated = authStore.isAuthenticated
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }
  
  if (to.meta.guest && isAuthenticated) {
    next('/')
    return
  }
  
  if (to.meta.requiresAdmin) {
    // 관리자 권한이 필요한 경우, 현재 사용자 정보를 다시 확인
    console.log('Admin route access check:', {
      path: to.path,
      user: authStore.user,
      isAuthenticated: authStore.isAuthenticated
    })
    
    try {
      if (!authStore.user) {
        console.log('User not found, fetching current user...')
        await authStore.getCurrentUser()
      }
      
      const isAdmin = authStore.user?.role === 'ADMIN'
      console.log('Admin check result:', {
        user: authStore.user,
        role: authStore.user?.role,
        isAdmin: isAdmin
      })
      
      if (!isAdmin) {
        alert('관리자 권한이 필요합니다.')
        next('/')
        return
      }
    } catch (error) {
      console.error('Admin auth check failed:', error)
      next('/login')
      return
    }
  }
  
  next()
})

export default router