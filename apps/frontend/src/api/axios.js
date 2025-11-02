import axios from 'axios';
import { getXssMode } from '@/utils/xss';

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
});

// Global Axios instance for API calls
// Notes:
// - In VULN mode we attach JWT from localStorage (intentionally unsafe for demo)
// - In SECURE mode we rely on HttpOnly cookies (JS cannot read them)
// - This mirrors the backend's dual-mode behavior to enable side-by-side demos
// Request interceptor
instance.interceptors.request.use(
  (config) => {
    const xssMode = getXssMode();
    
    if (xssMode === 'vuln') {
      // VULN mode: Add JWT from localStorage to Authorization header (unsafe by design)
      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } else {
      // SECURE mode: Let browser automatically send HttpOnly cookie
      config.withCredentials = true;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Unauthorized - clear auth and redirect to login
      localStorage.removeItem('accessToken');
      // Prevent redirect loop - only redirect if not already on login page
      if (!window.location.pathname.includes('/login') && 
          !window.location.pathname.includes('/profile/')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default instance;
