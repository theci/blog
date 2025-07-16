<template>
  <div class="user-management">
    <h2>사용자 관리</h2>
    
    <div v-if="loading" class="loading">
      로딩 중...
    </div>
    
    <div v-else-if="error" class="error">
      {{ error }}
    </div>
    
    <div v-else class="users-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>사용자명</th>
            <th>이메일</th>
            <th>표시명</th>
            <th>역할</th>
            <th>가입일</th>
            <th>포인트</th>
            <th>정지 상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id" :class="{ suspended: user.suspended }">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.displayName }}</td>
            <td>
              <span :class="['role-badge', user.role.toLowerCase()]">
                {{ user.role }}
              </span>
            </td>
            <td>{{ formatDate(user.createdDate) }}</td>
            <td>{{ user.points }}</td>
            <td>
              <div v-if="user.suspended" class="suspension-info">
                <span class="suspended-badge">정지됨</span>
                <div class="suspension-details">
                  <p>사유: {{ user.suspensionReason }}</p>
                  <p v-if="!user.permanentSuspension">
                    종료일: {{ formatDate(user.suspensionEndDate) }}
                  </p>
                  <p v-else>영구 정지</p>
                </div>
              </div>
              <span v-else class="active-badge">활성</span>
            </td>
            <td>
              <div class="action-buttons">
                <div v-if="!user.suspended && user.role !== 'ADMIN'" class="suspend-controls">
                  <select v-model="suspensionDays[user.id]" class="days-select">
                    <option value="1">1일</option>
                    <option value="3">3일</option>
                    <option value="7">7일</option>
                    <option value="30">30일</option>
                    <option value="-1">영구정지</option>
                  </select>
                  <input 
                    v-model="suspensionReasons[user.id]" 
                    placeholder="정지 사유" 
                    class="reason-input"
                  />
                  <button 
                    @click="suspendUser(user.id)" 
                    class="suspend-btn"
                    :disabled="!suspensionReasons[user.id]"
                  >
                    정지
                  </button>
                </div>
                <div v-else-if="user.suspended" class="unsuspend-controls">
                  <button @click="unsuspendUser(user.id)" class="unsuspend-btn">
                    정지 해제
                  </button>
                </div>
                <div v-else class="admin-notice">
                  관리자 계정
                </div>
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
  name: 'UserManagement',
  data() {
    return {
      users: [],
      loading: false,
      error: null,
      suspensionDays: {},
      suspensionReasons: {}
    }
  },
  async mounted() {
    await this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      this.error = null
      
      try {
        const response = await adminService.getAllUsers()
        this.users = response.data
        
        // 각 사용자의 정지 상태를 병렬로 확인
        await Promise.all(this.users.map(async (user) => {
          // 기본값 설정
          this.suspensionDays[user.id] = 1
          this.suspensionReasons[user.id] = ''
          
          try {
            const suspensionResponse = await adminService.getUserSuspension(user.id)
            const suspension = suspensionResponse.data
            
            if (suspension) {
              user.suspended = suspension.isActive
              user.suspensionReason = suspension.reason
              user.suspensionEndDate = suspension.endDate
              user.permanentSuspension = suspension.endDate === null
            } else {
              user.suspended = false
            }
          } catch (error) {
            // 401 에러는 인증 문제이므로 상위로 전파
            if (error.response?.status === 401) {
              throw error
            } else {
              console.error('Error fetching suspension for user', user.id, error)
              user.suspended = false
            }
          }
        }))
      } catch (error) {
        this.error = '사용자 목록을 불러오는 중 오류가 발생했습니다.'
        console.error('Error loading users:', error)
      } finally {
        this.loading = false
      }
    },
    
    async suspendUser(userId) {
      if (!this.suspensionReasons[userId]) {
        alert('정지 사유를 입력해주세요.')
        return
      }
      
      try {
        await adminService.suspendUser(
          userId, 
          this.suspensionDays[userId], 
          this.suspensionReasons[userId]
        )
        
        alert('사용자가 정지되었습니다.')
        await this.loadUsers()
      } catch (error) {
        alert('사용자 정지 중 오류가 발생했습니다.')
        console.error('Error suspending user:', error)
      }
    },
    
    async unsuspendUser(userId) {
      if (!confirm('정지를 해제하시겠습니까?')) {
        return
      }
      
      try {
        await adminService.unsuspendUser(userId)
        alert('사용자 정지가 해제되었습니다.')
        await this.loadUsers()
      } catch (error) {
        alert('사용자 정지 해제 중 오류가 발생했습니다.')
        console.error('Error unsuspending user:', error)
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
.user-management {
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

.users-table {
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

tr.suspended {
  background-color: #ffebee;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
}

.role-badge.admin {
  background-color: #f39c12;
  color: white;
}

.role-badge.user {
  background-color: #95a5a6;
  color: white;
}

.suspended-badge {
  background-color: #e74c3c;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.active-badge {
  background-color: #27ae60;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.suspension-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.suspension-details {
  font-size: 12px;
  color: #666;
}

.suspension-details p {
  margin: 2px 0;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.suspend-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.days-select {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
}

.reason-input {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  width: 150px;
}

.suspend-btn {
  background-color: #e74c3c;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.suspend-btn:hover:not(:disabled) {
  background-color: #c0392b;
}

.suspend-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.unsuspend-btn {
  background-color: #27ae60;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.unsuspend-btn:hover {
  background-color: #229954;
}

.admin-notice {
  color: #7f8c8d;
  font-size: 12px;
  font-style: italic;
}
</style>