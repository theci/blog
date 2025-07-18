<template>
  <div class="post-detail">
    <div class="container">
      <div v-if="loading" class="loading">Loading...</div>
      
      <div v-else-if="error" class="error">
        <h2>Post not found</h2>
        <p>The post you're looking for doesn't exist.</p>
        <button @click="goBack" class="btn-back">Go Back</button>
      </div>
      
      <div v-else class="post">
        <div class="post-header">
          <button @click="goBack" class="btn-back">← Back to Posts</button>
        </div>
        
        <article class="post-content">
          <div class="post-header-actions">
            <h1 class="post-title">{{ post.title }}</h1>
            <div v-if="canEditPost" class="post-actions">
              <button @click="editPost" class="btn-edit">편집</button>
              <button @click="deletePost" class="btn-delete">삭제</button>
            </div>
          </div>
          
          <div class="post-meta">
            <span class="post-author">작성자: {{ post.userResponse?.displayName || post.userResponse?.username || 'Unknown' }}</span>
            <span class="post-date">Created: {{ formatDate(post.createdDate) }}</span>
            <span v-if="post.modifiedDate !== post.createdDate" class="post-date">
              Modified: {{ formatDate(post.modifiedDate) }}
            </span>
            <span class="post-views">조회수: {{ post.viewCount || 0 }}</span>
          </div>
          
          <div class="post-actions-likes">
            <button 
              v-if="isAuthenticated"
              @click="toggleLike('like')" 
              :class="['like-btn', { active: userLikeStatus === 'like' }]"
            >
              👍 {{ post.likeCount || 0 }}
            </button>
            <button 
              v-if="isAuthenticated"
              @click="toggleLike('dislike')" 
              :class="['dislike-btn', { active: userLikeStatus === 'dislike' }]"
            >
              👎 {{ post.dislikeCount || 0 }}
            </button>
            <span v-if="!isAuthenticated" class="login-message">
              로그인하여 좋아요/싫어요를 표시하세요
            </span>
          </div>
          
          <div class="post-body" v-html="formatContent(post.content)"></div>
          
          <div v-if="post.fileAttachments && post.fileAttachments.length > 0" class="file-attachments">
            <h3>Attachments</h3>
            <div class="attachment-grid">
              <div 
                v-for="file in post.fileAttachments" 
                :key="file.id" 
                class="attachment-item"
              >
                <div v-if="isImage(file.contentType)" class="image-attachment">
                  <img :src="file.s3Url" :alt="file.originalFileName" @click="openFile(file.s3Url)" />
                  <p class="file-name">{{ file.originalFileName }}</p>
                </div>
                
                <div v-else-if="isVideo(file.contentType)" class="video-attachment">
                  <video :src="file.s3Url" controls>
                    Your browser does not support the video tag.
                  </video>
                  <p class="file-name">{{ file.originalFileName }}</p>
                </div>
                
                <div v-else class="document-attachment">
                  <div class="document-icon">📄</div>
                  <div class="document-info">
                    <p class="file-name">{{ file.originalFileName }}</p>
                    <p class="file-size">{{ formatFileSize(file.fileSize) }}</p>
                    <a :href="file.s3Url" target="_blank" class="download-btn">Download</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </article>
        
        <CommentSection 
          :post-id="post.id" 
          @comments-updated="onCommentsUpdated"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { postService } from '../services/api'
import CommentSection from '../components/CommentSection.vue'
import { authStore } from '../store/auth'

export default {
  name: 'PostDetail',
  components: {
    CommentSection
  },
  data() {
    return {
      post: null,
      loading: true,
      error: false,
      userLikeStatus: null
    }
  },
  computed: {
    canEditPost() {
      return authStore.isAuthenticated && 
             this.post && 
             this.post.userResponse && 
             authStore.user && 
             this.post.userResponse.username === authStore.user.username
    },
    isAuthenticated() {
      return authStore.isAuthenticated
    }
  },
  async mounted() {
    await this.fetchPost()
  },
  methods: {
    async fetchPost() {
      try {
        this.loading = true
        this.error = false
        const postId = this.$route.params.id
        const response = await postService.getPostById(postId)
        this.post = response.data
        
        // 게시글 조회 후 조회수 증가
        await this.incrementViewCount(postId)
      } catch (error) {
        console.error('Error fetching post:', error)
        this.error = true
      } finally {
        this.loading = false
      }
    },
    async incrementViewCount(postId) {
      try {
        await postService.incrementViewCount(postId)
      } catch (error) {
        console.error('Error incrementing view count:', error)
      }
    },
    goBack() {
      this.$router.push('/')
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatContent(content) {
      return content.replace(/\n/g, '<br>')
    },
    
    isImage(contentType) {
      return contentType && contentType.startsWith('image/')
    },
    
    isVideo(contentType) {
      return contentType && contentType.startsWith('video/')
    },
    
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    openFile(url) {
      window.open(url, '_blank')
    },
    
    onCommentsUpdated(count) {
      console.log(`Comments updated: ${count} comments`)
    },
    
    editPost() {
      this.$router.push(`/post/${this.post.id}/edit`)
    },
    
    async deletePost() {
      if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
        try {
          await postService.deletePost(this.post.id)
          this.$router.push('/')
        } catch (error) {
          console.error('Error deleting post:', error)
          alert('게시글 삭제에 실패했습니다.')
        }
      }
    },
    async toggleLike(type) {
      try {
        await postService.toggleLike(this.post.id, type)
        // 게시글 정보 새로고침
        const response = await postService.getPostById(this.post.id)
        this.post = response.data
      } catch (error) {
        console.error('Error toggling like:', error)
        alert('좋아요/싫어요 처리에 실패했습니다.')
      }
    }
  },
  watch: {
    '$route'() {
      this.fetchPost()
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.loading {
  text-align: center;
  padding: 50px 0;
  font-size: 18px;
  color: #7f8c8d;
}

.error {
  text-align: center;
  padding: 50px 0;
}

.error h2 {
  color: #e74c3c;
  margin-bottom: 10px;
}

.error p {
  color: #7f8c8d;
  margin-bottom: 20px;
}

.btn-back {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  margin-bottom: 20px;
}

.btn-back:hover {
  background-color: #7f8c8d;
}

.post-header {
  margin-bottom: 20px;
}

.post-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.post-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.post-title {
  color: #2c3e50;
  margin: 0;
  font-size: 2.5rem;
  line-height: 1.2;
  word-wrap: break-word;
  flex: 1;
}

.post-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
}

.btn-edit {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-edit:hover {
  background-color: #2980b9;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-delete:hover {
  background-color: #c0392b;
}

.post-meta {
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.post-author {
  color: #2c3e50;
  font-weight: 500;
  font-size: 0.9rem;
  margin-right: 20px;
}

.post-date {
  color: #95a5a6;
  font-size: 0.9rem;
  margin-right: 20px;
}

.post-views {
  color: #3498db;
  font-size: 0.9rem;
  font-weight: 500;
}

.post-actions-likes {
  display: flex;
  gap: 10px;
  align-items: center;
  margin: 20px 0;
  padding: 15px 0;
  border-top: 1px solid #ecf0f1;
  border-bottom: 1px solid #ecf0f1;
}

.like-btn, .dislike-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 16px;
  border: 2px solid #dee2e6;
  border-radius: 20px;
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.like-btn:hover {
  background-color: #e8f5e8;
  border-color: #28a745;
  color: #28a745;
}

.like-btn.active {
  background-color: #28a745;
  border-color: #28a745;
  color: white;
}

.dislike-btn:hover {
  background-color: #fce8e8;
  border-color: #dc3545;
  color: #dc3545;
}

.dislike-btn.active {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.login-message {
  color: #6c757d;
  font-size: 14px;
  font-style: italic;
}

.post-body {
  color: #2c3e50;
  line-height: 1.8;
  font-size: 1.1rem;
  word-wrap: break-word;
}

.post-body ::v-deep(br) {
  margin-bottom: 1em;
}

.file-attachments {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #ecf0f1;
}

.file-attachments h3 {
  color: #2c3e50;
  margin: 0 0 20px 0;
  font-size: 1.5rem;
}

.attachment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.attachment-item {
  border: 1px solid #ecf0f1;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.attachment-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-attachment img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
}

.image-attachment img:hover {
  transform: scale(1.05);
}

.video-attachment video {
  width: 100%;
  height: 200px;
}

.document-attachment {
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 15px;
}

.document-icon {
  font-size: 48px;
}

.document-info {
  flex: 1;
}

.file-name {
  margin: 10px 0 5px 0;
  font-weight: 500;
  color: #2c3e50;
  word-break: break-word;
}

.file-size {
  margin: 0;
  font-size: 0.9rem;
  color: #7f8c8d;
}

.download-btn {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.download-btn:hover {
  background-color: #2980b9;
}
</style>