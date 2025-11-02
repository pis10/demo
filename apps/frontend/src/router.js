// Vue Router configuration
// Includes admin routes guarded by a simple store-based check
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/Register.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/pages/ArticleDetail.vue')
  },
  {
    path: '/profile/:username',
    name: 'Profile',
    component: () => import('@/pages/Profile.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/pages/Search.vue')
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('@/pages/Feedback.vue')
  },
  {
    path: '/admin',
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/pages/admin/Dashboard.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/feedbacks',
    name: 'AdminFeedbackList',
    component: () => import('@/pages/admin/FeedbackList.vue'),
    meta: { requiresAdmin: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAdmin) {
    if (!authStore.isAuthenticated) {
      next('/login');
    } else if (!authStore.isAdmin) {
      next('/');
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
