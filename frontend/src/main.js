import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { commentService } from './services/api'
import { authStore } from './store/auth'

const app = createApp(App)
app.use(router)
app.config.globalProperties.$commentService = commentService

// authStore를 전역에서 접근할 수 있도록 설정
window.authStore = authStore

app.mount('#app')