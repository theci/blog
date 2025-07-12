<template>
  <div class="file-upload">
    <div class="upload-area" @drop="handleDrop" @dragover="handleDragOver" @dragenter="handleDragEnter" @dragleave="handleDragLeave">
      <input
        ref="fileInput"
        type="file"
        multiple
        @change="handleFileSelect"
        accept="image/*,video/*,.pdf,.doc,.docx,.txt"
        style="display: none"
      />
      
      <div class="upload-content">
        <div class="upload-icon">📁</div>
        <p>Drop files here or <button type="button" @click="selectFiles" class="select-btn">choose files</button></p>
        <p class="upload-hint">Supports: Images, Videos, PDF, Documents</p>
      </div>
    </div>
    
    <div v-if="selectedFiles.length > 0" class="file-list">
      <h4>Selected Files:</h4>
      <div class="file-item" v-for="(file, index) in selectedFiles" :key="index">
        <div class="file-info">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
        </div>
        <button type="button" @click="removeFile(index)" class="remove-btn">×</button>
      </div>
    </div>
    
    <div v-if="uploadProgress.length > 0" class="upload-progress">
      <h4>Upload Progress:</h4>
      <div v-for="(progress, index) in uploadProgress" :key="index" class="progress-item">
        <span>{{ progress.fileName }}</span>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progress.percent + '%' }"></div>
        </div>
        <span>{{ progress.percent }}%</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FileUpload',
  emits: ['files-selected', 'upload-complete'],
  data() {
    return {
      selectedFiles: [],
      uploadProgress: [],
      isDragging: false
    }
  },
  methods: {
    selectFiles() {
      this.$refs.fileInput.click()
    },
    
    handleFileSelect(event) {
      const files = Array.from(event.target.files)
      this.addFiles(files)
    },
    
    handleDrop(event) {
      event.preventDefault()
      this.isDragging = false
      const files = Array.from(event.dataTransfer.files)
      this.addFiles(files)
    },
    
    handleDragOver(event) {
      event.preventDefault()
    },
    
    handleDragEnter(event) {
      event.preventDefault()
      this.isDragging = true
    },
    
    handleDragLeave(event) {
      event.preventDefault()
      this.isDragging = false
    },
    
    addFiles(files) {
      this.selectedFiles = [...this.selectedFiles, ...files]
      this.$emit('files-selected', this.selectedFiles)
    },
    
    removeFile(index) {
      this.selectedFiles.splice(index, 1)
      this.$emit('files-selected', this.selectedFiles)
    },
    
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    clearFiles() {
      this.selectedFiles = []
      this.uploadProgress = []
      this.$emit('files-selected', [])
    }
  }
}
</script>

<style scoped>
.file-upload {
  margin: 20px 0;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  background-color: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #3498db;
  background-color: #f0f9ff;
}

.upload-area.dragging {
  border-color: #3498db;
  background-color: #e8f4fd;
}

.upload-content {
  pointer-events: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.select-btn {
  background: none;
  border: none;
  color: #3498db;
  text-decoration: underline;
  cursor: pointer;
  font-size: inherit;
  pointer-events: auto;
}

.select-btn:hover {
  color: #2980b9;
}

.upload-hint {
  color: #666;
  font-size: 0.9rem;
  margin-top: 8px;
}

.file-list {
  margin-top: 20px;
}

.file-list h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.file-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.file-name {
  font-weight: 500;
  color: #2c3e50;
}

.file-size {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.remove-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  background-color: #c0392b;
}

.upload-progress {
  margin-top: 20px;
}

.upload-progress h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #3498db;
  transition: width 0.3s ease;
}
</style>