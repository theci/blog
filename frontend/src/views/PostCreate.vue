<template>
  <div class="post-create">
    <div class="container">
      <div class="header">
        <h1>Write New Post</h1>
        <button @click="goBack" class="btn-back">← Back to Posts</button>
      </div>
      
      <form @submit.prevent="submitPost" class="post-form">
        <div class="form-group">
          <label for="title">Title</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            class="form-control"
            placeholder="Enter post title..."
            required
          />
        </div>
        
        <div class="form-group">
          <label for="category">Category</label>
          <select
            id="category"
            v-model="form.category"
            class="form-control"
            required
          >
            <option value="">카테고리를 선택하세요</option>
            <option value="정치">정치</option>
            <option value="연예">연예</option>
            <option value="스포츠">스포츠</option>
            <option value="유머">유머</option>
            <option value="게임">게임</option>
            <option value="쇼핑">쇼핑</option>
            <option value="지식">지식</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="content">Content</label>
          <textarea
            id="content"
            v-model="form.content"
            class="form-control content-textarea"
            placeholder="Write your post content here..."
            required
          ></textarea>
        </div>
        
        <div class="form-group">
          <label>File Attachments</label>
          <FileUpload 
            ref="fileUpload"
            @files-selected="onFilesSelected"
          />
        </div>
        
        <div class="form-actions">
          <button type="button" @click="goBack" class="btn-cancel">Cancel</button>
          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitting ? 'Publishing...' : 'Publish Post' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { postService, fileService } from '../services/api'
import FileUpload from '../components/FileUpload.vue'

export default {
  name: 'PostCreate',
  components: {
    FileUpload
  },
  data() {
    return {
      form: {
        title: '',
        content: '',
        category: ''
      },
      selectedFiles: [],
      submitting: false
    }
  },
  methods: {
    async submitPost() {
      if (!this.form.title.trim() || !this.form.content.trim() || !this.form.category) {
        alert('Please fill in title, content, and select a category.')
        return
      }
      
      try {
        this.submitting = true
        
        const postResponse = await postService.createPost({
          title: this.form.title.trim(),
          content: this.form.content.trim(),
          category: this.form.category
        })
        
        const postId = postResponse.data.id
        
        if (this.selectedFiles.length > 0) {
          try {
            await fileService.uploadFiles(postId, this.selectedFiles)
          } catch (fileError) {
            console.error('Error uploading files:', fileError)
            alert('Post created but file upload failed. You can try uploading files later.')
          }
        }
        
        alert('Post created successfully!')
        this.$router.push('/')
      } catch (error) {
        console.error('Error creating post:', error)
        alert('Failed to create post. Please try again.')
      } finally {
        this.submitting = false
      }
    },
    onFilesSelected(files) {
      this.selectedFiles = files
    },
    
    goBack() {
      if (this.form.title || this.form.content || this.form.category || this.selectedFiles.length > 0) {
        if (confirm('You have unsaved changes. Are you sure you want to leave?')) {
          this.$router.push('/')
        }
      } else {
        this.$router.push('/')
      }
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

.btn-back {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-back:hover {
  background-color: #7f8c8d;
}

.post-form {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 600;
  font-size: 1.1rem;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #ecf0f1;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #3498db;
}

.content-textarea {
  min-height: 300px;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
}

.btn-cancel {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-cancel:hover {
  background-color: #7f8c8d;
}

.btn-submit {
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-submit:hover:not(:disabled) {
  background-color: #229954;
}

.btn-submit:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}
</style>