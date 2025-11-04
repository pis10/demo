<template>
  <div class="auth-page">
    <div class="auth-container card">
      <h2>登录</h2>
      <p class="subtitle">欢迎回到 TechBlog</p>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="handleSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="auth-footer">
        还没有账号？ <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref();
const loading = ref(false);

const form = ref({
  username: '',
  password: ''
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  
  loading.value = true;
  try {
    await authStore.login(form.value);
    ElMessage.success('登录成功！');
    router.push('/');
  } catch (error) {
    ElMessage.error(error.response?.data?.error || error.response?.data?.message || '登录失败,请检查用户名和密码');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  background: linear-gradient(135deg, #0B1220 0%, #1e293b 100%);
}

.auth-container {
  width: 100%;
  max-width: 450px;
  padding: var(--spacing-2xl);
}

.auth-container h2 {
  text-align: center;
  margin-bottom: var(--spacing-sm);
  font-size: 2rem;
}

.subtitle {
  text-align: center;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xl);
}

.submit-btn {
  width: 100%;
}

.auth-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  color: var(--color-text-secondary);
}

.auth-footer a {
  color: var(--color-primary);
  font-weight: 500;
}
</style>
