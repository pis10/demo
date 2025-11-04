<template>
  <div class="article-detail-page">
    <div class="container" v-if="!loading && article">
      <article class="article-content">
        <header class="article-header">
          <h1>{{ article.title }}</h1>
          <div class="article-meta">
            <router-link :to="`/profile/${article.author.username}`" class="author-info">
              <img :src="article.author.avatarUrl" :alt="article.author.username" />
              <div>
                <div class="author-name">{{ article.author.username }}</div>
                <div class="publish-date">{{ formatDate(article.publishedAt) }}</div>
              </div>
            </router-link>
            <div class="article-stats">
              <span><el-icon><View /></el-icon> {{ article.likesCount }}</span>
            </div>
          </div>
        </header>
        
        <div class="article-body">
          <!-- XSS 渲染说明：
               - VULN：文章正文原样渲染（可执行脚本）
               - SECURE：使用 DOMPurify 净化，阻断脚本执行 -->
          <div class="content"
               v-if="configStore.xssMode === 'vuln'"
               v-html="article.contentHtml"></div>
          <div class="content"
               v-else
               v-html="pure(article.contentHtml)"></div>
        </div>
        
        <div class="article-tags">
          <span v-for="tag in article.tags" :key="tag.id" class="tag" :style="{ background: tag.color + '20', color: tag.color }">
            {{ tag.name }}
          </span>
        </div>
      </article>
      
      <section class="comments-section">
        <h3>评论 ({{ comments.length }})</h3>
        
        <!-- 评论输入框：只在登录后显示 -->
        <div v-if="authStore.isAuthenticated" class="comment-form card">
          <el-form @submit.prevent="submitComment">
            <el-form-item>
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="3"
                placeholder="写下你的评论..."
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" :loading="submitting" :disabled="!newComment.trim()">
                发表评论
              </el-button>
            </el-form-item>
          </el-form>
          
          <!-- XSS 提示（场景 3 评论蠕虫）：仅在 VULN 模式展示攻击示例 -->
          <div class="demo-info" v-if="configStore.xssMode === 'vuln'">
            <h4>⚠️ 评论蠕虫演示（场景 3）</h4>
            <p>当前处于 VULN 模式，评论内容将不经过滤直接存储。</p>
            <p style="margin-top: 8px;">示例 Payload（自传播到 3 篇文章）：</p>
            <code style="font-size: 0.75rem;">&lt;img src=x onerror="/*见文档完整蠕虫代码*/alert('评论 XSS')"&gt;</code>
            <p style="margin-top: 8px; font-size: 0.85em; color: var(--color-text-muted);">
              💡 完整 Payload 请查看《XSS演示场景说明.md》
            </p>
          </div>
        </div>
        <div v-else class="login-prompt card">
          <p>登录后才能发表评论。 <router-link to="/login">立即登录</router-link></p>
        </div>
        
        <!-- 评论列表 -->
        <div v-for="comment in comments" :key="comment.id" class="comment card">
          <div class="comment-author">
            <img :src="comment.user.avatarUrl" :alt="comment.user.username" />
            <div>
              <div class="comment-author-name">{{ comment.user.username }}</div>
              <div class="comment-date">{{ formatDate(comment.createdAt) }}</div>
            </div>
          </div>
          <!-- XSS 渲染说明：
               - VULN：评论原样渲染（存储型 XSS 示例）
               - SECURE：渲染前先净化 -->
          <div class="comment-content"
               v-if="configStore.xssMode === 'vuln'"
               v-html="comment.contentHtml"></div>
          <div class="comment-content"
               v-else
               v-html="pure(comment.contentHtml)"></div>
        </div>
      </section>
    </div>
    
    <div v-else-if="loading" class="loading">
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script setup>
// 文章详情与评论：演示两种模式下的内容渲染差异
// - VULN：直接 v-html 渲染
// - SECURE：通过 DOMPurify 净化后再渲染，防止 XSS
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from '@/api/axios';
import { useConfigStore } from '@/stores/config';
import { useAuthStore } from '@/stores/auth';
import { pure } from '@/utils/xss';
import { ElMessage } from 'element-plus';

const route = useRoute();
const configStore = useConfigStore();
const authStore = useAuthStore();
const article = ref(null);
const comments = ref([]);
const loading = ref(true);
const newComment = ref('');
const submitting = ref(false);

const fetchArticle = async () => {
  try {
    const response = await axios.get(`/articles/${route.params.id}`);
    article.value = response.data;
    
    const commentsResponse = await axios.get(`/articles/${route.params.id}/comments`);
    comments.value = commentsResponse.data;
  } catch (error) {
    console.error('Failed to fetch article:', error);
  } finally {
    loading.value = false;
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) return;
  
  submitting.value = true;
  try {
    const response = await axios.post(`/articles/${route.params.id}/comments`, {
      content: newComment.value
    });
    
    // 添加新评论到列表首位
    comments.value.unshift(response.data);
    newComment.value = '';
    ElMessage.success('评论发表成功！');
  } catch (error) {
    console.error('Failed to submit comment:', error);
    ElMessage.error('评论发表失败，请重试');
  } finally {
    submitting.value = false;
  }
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

onMounted(() => {
  fetchArticle();
});
</script>

<style scoped>
.article-detail-page {
  padding: var(--spacing-2xl) 0;
  min-height: calc(100vh - 80px);
}

.article-content {
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-xl);
}

.article-header {
  margin-bottom: var(--spacing-2xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--color-border);
}

.article-header h1 {
  font-size: 2.5rem;
  margin-bottom: var(--spacing-lg);
  line-height: 1.2;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--color-text-primary);
}

.author-info img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px solid var(--color-primary);
}

.author-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.publish-date {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.article-stats {
  color: var(--color-text-muted);
  display: flex;
  gap: var(--spacing-lg);
}

.article-body {
  margin-bottom: var(--spacing-2xl);
}

.content {
  font-size: 1.1rem;
  line-height: 1.8;
  color: var(--color-text-primary);
}

.content :deep(h2),
.content :deep(h3) {
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-md);
}

.content :deep(ul),
.content :deep(ol) {
  margin-left: var(--spacing-xl);
  margin-bottom: var(--spacing-md);
}

.content :deep(li) {
  margin-bottom: var(--spacing-sm);
}

.article-tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tag {
  padding: 6px 14px;
  border-radius: 14px;
  font-size: 0.9rem;
  font-weight: 500;
}

.comments-section {
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  padding: var(--spacing-2xl);
}

.comments-section h3 {
  margin-bottom: var(--spacing-xl);
}

.comment-form {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
}

.comment-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-md);
}

.comment-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.comment-form .demo-info {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md);
  background: rgba(251, 146, 60, 0.1);
  border: 2px solid rgba(251, 146, 60, 0.3);
  border-radius: var(--radius-md);
}

.comment-form .demo-info h4 {
  color: var(--color-accent);
  margin: 0 0 var(--spacing-sm) 0;
  font-size: 0.95rem;
}

.comment-form .demo-info p {
  color: var(--color-text-secondary);
  margin: 0;
  font-size: 0.85rem;
}

.comment-form .demo-info code {
  display: block;
  margin-top: var(--spacing-sm);
  padding: var(--spacing-sm);
  background: var(--color-bg-elevated);
  border-radius: var(--radius-sm);
  overflow-x: auto;
  color: var(--color-accent);
}

.login-prompt {
  padding: var(--spacing-xl);
  text-align: center;
  margin-bottom: var(--spacing-xl);
  background: var(--color-bg-secondary);
  border: 1px dashed var(--color-border);
}

.login-prompt p {
  margin: 0;
  color: var(--color-text-secondary);
}

.login-prompt a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
}

.login-prompt a:hover {
  text-decoration: underline;
}

.comment {
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-lg);
}

.comment:hover {
  transform: none;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.comment-author img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.comment-author-name {
  font-weight: 600;
  margin-bottom: 2px;
}

.comment-date {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

.comment-content {
  color: var(--color-text-secondary);
  line-height: 1.6;
}
</style>
