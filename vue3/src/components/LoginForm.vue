<template>
  <section class="login-panel">
    <div class="panel-heading">
      <p class="eyebrow">Redbook Campus</p>
      <h2>邮箱登录</h2>
      <p v-if="token" class="token-state">已写入登录态，可直接访问需要鉴权的接口。</p>
    </div>

    <div class="form-group">
      <label for="email">邮箱</label>
      <input id="email" v-model.trim="email" type="email" placeholder="请输入邮箱" autocomplete="email" />
    </div>

    <div v-if="loginType === 1" class="form-group">
      <label for="code">验证码</label>
      <div class="code-input">
        <input id="code" v-model.trim="code" type="text" placeholder="请输入验证码" autocomplete="one-time-code" />
        <button type="button" @click="getVerificationCode" :disabled="countdown > 0 || sendingCode">
          {{ countdown > 0 ? `${countdown}s` : sendingCode ? '发送中' : '获取验证码' }}
        </button>
      </div>
    </div>

    <div v-else class="form-group">
      <label for="password">密码</label>
      <input id="password" v-model="password" type="password" placeholder="请输入密码" autocomplete="current-password" />
    </div>

    <div class="login-type" role="tablist" aria-label="登录方式">
      <button type="button" :class="{ active: loginType === 1 }" @click="loginType = 1">验证码登录</button>
      <button type="button" :class="{ active: loginType === 2 }" @click="loginType = 2">密码登录</button>
    </div>

    <button class="login-btn" type="button" @click="handleLogin" :disabled="loading">
      {{ loading ? '登录中...' : '登录' }}
    </button>

    <p v-if="errorMessage" class="message error">{{ errorMessage }}</p>
    <p v-else-if="successMessage" class="message success">{{ successMessage }}</p>

    <div v-if="token" class="token-box">
      <span>当前 Token</span>
      <code>{{ tokenPreview }}</code>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import axios, { isAxiosError } from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8000'

const email = ref('2861173454@qq.com')
const code = ref('')
const password = ref('')
const loginType = ref<1 | 2>(1)
const countdown = ref(0)
const loading = ref(false)
const sendingCode = ref(false)
const token = ref('')
const errorMessage = ref('')
const successMessage = ref('')

const tokenPreview = computed(() => {
  if (!token.value) return ''
  if (token.value.length <= 18) return token.value
  return `${token.value.slice(0, 10)}...${token.value.slice(-8)}`
})

const saveToken = (value: string) => {
  token.value = value
  localStorage.setItem('authToken', value)
  localStorage.setItem('Authorization', `Bearer ${value}`)
  axios.defaults.headers.common.Authorization = `Bearer ${value}`
  window.dispatchEvent(new Event('auth-token-updated'))
}

const readError = (error: unknown, fallback: string) => {
  if (isAxiosError(error)) {
    return error.response?.data?.message || error.response?.data?.errorMessage || error.message || fallback
  }

  if (error instanceof Error) {
    return error.message
  }

  return fallback
}

const startCountdown = () => {
  countdown.value = 60
  const timer = window.setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      window.clearInterval(timer)
    }
  }, 1000)
}

const getVerificationCode = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (!email.value) {
    errorMessage.value = '请输入邮箱'
    return
  }

  sendingCode.value = true

  try {
    const response = await axios.post(
      `${API_BASE_URL}/auth/verification/code/send`,
      { email: email.value },
      { headers: { 'Content-Type': 'application/json' } },
    )

    if (response.data.success) {
      startCountdown()
      successMessage.value = '验证码已发送'
    } else {
      errorMessage.value = response.data.message || '验证码发送失败'
    }
  } catch (error: unknown) {
    errorMessage.value = readError(error, '验证码发送失败')
  } finally {
    sendingCode.value = false
  }
}

const handleLogin = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (!email.value) {
    errorMessage.value = '请输入邮箱'
    return
  }

  if (loginType.value === 1 && !code.value) {
    errorMessage.value = '请输入验证码'
    return
  }

  if (loginType.value === 2 && !password.value) {
    errorMessage.value = '请输入密码'
    return
  }

  loading.value = true

  try {
    const params =
      loginType.value === 1
        ? { email: email.value, code: code.value, type: 1 }
        : { email: email.value, password: password.value, type: 2 }

    const response = await axios.post(`${API_BASE_URL}/auth/login`, params)

    if (response.data.success && response.data.data) {
      saveToken(response.data.data)
      successMessage.value = '登录成功，Token 已保存'
    } else {
      errorMessage.value = response.data.message || '登录失败'
    }
  } catch (error: unknown) {
    errorMessage.value = readError(error, '登录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const savedToken = localStorage.getItem('authToken')
  if (savedToken) {
    saveToken(savedToken)
    successMessage.value = '已读取本地登录态'
  }
})
</script>

<style scoped>
.login-panel {
  width: min(100%, 440px);
  margin: 0 auto;
  padding: 28px;
  border: 1px solid rgba(28, 36, 48, 0.1);
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 18px 40px rgba(30, 38, 50, 0.08);
}

.panel-heading {
  margin-bottom: 24px;
}

.eyebrow {
  margin-bottom: 4px;
  color: #d94858;
  font-size: 0.78rem;
  font-weight: 700;
  text-transform: uppercase;
}

h2 {
  margin: 0;
  color: #1d2733;
  font-size: 1.75rem;
  font-weight: 700;
}

.token-state {
  margin-top: 8px;
  color: #15836d;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 6px;
  color: #465264;
  font-weight: 600;
}

input {
  width: 100%;
  min-height: 44px;
  padding: 0 12px;
  border: 1px solid #cfd6df;
  border-radius: 6px;
  color: #1d2733;
  font-size: 1rem;
}

input:focus {
  border-color: #d94858;
  outline: 3px solid rgba(217, 72, 88, 0.16);
}

.code-input {
  display: grid;
  grid-template-columns: 1fr 116px;
  gap: 10px;
}

button {
  min-height: 42px;
  border: 0;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 700;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.62;
}

.code-input button {
  background: #1b8a7a;
  color: #ffffff;
}

.login-type {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin: 12px 0 18px;
  padding: 4px;
  border-radius: 8px;
  background: #f2f5f7;
}

.login-type button {
  background: transparent;
  color: #5e6877;
}

.login-type button.active {
  background: #ffffff;
  color: #d94858;
  box-shadow: 0 1px 8px rgba(20, 27, 38, 0.08);
}

.login-btn {
  width: 100%;
  background: #d94858;
  color: #ffffff;
  font-size: 1rem;
}

.message {
  margin-top: 14px;
  line-height: 1.5;
}

.message.error {
  color: #bf2f3c;
}

.message.success {
  color: #15836d;
}

.token-box {
  display: grid;
  gap: 6px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid #edf0f3;
  color: #5e6877;
}

code {
  overflow-wrap: anywhere;
  color: #1d2733;
  font-family: Consolas, 'Courier New', monospace;
}

@media (max-width: 520px) {
  .login-panel {
    padding: 22px;
  }

  .code-input {
    grid-template-columns: 1fr;
  }
}
</style>
