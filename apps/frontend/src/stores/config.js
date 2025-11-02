// Config store
// - Holds current XSS mode fetched from backend
// - Exposes switchMode to flip between VULN and SECURE for demos
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from '@/api/axios';

export const useConfigStore = defineStore('config', () => {
  const xssMode = ref('vuln');
  const loading = ref(false);
  
  async function fetchConfig() {
    loading.value = true;
    try {
      const response = await axios.get('/config');
      xssMode.value = response.data.xssMode || 'vuln';
    } catch (error) {
      console.error('Fetch config failed:', error);
    } finally {
      loading.value = false;
    }
  }
  
  async function switchMode(newMode) {
    loading.value = true;
    try {
      const response = await axios.post('/config/mode', { mode: newMode });
      xssMode.value = response.data.xssMode || newMode;
      return true;
    } catch (error) {
      console.error('Switch mode failed:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  }
  
  return {
    xssMode,
    loading,
    fetchConfig,
    switchMode
  };
});
