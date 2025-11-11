import axios from 'axios';
import { getXssMode } from '@/utils/xss';
import { handleAxiosError } from '@/utils/errors';
import { getToken, removeToken } from '@/utils/storage';
import router from '@/router';

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
});

// 全局 Axios 实例
// 说明：
// - VULN 模式：从 localStorage 读取 JWT 并放到 Authorization 头（教学用，故意不安全）
// - SECURE 模式：使用 HttpOnly Cookie（JS 无法读取），通过 withCredentials 让浏览器自动携带
// - 与后端双态配置保持一致，便于演示对比
// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const xssMode = getXssMode();
    
    if (xssMode === 'vuln') {
      // VULN 模式：从统一存储取 Token 并加到请求头（不安全示范）
      const token = getToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } else {
      // SECURE 模式：开启 withCredentials，浏览器自动携带 HttpOnly Cookie
      config.withCredentials = true;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 使用统一错误处理
    const apiError = handleAxiosError(error);
    
    // 401 未认证：清理 Token 并跳转登录页
    if (apiError.status === 401) {
      removeToken();
      
      // 定义公开访问的路由（无需认证）
      const publicRoutes = ['/login', '/register', '/feedback'];
      const currentPath = router.currentRoute.value.path;
      
      // 检查当前是否在公开路由或个人主页
      const isPublicRoute = publicRoutes.some(route => currentPath.startsWith(route));
      const isProfilePage = currentPath.startsWith('/profile/');
      
      // 只有在非公开路由且非个人主页时才跳转到登录页
      if (!isPublicRoute && !isProfilePage) {
        router.push('/login');
      }
    }
    
    return Promise.reject(apiError);
  }
);

export default instance;
