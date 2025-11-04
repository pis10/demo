<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <el-icon size="32"><Tools /></el-icon>
        <h2>管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#121826"
        text-color="#E2E8F0"
        active-text-color="#22D3EE">
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/feedbacks">
          <el-icon><ChatDotRound /></el-icon>
          <span>反馈管理</span>
        </el-menu-item>
        <el-menu-item index="/" divided>
          <el-icon><HomeFilled /></el-icon>
          <span>返回前台</span>
        </el-menu-item>
      </el-menu>
    </aside>
    
    <main class="admin-main">
      <div class="admin-content">
        <div class="dashboard-header">
          <h1>仪表盘</h1>
          <div class="mode-badge">
            <span class="badge" :class="configStore.xssMode === 'vuln' ? 'badge-vuln' : 'badge-secure'">
              {{ configStore.xssMode.toUpperCase() }} 模式
            </span>
          </div>
        </div>
        
        <div v-if="!loading" class="stats-grid">
          <div class="stat-card card">
            <div class="stat-icon" style="background: rgba(34, 211, 238, 0.2)">
              <el-icon size="32" color="#22D3EE"><View /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayVisits }}</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
          
          <div class="stat-card card">
            <div class="stat-icon" style="background: rgba(251, 146, 60, 0.2)">
              <el-icon size="32" color="#FB923C"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.newUsers }}</div>
              <div class="stat-label">新增用户</div>
            </div>
          </div>
          
          <div class="stat-card card">
            <div class="stat-icon" style="background: rgba(34, 197, 94, 0.2)">
              <el-icon size="32" color="#22C55E"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalArticles }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
          
          <div class="stat-card card">
            <div class="stat-icon" style="background: rgba(239, 68, 68, 0.2)">
              <el-icon size="32" color="#EF4444"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingFeedbacks }}</div>
              <div class="stat-label">待处理反馈</div>
            </div>
          </div>
        </div>
        
        <div v-else class="loading">
          <div class="spinner"></div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useConfigStore } from '@/stores/config';
import axios from '@/api/axios';

const route = useRoute();
const configStore = useConfigStore();

const stats = ref({
  todayVisits: 0,
  newUsers: 0,
  totalArticles: 0,
  pendingFeedbacks: 0
});
const loading = ref(true);

const activeMenu = computed(() => route.path);

const fetchStats = async () => {
  try {
    const response = await axios.get('/admin/dashboard');
    stats.value = response.data;
  } catch (error) {
    console.error('Failed to fetch stats:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg-base);
}

.admin-sidebar {
  width: 250px;
  background: var(--color-bg-card);
  border-right: 1px solid var(--color-border);
  padding: var(--spacing-lg);
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  color: var(--color-primary);
}

.admin-brand h2 {
  font-size: 1.25rem;
}

.admin-main {
  flex: 1;
  margin-left: 250px;
  padding: var(--spacing-2xl);
}

.admin-content {
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2xl);
}

.dashboard-header h1 {
  font-size: 2.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.stat-label {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}
</style>
