<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <router-link to="/" class="nav-logo">Blog</router-link>
        <div class="nav-menu">
          <router-link to="/" class="nav-link">Home</router-link>
          <div v-if="!authStore.isAuthenticated" class="auth-buttons">
            <router-link to="/login" class="nav-link login-btn">로그인</router-link>
            <router-link to="/register" class="nav-link register-btn">회원가입</router-link>
          </div>
          <div v-else class="user-menu">
            <router-link to="/create" class="nav-link">글쓰기</router-link>
            <router-link to="/profile" class="nav-link profile-btn">프로필</router-link>
            <span class="user-info">{{ authStore.user?.displayName || authStore.user?.username }}</span>
            <button @click="handleLogout" class="nav-link logout-btn">로그아웃</button>
          </div>
        </div>
      </div>
    </nav>
    <router-view/>
  </div>
</template>

<script>
import { authStore } from './store/auth'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const router = useRouter()

    const handleLogout = async () => {
      await authStore.logout()
      router.push('/')
    }

    return {
      authStore,
      handleLogout
    }
  }
}
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  color: #2c3e50;
}

.navbar {
  background-color: #34495e;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav-link {
  color: white;
  text-decoration: none;
  margin-left: 20px;
}

.nav-link:hover {
  color: #3498db;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.login-btn {
  background-color: #3498db;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #2980b9;
  color: white;
}

.register-btn {
  background-color: #27ae60;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.register-btn:hover {
  background-color: #229954;
  color: white;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  color: white;
  font-weight: 500;
}

.logout-btn {
  background-color: #e74c3c;
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
}

.logout-btn:hover {
  background-color: #c0392b;
  color: white;
}

.profile-btn {
  background-color: #9b59b6;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.profile-btn:hover {
  background-color: #8e44ad;
  color: white;
}
</style>