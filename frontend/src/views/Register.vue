<template>
  <div class="register-container">
    <div class="register-form">
      <h2>회원가입</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">사용자명</label>
          <input
            type="text"
            id="username"
            v-model="userData.username"
            required
            placeholder="사용자명을 입력하세요"
          />
        </div>
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            type="email"
            id="email"
            v-model="userData.email"
            required
            placeholder="이메일을 입력하세요"
          />
        </div>
        <div class="form-group">
          <label for="displayName">표시 이름</label>
          <input
            type="text"
            id="displayName"
            v-model="userData.displayName"
            placeholder="표시 이름을 입력하세요 (선택사항)"
          />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="userData.password"
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">비밀번호 확인</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            required
            placeholder="비밀번호를 다시 입력하세요"
          />
        </div>
        <button type="submit" :disabled="loading || !isPasswordMatch" class="register-btn">
          {{ loading ? '회원가입 중...' : '회원가입' }}
        </button>
      </form>
      <div class="form-footer">
        <p>이미 계정이 있으신가요? <router-link to="/login">로그인</router-link></p>
      </div>
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      <div v-if="!isPasswordMatch && confirmPassword" class="error-message">
        비밀번호가 일치하지 않습니다.
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authStore } from '../store/auth'

export default {
  name: 'RegisterPage',
  setup() {
    const router = useRouter()
    const userData = ref({
      username: '',
      email: '',
      displayName: '',
      password: ''
    })
    const confirmPassword = ref('')
    const loading = ref(false)
    const error = ref('')

    const isPasswordMatch = computed(() => {
      return !confirmPassword.value || userData.value.password === confirmPassword.value
    })

    const handleRegister = async () => {
      if (!isPasswordMatch.value) {
        error.value = '비밀번호가 일치하지 않습니다.'
        return
      }

      loading.value = true
      error.value = ''
      
      try {
        await authStore.register(userData.value)
        router.push('/')
      } catch (err) {
        if (err.response?.status === 400) {
          error.value = '이미 존재하는 사용자명 또는 이메일입니다.'
        } else {
          error.value = '회원가입에 실패했습니다. 다시 시도해주세요.'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      userData,
      confirmPassword,
      loading,
      error,
      isPasswordMatch,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.register-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-form h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.register-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #27ae60;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover:not(:disabled) {
  background-color: #229954;
}

.register-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.form-footer {
  text-align: center;
  margin-top: 1rem;
}

.form-footer a {
  color: #3498db;
  text-decoration: none;
}

.form-footer a:hover {
  text-decoration: underline;
}

.error-message {
  background-color: #e74c3c;
  color: white;
  padding: 0.75rem;
  border-radius: 4px;
  margin-top: 1rem;
  text-align: center;
}
</style>