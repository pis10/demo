import DOMPurify from 'dompurify';

/**
 * Sanitize HTML using DOMPurify with whitelist
 * Used in SECURE mode to prevent XSS
 */
export const pure = (html) => {
  if (!html) return '';
  
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: [
      'b', 'i', 'em', 'strong', 'a', 'p', 'code', 'pre', 
      'ul', 'ol', 'li', 'img', 'h1', 'h2', 'h3', 'h4', 
      'blockquote', 'br', 'hr', 'span', 'div'
    ],
    ALLOWED_ATTR: {
      'a': ['href', 'title', 'target', 'rel'],
      'img': ['src', 'alt', 'width', 'height'],
      'code': ['class'],
      'pre': ['class'],
      'span': ['class'],
      'div': ['class']
    },
    ALLOWED_URI_REGEXP: /^(?:(?:(?:f|ht)tps?|mailto|tel|callto|cid|xmpp):|[^a-z]|[a-z+.\-]+(?:[^a-z+.\-:]|$))/i
  });
};

/**
 * Get current XSS mode from config store
 * Falls back to environment variable if store not available
 */
export const getXssMode = () => {
  // Dynamic mode from store if available
  if (typeof window !== 'undefined' && window.__XSS_MODE__) {
    return window.__XSS_MODE__;
  }
  return import.meta.env.VITE_XSS_MODE || 'vuln';
};

/**
 * Check if current mode is VULN
 */
export const isVulnMode = () => {
  return getXssMode() === 'vuln';
};

/**
 * Check if current mode is SECURE
 */
export const isSecureMode = () => {
  return getXssMode() === 'secure';
};
