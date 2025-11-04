/**
 * 统一存储工具
 * 
 * 封装 Token 存储逻辑，支持双模式（localStorage vs Cookie）
 */

import { useConfigStore } from '../stores/config';

/**
 * 存储 Token
 * 
 * @param {string} token - JWT Token
 */
export function setToken(token) {
  const configStore = useConfigStore();
  
  // VULN 模式：存储到 localStorage
  if (configStore.xssMode === 'vuln') {
    localStorage.setItem('accessToken', token);
  }
  // SECURE 模式：不存储（使用 HttpOnly Cookie）
  // Cookie 由后端自动设置，前端无需处理
}

/**
 * 获取 Token
 * 
 * @returns {string|null} JWT Token 或 null
 */
export function getToken() {
  const configStore = useConfigStore();
  
  // VULN 模式：从 localStorage 读取
  if (configStore.xssMode === 'vuln') {
    return localStorage.getItem('accessToken');
  }
  
  // SECURE 模式：Token 在 HttpOnly Cookie 中，JS 无法访问
  return null;
}

/**
 * 移除 Token（退出登录）
 */
export function removeToken() {
  const configStore = useConfigStore();
  
  // VULN 模式：清除 localStorage
  if (configStore.xssMode === 'vuln') {
    localStorage.removeItem('accessToken');
  }
  
  // SECURE 模式：Cookie 由后端清除，前端无需处理
}

/**
 * 检查是否已登录
 * 
 * @returns {boolean} 是否已登录
 */
export function isAuthenticated() {
  const configStore = useConfigStore();
  
  // VULN 模式：检查 localStorage
  if (configStore.xssMode === 'vuln') {
    return !!localStorage.getItem('accessToken');
  }
  
  // SECURE 模式：假定已登录（实际由后端验证 Cookie）
  // 前端无法直接判断，需要通过 API 请求验证
  return true;
}
