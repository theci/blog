import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { commentService } from './services/api'

const app = createApp(App)
app.use(router)
app.config.globalProperties.$commentService = commentService
app.mount('#app')