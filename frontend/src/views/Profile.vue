<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>내 프로필</h1>
    </div>
    
    <div v-if="loading" class="loading">
      로딩 중...
    </div>
    
    <div v-else-if="user" class="profile-content">
      <div class="profile-info">
        <h2>사용자 정보</h2>
        <div class="info-item">
          <label>사용자명:</label>
          <span>{{ user.username }}</span>
        </div>
        <div class="info-item">
          <label>이메일:</label>
          <span>{{ user.email }}</span>
        </div>
        <div class="info-item">
          <label>표시 이름:</label>
          <span>{{ user.displayName || '설정되지 않음' }}</span>
        </div>
        <div class="info-item">
          <label>가입일:</label>
          <span>{{ formatDate(user.createdDate) }}</span>
        </div>
        <div class="info-item points">
          <label>포인트:</label>
          <span class="points-value">{{ user.points || 0 }}P</span>
        </div>
      </div>
    </div>
    
    <div v-else class="error">
      프로필을 불러올 수 없습니다.
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'UserProfile',
  setup() {
    const user = ref(null)
    const loading = ref(true)

    const fetchUserProfile = async () => {
      try {
        const response = await api.get('/auth/me')
        user.value = response.data
      } catch (error) {
        console.error('프로필 로딩 실패:', error)
      } finally {
        loading.value = false
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    onMounted(() => {
      fetchUserProfile()
    })

    return {
      user,
      loading,
      formatDate
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-header h1 {
  color: #333;
  font-size: 2rem;
}

.loading, .error {
  text-align: center;
  padding: 20px;
  font-size: 1.1rem;
}

.error {
  color: #e74c3c;
}

.profile-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.profile-info h2 {
  color: #2c3e50;
  margin-bottom: 20px;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ecf0f1;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  font-weight: 600;
  color: #34495e;
  min-width: 100px;
}

.info-item span {
  color: #2c3e50;
  text-align: right;
}

.info-item.points {
  background: linear-gradient(135deg, #f39c12, #e67e22);
  margin: 15px -15px -15px -15px;
  padding: 15px;
  border-radius: 0 0 8px 8px;
  border: none;
}

.info-item.points label,
.info-item.points span {
  color: white;
  font-weight: bold;
}

.points-value {
  font-size: 1.2rem;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}
</style>