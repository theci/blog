<template>
  <div class="post-management">
    <h2>게시글 관리</h2>
    
    <div v-if="loading" class="loading">
      로딩 중...
    </div>
    
    <div v-else-if="error" class="error">
      {{ error }}
    </div>
    
    <div v-else class="posts-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>작성자</th>
            <th>카테고리</th>
            <th>조회수</th>
            <th>좋아요</th>
            <th>싫어요</th>
            <th>작성일</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in posts" :key="post.id" :class="{ hidden: post.hidden }">
            <td>{{ post.id }}</td>
            <td>
              <div class="post-title">
                {{ post.title }}
                <span v-if="post.hidden" class="hidden-badge">숨김</span>
              </div>
            </td>
            <td>{{ post.username }}</td>
            <td>
              <span class="category-badge">{{ post.category }}</span>
            </td>
            <td>{{ post.viewCount }}</td>
            <td>{{ post.likeCount }}</td>
            <td>{{ post.dislikeCount }}</td>
            <td>{{ formatDate(post.createdDate) }}</td>
            <td>
              <span v-if="post.hidden" class="status-badge hidden">숨김</span>
              <span v-else class="status-badge visible">표시</span>
            </td>
            <td>
              <div class="action-buttons">
                <div v-if="!post.hidden" class="hide-controls">
                  <input 
                    v-model="hideReasons[post.id]" 
                    placeholder="숨김 사유" 
                    class="reason-input"
                  />
                  <button 
                    @click="hidePost(post.id)" 
                    class="hide-btn"
                    :disabled="!hideReasons[post.id]"
                  >
                    숨김
                  </button>
                </div>
                <div v-else class="unhide-controls">
                  <button @click="unhidePost(post.id)" class="unhide-btn">
                    숨김 해제
                  </button>
                  <div v-if="post.hiddenReason" class="hidden-reason">
                    사유: {{ post.hiddenReason }}
                  </div>
                </div>
                <button @click="deletePost(post.id)" class="delete-btn">
                  삭제
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { adminService } from '../services/api'

export default {
  name: 'PostManagement',
  data() {
    return {
      posts: [],
      loading: false,
      error: null,
      hideReasons: {}
    }
  },
  async mounted() {
    await this.loadPosts()
  },
  methods: {
    async loadPosts() {
      this.loading = true
      this.error = null
      
      try {
        const response = await adminService.getAllPosts()
        this.posts = response.data
        
        // 각 게시글의 숨김 사유 입력 필드 초기화
        this.posts.forEach(post => {
          this.hideReasons[post.id] = ''
        })
      } catch (error) {
        this.error = '게시글 목록을 불러오는 중 오류가 발생했습니다.'
        console.error('Error loading posts:', error)
      } finally {
        this.loading = false
      }
    },
    
    async hidePost(postId) {
      if (!this.hideReasons[postId]) {
        alert('숨김 사유를 입력해주세요.')
        return
      }
      
      if (!confirm('이 게시글을 숨기시겠습니까?')) {
        return
      }
      
      try {
        await adminService.hidePost(postId, this.hideReasons[postId])
        alert('게시글이 숨겨졌습니다.')
        await this.loadPosts()
      } catch (error) {
        alert('게시글 숨김 처리 중 오류가 발생했습니다.')
        console.error('Error hiding post:', error)
      }
    },
    
    async unhidePost(postId) {
      if (!confirm('이 게시글의 숨김을 해제하시겠습니까?')) {
        return
      }
      
      try {
        await adminService.unhidePost(postId)
        alert('게시글 숨김이 해제되었습니다.')
        await this.loadPosts()
      } catch (error) {
        alert('게시글 숨김 해제 중 오류가 발생했습니다.')
        console.error('Error unhiding post:', error)
      }
    },
    
    async deletePost(postId) {
      if (!confirm('이 게시글을 완전히 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
        return
      }
      
      try {
        await adminService.deletePost(postId)
        alert('게시글이 삭제되었습니다.')
        await this.loadPosts()
      } catch (error) {
        alert('게시글 삭제 중 오류가 발생했습니다.')
        console.error('Error deleting post:', error)
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('ko-KR')
    }
  }
}
</script>

<style scoped>
.post-management {
  padding: 20px;
}

h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
  font-size: 18px;
}

.error {
  background-color: #ffebee;
  color: #c62828;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.posts-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-radius: 8px;
  overflow: hidden;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

tr:hover {
  background-color: #f8f9fa;
}

tr.hidden {
  background-color: #ffeaa7;
}

.post-title {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 200px;
}

.hidden-badge {
  background-color: #e17055;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: bold;
}

.category-badge {
  background-color: #74b9ff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge.visible {
  background-color: #00b894;
  color: white;
}

.status-badge.hidden {
  background-color: #e17055;
  color: white;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 200px;
}

.hide-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.unhide-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reason-input {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  width: 150px;
}

.hide-btn {
  background-color: #fdcb6e;
  color: #2d3436;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.hide-btn:hover:not(:disabled) {
  background-color: #f39c12;
}

.hide-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.unhide-btn {
  background-color: #00b894;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.unhide-btn:hover {
  background-color: #00a085;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.hidden-reason {
  font-size: 11px;
  color: #666;
  font-style: italic;
  max-width: 150px;
  word-wrap: break-word;
}
</style>