<template>
  <div class="home-page">
    <div class="container">
      <div class="page-header">
        <h1>XSSBlog</h1>
        <p class="subtitle">Cross-Site Scripting Demo Blog - XSS 漏洞演示靶场</p>
      </div>
      
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
      </div>
      
      <div v-else class="articles-grid">
        <article-card 
          v-for="article in articles" 
          :key="article.id" 
          :article="article" 
        />
      </div>
      
      <div v-if="!loading && articles.length === 0" class="empty-state">
        <p>暂无文章</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/api/axios';
import ArticleCard from '@/components/ArticleCard.vue';

const articles = ref([]);
const loading = ref(true);

const fetchArticles = async () => {
  try {
    const response = await axios.get('/articles?page=0&size=20');
    articles.value = response.data.content || [];
  } catch (error) {
    console.error('Failed to fetch articles:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchArticles();
});
</script>

<style scoped>
.home-page {
  padding: var(--spacing-2xl) 0;
  min-height: calc(100vh - 80px);
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
}

.page-header h1 {
  font-size: 3rem;
  margin-bottom: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1.25rem;
  color: var(--color-text-secondary);
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: var(--spacing-lg);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
}
</style>
