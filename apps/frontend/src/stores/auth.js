// Auth store
// - VULN mode: stores JWT in localStorage (intentionally unsafe)
// - SECURE mode: relies on HttpOnly cookie (no token in JS)
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from '@/api/axios';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null);
  const loading = ref(false);
  
  const isAuthenticated = computed(() => !!user.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN');
  
  async function login(credentials) {
    loading.value = true;
    try {
      const response = await axios.post('/auth/login', credentials);
      
      const xssMode = window.__XSS_MODE__ || import.meta.env.VITE_XSS_MODE;
      if (xssMode === 'vuln' && response.data.accessToken) {
        // VULN mode: Store JWT in localStorage
        localStorage.setItem('accessToken', response.data.accessToken);
      }
      // SECURE mode: JWT is in HttpOnly cookie, nothing to store
      
      await fetchCurrentUser();
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  }
  
  async function register(data) {
    loading.value = true;
    try {
      const response = await axios.post('/auth/register', data);
      
      const xssMode = window.__XSS_MODE__ || import.meta.env.VITE_XSS_MODE;
      if (xssMode === 'vuln' && response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
      }
      
      await fetchCurrentUser();
      return true;
    } catch (error) {
      console.error('Register failed:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  }
  
  async function logout() {
    try {
      await axios.post('/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    }
    
    localStorage.removeItem('accessToken');
    user.value = null;
  }
  
  async function fetchCurrentUser() {
    try {
      const response = await axios.get('/auth/me');
      user.value = response.data;
    } catch (error) {
      console.error('Fetch current user failed:', error);
      user.value = null;
    }
  }
  
  async function init() {
    const xssMode = window.__XSS_MODE__ || import.meta.env.VITE_XSS_MODE;
    
    // Only try to fetch user if we have a token (VULN) or in SECURE mode
    if (xssMode === 'vuln' && !localStorage.getItem('accessToken')) {
      return;
    }
    
    await fetchCurrentUser();
  }
  
  return {
    user,
    loading,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    fetchCurrentUser,
    init
  };
});
