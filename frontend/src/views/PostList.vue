<template>
  <div class="post-list">
    <div class="container">
      <div class="header">
        <h1>Blog Posts</h1>
        <button @click="goToCreate" class="btn-create">Write New Post</button>
      </div>
      
      <div class="search-container">
        <div class="search-wrapper">
          <select v-model="searchType" class="search-dropdown">
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
            <option value="author">작성자</option>
          </select>
          <input 
            v-model="searchKeyword" 
            @keyup.enter="handleSearch"
            type="text" 
            placeholder="검색어를 입력하세요 (최소 2글자)"
            class="search-input"
          />
          <button @click="handleSearch" class="btn-search">
            검색
          </button>
          <button @click="clearSearch" class="btn-clear" v-if="searchKeyword">
            ✕
          </button>
        </div>
        <div v-if="validationMessage" class="validation-message">
          {{ validationMessage }}
        </div>
      </div>
      
      <div v-if="loading" class="loading">Loading...</div>
      
      <div v-else-if="posts.length === 0" class="no-posts">
        <span v-if="searchKeyword">검색 결과가 없습니다.</span>
        <span v-else>No posts available. Create your first post!</span>
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
            <span class="post-views">조회수: {{ post.viewCount || 0 }}</span>
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
      loading: true,
      searchKeyword: '',
      searchType: 'all',
      validationMessage: ''
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
    },
    handleSearch() {
      this.validationMessage = ''
      
      if (this.searchKeyword.length === 1) {
        this.validationMessage = '2글자를 입력하시오.'
        return
      }
      
      if (this.searchKeyword.length < 2) {
        this.fetchPosts()
        return
      }
      
      this.performSearch()
    },
    async performSearch() {
      try {
        this.loading = true
        const response = await postService.searchPosts(this.searchKeyword, this.searchType)
        this.posts = response.data
      } catch (error) {
        console.error('Error searching posts:', error)
      } finally {
        this.loading = false
      }
    },
    clearSearch() {
      this.searchKeyword = ''
      this.validationMessage = ''
      this.fetchPosts()
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-date {
  color: #95a5a6;
  font-size: 0.9rem;
}

.post-views {
  color: #3498db;
  font-size: 0.9rem;
  font-weight: 500;
}

.search-container {
  margin-bottom: 30px;
}

.search-wrapper {
  display: flex;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto;
  align-items: center;
}

.search-dropdown {
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 5px;
  font-size: 16px;
  background-color: white;
  cursor: pointer;
  min-width: 100px;
}

.search-dropdown:focus {
  outline: none;
  border-color: #3498db;
}

.search-input {
  flex: 1;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
}

.search-input::placeholder {
  color: #bdc3c7;
}

.btn-clear {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 12px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-clear:hover {
  background-color: #c0392b;
}

.btn-search {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-search:hover {
  background-color: #2980b9;
}

.validation-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 5px;
  text-align: center;
}
</style>