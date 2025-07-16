<template>
  <div class="admin-page">
    <div class="container">
      <h1>관리자 페이지</h1>
      
      <div class="admin-tabs">
        <button 
          @click="activeTab = 'users'" 
          :class="['tab-btn', { active: activeTab === 'users' }]"
        >
          사용자 관리
        </button>
        <button 
          @click="activeTab = 'posts'" 
          :class="['tab-btn', { active: activeTab === 'posts' }]"
        >
          게시글 관리
        </button>
      </div>

      <div v-if="activeTab === 'users'" class="tab-content">
        <UserManagement />
      </div>

      <div v-if="activeTab === 'posts'" class="tab-content">
        <PostManagement />
      </div>
    </div>
  </div>
</template>

<script>
import UserManagement from '../components/UserManagement.vue'
import PostManagement from '../components/PostManagement.vue'
import { authStore } from '../store/auth'

export default {
  name: 'AdminPage',
  components: {
    UserManagement,
    PostManagement
  },
  data() {
    return {
      activeTab: 'users'
    }
  },
  async mounted() {
    // 관리자 권한 확인
    try {
      if (!authStore.user) {
        await authStore.getCurrentUser()
      }
      
      if (!authStore.user || authStore.user.role !== 'ADMIN') {
        this.$router.push('/')
      }
    } catch (error) {
      console.error('Admin auth check failed:', error)
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 30px;
  text-align: center;
}

.admin-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #ecf0f1;
}

.tab-btn {
  padding: 12px 24px;
  background-color: #f8f9fa;
  border: none;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
  color: #6c757d;
}

.tab-btn:hover {
  background-color: #e9ecef;
  color: #495057;
}

.tab-btn.active {
  background-color: #3498db;
  color: white;
}

.tab-content {
  min-height: 500px;
}
</style>