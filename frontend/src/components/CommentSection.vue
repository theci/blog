<template>
  <div class="comment-section">
    <div class="comment-header">
      <h3>Comments ({{ comments.length }})</h3>
    </div>
    
    <div class="comment-form">
      <form @submit.prevent="submitComment">
        <div class="form-row">
          <input
            v-model="newComment.author"
            type="text"
            placeholder="Your name"
            class="author-input"
            required
          />
        </div>
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
          
          <div class="comment-actions">
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
                <input
                  v-model="editingComment.author"
                  type="text"
                  placeholder="Your name"
                  class="author-input"
                  required
                />
              </div>
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
        author: '',
        content: ''
      },
      editingComment: null
    }
  },
  async mounted() {
    await this.fetchComments()
  },
  methods: {
    async fetchComments() {
      try {
        this.loading = true
        const response = await this.$commentService.getCommentsByPostId(this.postId)
        this.comments = response.data
        this.$emit('comments-updated', this.comments.length)
      } catch (error) {
        console.error('Error fetching comments:', error)
      } finally {
        this.loading = false
      }
    },
    
    async submitComment() {
      if (!this.newComment.author.trim() || !this.newComment.content.trim()) {
        alert('Please fill in both name and comment.')
        return
      }
      
      try {
        this.submitting = true
        await this.$commentService.createComment(this.postId, {
          author: this.newComment.author.trim(),
          content: this.newComment.content.trim()
        })
        
        this.newComment.author = ''
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
        author: comment.author,
        content: comment.content
      }
    },
    
    cancelEdit() {
      this.editingComment = null
    },
    
    async updateComment() {
      if (!this.editingComment.author.trim() || !this.editingComment.content.trim()) {
        alert('Please fill in both name and comment.')
        return
      }
      
      try {
        this.updating = true
        await this.$commentService.updateComment(this.editingComment.id, {
          author: this.editingComment.author.trim(),
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
      if (!confirm('Are you sure you want to delete this comment?')) {
        return
      }
      
      try {
        await this.$commentService.deleteComment(commentId)
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
</style>