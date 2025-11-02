// App entry: sets up Vue app, Pinia, router, ElementPlus
// Fetches server-side config to expose current XSS mode
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import App from './App.vue';
import router from './router';
import './theme/dark.scss';
import { useConfigStore } from '@/stores/config';
import { useAuthStore } from '@/stores/auth';

const app = createApp(App);
const pinia = createPinia();

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(pinia);
app.use(router);
app.use(ElementPlus, { size: 'default', zIndex: 3000 });

// Initialize stores
const configStore = useConfigStore();
const authStore = useAuthStore();

// Fetch config and sync to global variable
await configStore.fetchConfig();
window.__XSS_MODE__ = configStore.xssMode;

// Initialize auth state
await authStore.init();

app.mount('#app');
