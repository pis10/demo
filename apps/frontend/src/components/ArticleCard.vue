<template>
  <div class="article-card card" @click="goToArticle">
    <div class="article-header">
      <h3 class="article-title">{{ article.title }}</h3>
      <div class="article-meta">
        <router-link 
          :to="`/profile/${article.author.username}`" 
          class="author-link"
          @click.stop>
          <img :src="article.author.avatarUrl" :alt="article.author.username" class="author-avatar" />
          <span>{{ article.author.username }}</span>
        </router-link>
        <span class="date">{{ formatDate(article.publishedAt) }}</span>
      </div>
    </div>
    
    <p class="article-excerpt">{{ article.excerpt }}</p>
    
    <div class="article-footer">
      <div class="tags">
        <span 
          v-for="tag in article.tags" 
          :key="tag.id" 
          class="tag"
          :style="{ background: tag.color + '20', color: tag.color }">
          {{ tag.name }}
        </span>
      </div>
      <div class="stats">
        <span class="stat">
          <el-icon><View /></el-icon>
          {{ article.likesCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
});

const router = useRouter();

const goToArticle = () => {
  router.push(`/article/${props.article.id}`);
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};
</script>

<style scoped>
.article-card {
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all var(--transition-fast);
}

.article-card:hover {
  border-color: var(--color-primary);
}

.article-header {
  margin-bottom: var(--spacing-md);
}

.article-title {
  font-size: 1.5rem;
  margin-bottom: var(--spacing-sm);
  color: var(--color-text-primary);
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}

.author-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.author-link:hover {
  color: var(--color-primary);
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.article-excerpt {
  flex: 1;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
}

.tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.stats {
  display: flex;
  gap: var(--spacing-md);
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}
</style>
