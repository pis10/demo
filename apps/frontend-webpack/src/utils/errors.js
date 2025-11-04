/**
 * 统一错误处理工具
 * 
 * 用于规范化 API 错误处理，提供友好的错误提示
 */

/**
 * API 错误类
 * 
 * 封装后端返回的错误信息，包含错误码、消息和状态码
 */
export class ApiError extends Error {
  /**
   * 构造函数
   * 
   * @param {string} code - 错误码（如 USER_ALREADY_EXISTS）
   * @param {string} message - 错误消息
   * @param {number} status - HTTP 状态码
   */
  constructor(code, message, status) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.status = status;
  }
}

/**
 * 错误消息映射表（中文友好提示）
 */
const ERROR_MESSAGES = {
  // 通用错误
  UNKNOWN_ERROR: '系统错误，请稍后重试',
  VALIDATION_FAILED: '表单验证失败，请检查输入',
  RESOURCE_NOT_FOUND: '请求的资源不存在',
  
  // 认证错误
  UNAUTHORIZED: '未登录或登录已过期，请重新登录',
  FORBIDDEN: '无权限访问此资源',
  INVALID_CREDENTIALS: '用户名或密码错误',
  INVALID_TOKEN: 'Token 无效或已过期',
  
  // 用户错误
  USER_ALREADY_EXISTS: '用户名或邮箱已被使用',
  USER_NOT_FOUND: '用户不存在',
  EMAIL_ALREADY_EXISTS: '邮箱已被使用',
  
  // 文章错误
  ARTICLE_NOT_FOUND: '文章不存在',
  
  // 评论错误
  COMMENT_NOT_FOUND: '评论不存在',
  
  // 反馈错误
  FEEDBACK_NOT_FOUND: '反馈不存在',
  
  // 网络错误
  NETWORK_ERROR: '网络连接失败，请检查网络设置',
  TIMEOUT_ERROR: '请求超时，请稍后重试',
};

/**
 * 获取友好的错误消息
 * 
 * @param {string|Error} error - 错误对象或错误码
 * @returns {string} 友好的错误消息
 */
export function getErrorMessage(error) {
  if (error instanceof ApiError) {
    return ERROR_MESSAGES[error.code] || error.message;
  }
  
  if (typeof error === 'string') {
    return ERROR_MESSAGES[error] || error;
  }
  
  if (error && error.message) {
    return error.message;
  }
  
  return ERROR_MESSAGES.UNKNOWN_ERROR;
}

/**
 * 处理 Axios 错误响应
 * 
 * @param {Error} error - Axios 错误对象
 * @returns {ApiError} 规范化的 API 错误对象
 */
export function handleAxiosError(error) {
  // 网络错误
  if (!error.response) {
    if (error.code === 'ECONNABORTED') {
      return new ApiError('TIMEOUT_ERROR', ERROR_MESSAGES.TIMEOUT_ERROR, 0);
    }
    return new ApiError('NETWORK_ERROR', ERROR_MESSAGES.NETWORK_ERROR, 0);
  }
  
  // 后端返回的错误
  const { data, status } = error.response;
  
  if (data && data.code) {
    // 使用后端返回的错误码和消息
    return new ApiError(
      data.code,
      data.message || ERROR_MESSAGES[data.code] || ERROR_MESSAGES.UNKNOWN_ERROR,
      status
    );
  }
  
  // 未知错误
  return new ApiError(
    'UNKNOWN_ERROR',
    data?.message || ERROR_MESSAGES.UNKNOWN_ERROR,
    status
  );
}
