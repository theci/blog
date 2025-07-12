<template>
  <div class="comment-section">
    <div class="comment-header">
      <h3>Comments ({{ comments.length }})</h3>
    </div>
    
    <div v-if="isAuthenticated" class="comment-form">
      <form @submit.prevent="submitComment">
        <div class="form-row">
          <textarea
            v-model="newComment.content"
            placeholder="Write your comment..."
            class="comment-textarea"
            rows="3"
            required
          ></textarea>
        </div>
        <div class="form-actions">
          <button type="submit" class="submit-btn" :disabled="submitting">
            {{ submitting ? 'Posting...' : 'Post Comment' }}
          </button>
        </div>
      </form>
    </div>
    
    <div v-else class="login-prompt">
      <p>Please <router-link to="/login">login</router-link> to post a comment.</p>
    </div>
    
    <div class="comments-list">
      <div v-if="loading" class="loading">Loading comments...</div>
      
      <div v-else-if="comments.length === 0" class="no-comments">
        No comments yet. Be the first to comment!
      </div>
      
      <div v-else>
        <div 
          v-for="comment in comments" 
          :key="comment.id" 
          class="comment-item"
        >
          <div class="comment-header-info">
            <span class="comment-author">{{ comment.author }}</span>
            <span class="comment-date">{{ formatDate(comment.createdDate) }}</span>
            <span v-if="comment.modifiedDate !== comment.createdDate" class="comment-edited">
              (edited)
            </span>
          </div>
          
          <div class="comment-content">
            {{ comment.content }}
          </div>
          
          <div v-if="canEditComment(comment)" class="comment-actions">
            <button 
              @click="startEdit(comment)" 
              class="edit-btn"
              v-if="!editingComment || editingComment.id !== comment.id"
            >
              Edit
            </button>
            <button 
              @click="deleteComment(comment.id)" 
              class="delete-btn"
              v-if="!editingComment || editingComment.id !== comment.id"
            >
              Delete
            </button>
          </div>
          
          <div v-if="editingComment && editingComment.id === comment.id" class="edit-form">
            <form @submit.prevent="updateComment">
              <div class="form-row">
                <textarea
                  v-model="editingComment.content"
                  placeholder="Edit your comment..."
                  class="comment-textarea"
                  rows="3"
                  required
                ></textarea>
              </div>
              <div class="form-actions">
                <button type="button" @click="cancelEdit" class="cancel-btn">Cancel</button>
                <button type="submit" class="submit-btn" :disabled="updating">
                  {{ updating ? 'Updating...' : 'Update' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { commentService } from '../services/api'
import { authStore } from '../store/auth'

export default {
  name: 'CommentSection',
  props: {
    postId: {
      type: [String, Number],
      required: true
    }
  },
  emits: ['comments-updated'],
  data() {
    return {
      comments: [],
      loading: false,
      submitting: false,
      updating: false,
      newComment: {
        content: ''
      },
      editingComment: null
    }
  },
  computed: {
    isAuthenticated() {
      return authStore.isAuthenticated
    }
  },
  async mounted() {
    await this.fetchComments()
  },
  methods: {
    async fetchComments() {
      try {
        this.loading = true
        const response = await commentService.getCommentsByPostId(this.postId)
        this.comments = response.data
        this.$emit('comments-updated', this.comments.length)
      } catch (error) {
        console.error('Error fetching comments:', error)
      } finally {
        this.loading = false
      }
    },
    
    canEditComment(comment) {
      return authStore.isAuthenticated && 
             comment.userResponse && 
             authStore.user && 
             comment.userResponse.username === authStore.user.username
    },
    
    async submitComment() {
      if (!this.newComment.content.trim()) {
        alert('Please write a comment.')
        return
      }
      
      try {
        this.submitting = true
        await commentService.createComment(this.postId, {
          content: this.newComment.content.trim()
        })
        
        this.newComment.content = ''
        await this.fetchComments()
      } catch (error) {
        console.error('Error creating comment:', error)
        alert('Failed to post comment. Please try again.')
      } finally {
        this.submitting = false
      }
    },
    
    startEdit(comment) {
      this.editingComment = {
        id: comment.id,
        content: comment.content
      }
    },
    
    cancelEdit() {
      this.editingComment = null
    },
    
    async updateComment() {
      if (!this.editingComment.content.trim()) {
        alert('Please write a comment.')
        return
      }
      
      try {
        this.updating = true
        await commentService.updateComment(this.editingComment.id, {
          content: this.editingComment.content.trim()
        })
        
        this.editingComment = null
        await this.fetchComments()
      } catch (error) {
        console.error('Error updating comment:', error)
        alert('Failed to update comment. Please try again.')
      } finally {
        this.updating = false
      }
    },
    
    async deleteComment(commentId) {
      if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
        return
      }
      
      try {
        await commentService.deleteComment(commentId)
        await this.fetchComments()
      } catch (error) {
        console.error('Error deleting comment:', error)
        alert('Failed to delete comment. Please try again.')
      }
    },
    
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  },
  watch: {
    postId() {
      this.fetchComments()
    }
  }
}
</script>

<style scoped>
.comment-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #ecf0f1;
}

.comment-header h3 {
  color: #2c3e50;
  margin: 0 0 25px 0;
  font-size: 1.5rem;
}

.comment-form {
  background-color: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.form-row {
  margin-bottom: 15px;
}

.author-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  box-sizing: border-box;
}

.author-input:focus,
.comment-textarea:focus {
  outline: none;
  border-color: #3498db;
}

.form-actions {
  text-align: right;
}

.submit-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #2980b9;
}

.submit-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
  transition: background-color 0.3s;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

.loading {
  text-align: center;
  padding: 30px;
  color: #7f8c8d;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #7f8c8d;
  font-style: italic;
}

.comment-item {
  background-color: white;
  border: 1px solid #ecf0f1;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.comment-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-header-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.comment-author {
  font-weight: 600;
  color: #2c3e50;
}

.comment-date {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.comment-edited {
  font-size: 0.8rem;
  color: #95a5a6;
  font-style: italic;
}

.comment-content {
  color: #2c3e50;
  line-height: 1.6;
  margin-bottom: 15px;
  white-space: pre-wrap;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  font-size: 0.9rem;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.edit-btn:hover {
  background-color: #e8f4fd;
}

.delete-btn {
  color: #e74c3c;
}

.delete-btn:hover {
  background-color: #fdf2f2;
}

.edit-form {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ecf0f1;
}

.login-prompt {
  background-color: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  margin-bottom: 30px;
  text-align: center;
}

.login-prompt p {
  margin: 0;
  color: #7f8c8d;
}

.login-prompt a {
  color: #3498db;
  text-decoration: none;
}

.login-prompt a:hover {
  text-decoration: underline;
}
</style>