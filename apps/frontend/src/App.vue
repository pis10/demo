<template>
  <div id="app" class="dark">
    <AppHeader v-if="!isAdminRoute" />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';

const route = useRoute();

const isAdminRoute = computed(() => route.path.startsWith('/admin'));
</script>

<style scoped>
.main-content {
  flex: 1;
  width: 100%;
}
</style>
