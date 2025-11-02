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

- 页面左上角徽章点击切换（推荐）。切换后**认证状态保持不变**，无需重新登录。
- 或修改配置并重启：
  - 后端 `apps/backend/src/main/resources/application.yml`: `xss.mode: vuln|secure`
  - 前端 `apps/frontend/.env`: `VITE_XSS_MODE=vuln|secure`

对比要点：
- VULN：JWT 存 localStorage；输入直接渲染；XSS 可成功
- SECURE：JWT 为 HttpOnly Cookie；后端转义 + DOMPurify；XSS 被拦截

## 测试账号

- admin / Admin#2025（管理员，用于 L3 后台查看）
- attacker / Attacker#2025（用于 L2 恶意 Bio）
- alice / Admin#2025（普通用户）

## 场景速览（L0~L3）

| 场景 | 类型 | 目标 | 入口 |
|------|------|------|------|
| L0 | 反射型 | 代码执行 | 搜索 URL 参数 |
| L1 | 反射型 | 窃取 JWT | 搜索 + localStorage |
| L2 | 存储型 | 伪装登录钓鱼 | 用户 Bio |
| L3 | 盲 XSS | 管理员凭证窃取 | 反馈 → 后台查看 |

详细步骤与对比见：`XSS演示场景说明.md`

### 快速体验（示例）
- L0：`/search?q=<script>alert('XSS!')</script>`
- L1：登录后：`/search?q=<script>console.log(localStorage.getItem('accessToken'))</script>`
- L2：访问 `/profile/attacker` 观察伪装登录框
- L3：提交 `/feedback` 后，用 admin 在 `/admin/feedbacks` 打开详情

## 技术栈
- 前端：Vue 3、Vite、Element Plus、Pinia、Axios
- 后端：Spring Boot 3、Spring Security、JPA/Hibernate、MySQL 8
- 安全：HttpOnly Cookie、后端转义、DOMPurify 白名单

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
