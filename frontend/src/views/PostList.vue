<template>
  <div class="post-list">
    <div class="container">
      <div class="header">
        <h1>Blog Posts</h1>
        <button @click="goToCreate" class="btn-create">Write New Post</button>
      </div>
      
      <div class="category-menu">
        <button 
          @click="selectCategory('all')" 
          :class="['category-btn', { active: selectedCategory === 'all' }]"
        >
          전체
        </button>
        <button 
          @click="selectCategory('정치')" 
          :class="['category-btn', { active: selectedCategory === '정치' }]"
        >
          정치
        </button>
        <button 
          @click="selectCategory('연예')" 
          :class="['category-btn', { active: selectedCategory === '연예' }]"
        >
          연예
        </button>
        <button 
          @click="selectCategory('스포츠')" 
          :class="['category-btn', { active: selectedCategory === '스포츠' }]"
        >
          스포츠
        </button>
        <button 
          @click="selectCategory('유머')" 
          :class="['category-btn', { active: selectedCategory === '유머' }]"
        >
          유머
        </button>
        <button 
          @click="selectCategory('게임')" 
          :class="['category-btn', { active: selectedCategory === '게임' }]"
        >
          게임
        </button>
        <button 
          @click="selectCategory('쇼핑')" 
          :class="['category-btn', { active: selectedCategory === '쇼핑' }]"
        >
          쇼핑
        </button>
        <button 
          @click="selectCategory('지식')" 
          :class="['category-btn', { active: selectedCategory === '지식' }]"
        >
          지식
        </button>
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
          <div class="post-category-badge">{{ post.category }}</div>
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
      validationMessage: '',
      selectedCategory: 'all'
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
    },
    async selectCategory(category) {
      this.selectedCategory = category
      this.searchKeyword = ''
      this.validationMessage = ''
      await this.fetchPostsByCategory(category)
    },
    async fetchPostsByCategory(category) {
      try {
        this.loading = true
        if (category === 'all') {
          const response = await postService.getAllPosts()
          this.posts = response.data
        } else {
          const response = await postService.getPostsByCategory(category)
          this.posts = response.data
        }
      } catch (error) {
        console.error('Error fetching posts by category:', error)
      } finally {
        this.loading = false
      }
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

.post-category-badge {
  background-color: #e74c3c;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-block;
  margin-bottom: 10px;
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

.category-menu {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  padding: 20px 0;
  border-bottom: 1px solid #ecf0f1;
}

.category-btn {
  background-color: #f8f9fa;
  color: #6c757d;
  border: 1px solid #dee2e6;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  white-space: nowrap;
}

.category-btn:hover {
  background-color: #e9ecef;
  color: #495057;
}

.category-btn.active {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.category-btn.active:hover {
  background-color: #2980b9;
  border-color: #2980b9;
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