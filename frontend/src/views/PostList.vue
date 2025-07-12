<template>
  <div class="post-list">
    <div class="container">
      <div class="header">
        <h1>Blog Posts</h1>
        <button @click="goToCreate" class="btn-create">Write New Post</button>
      </div>
      
      <div v-if="loading" class="loading">Loading...</div>
      
      <div v-else-if="posts.length === 0" class="no-posts">
        No posts available. Create your first post!
      </div>
      
      <div v-else class="posts-grid">
        <div 
          v-for="post in posts" 
          :key="post.id" 
          class="post-card"
          @click="goToDetail(post.id)"
        >
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-content">{{ truncateContent(post.content) }}</p>
          <div class="post-meta">
            <span class="post-date">{{ formatDate(post.createdDate) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { postService } from '../services/api'

export default {
  name: 'PostList',
  data() {
    return {
      posts: [],
      loading: true
    }
  },
  async mounted() {
    await this.fetchPosts()
  },
  methods: {
    async fetchPosts() {
      try {
        this.loading = true
        const response = await postService.getAllPosts()
        this.posts = response.data
      } catch (error) {
        console.error('Error fetching posts:', error)
      } finally {
        this.loading = false
      }
    },
    goToDetail(id) {
      this.$router.push(`/post/${id}`)
    },
    goToCreate() {
      this.$router.push('/create')
    },
    truncateContent(content) {
      return content.length > 150 ? content.substring(0, 150) + '...' : content
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
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

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  color: #2c3e50;
  margin: 0;
}

.btn-create {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-create:hover {
  background-color: #2980b9;
}

.loading {
  text-align: center;
  padding: 50px 0;
  font-size: 18px;
  color: #7f8c8d;
}

.no-posts {
  text-align: center;
  padding: 50px 0;
  font-size: 18px;
  color: #7f8c8d;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.post-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.post-title {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 1.4rem;
  line-height: 1.3;
}

.post-content {
  color: #7f8c8d;
  line-height: 1.5;
  margin: 0 0 15px 0;
}

.post-meta {
  border-top: 1px solid #ecf0f1;
  padding-top: 10px;
}

.post-date {
  color: #95a5a6;
  font-size: 0.9rem;
}
</style>