<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { RouterLink, RouterView } from 'vue-router'

const hasToken = ref(Boolean(localStorage.getItem('authToken')))

const syncTokenState = () => {
  hasToken.value = Boolean(localStorage.getItem('authToken'))
}

onMounted(() => {
  window.addEventListener('storage', syncTokenState)
  window.addEventListener('auth-token-updated', syncTokenState)
})

onUnmounted(() => {
  window.removeEventListener('storage', syncTokenState)
  window.removeEventListener('auth-token-updated', syncTokenState)
})
</script>

<template>
  <div class="xhs-shell">
    <aside class="xhs-sidebar" aria-label="主导航">
      <RouterLink class="brand" to="/" aria-label="BIT Redbook 首页">
        <span class="brand-symbol">
          <img src="/bit-emblem-red.svg" alt="北京理工大学校徽" />
        </span>
        <strong>BIT Redbook</strong>
      </RouterLink>

      <nav class="side-nav">
        <RouterLink to="/" class="nav-item">
          <span class="nav-icon">⌂</span>
          <span>发现</span>
        </RouterLink>
        <a href="/#trend" class="nav-item">
          <span class="nav-icon">▶</span>
          <span>RED</span>
        </a>
        <a href="/#live" class="nav-item">
          <span class="nav-icon">◉</span>
          <span>直播</span>
        </a>
        <a href="/#publish" class="nav-item">
          <span class="nav-icon">＋</span>
          <span>发布</span>
        </a>
        <RouterLink to="/login" class="nav-item">
          <span class="nav-icon">◇</span>
          <span>{{ hasToken ? '登录态' : '登录' }}</span>
        </RouterLink>
      </nav>

      <RouterLink v-if="!hasToken" to="/login" class="login-button">登录</RouterLink>
      <div v-else class="status-card">
        <span></span>
        已登录
      </div>

      <div class="sidebar-card">
        <strong>Campus Pro</strong>
        <p>AI paper notes, lab posters, methods, benchmarks.</p>
      </div>

      <div class="sidebar-bottom">
        <a href="/#more" class="nav-item subtle">
          <span class="nav-icon">☰</span>
          <span>更多</span>
        </a>
        <a href="/#about" class="nav-item subtle">
          <span class="nav-icon">ⓘ</span>
          <span>关于我们</span>
        </a>
      </div>
    </aside>

    <main class="xhs-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.xhs-shell {
  min-height: 100vh;
}

.xhs-sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 30;
  display: flex;
  width: 260px;
  flex-direction: column;
  gap: 18px;
  border-right: 1px solid #f0f0f0;
  background: rgba(255, 255, 255, 0.96);
  padding: 34px 24px 28px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  width: fit-content;
}

.brand-symbol {
  display: grid;
  width: 50px;
  height: 50px;
  place-items: center;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(215, 25, 32, 0.14);
}

.brand-symbol img {
  width: 44px;
  height: 44px;
  display: block;
}

.brand strong {
  color: #111111;
  font-size: 1.04rem;
  font-weight: 950;
  letter-spacing: 0;
}

.side-nav,
.sidebar-bottom {
  display: grid;
  gap: 8px;
}

.side-nav {
  margin-top: 18px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 48px;
  border-radius: 999px;
  color: #222222;
  font-size: 1.02rem;
  font-weight: 760;
  padding: 0 18px;
}

.nav-item:hover,
.nav-item.router-link-exact-active {
  background: #f5f5f5;
}

.nav-icon {
  display: grid;
  width: 22px;
  height: 22px;
  place-items: center;
  color: #222222;
  font-size: 1rem;
  font-weight: 900;
}

.login-button {
  display: grid;
  min-height: 48px;
  place-items: center;
  border-radius: 999px;
  background: #ff2442;
  color: #ffffff;
  font-size: 1rem;
  font-weight: 900;
}

.status-card,
.sidebar-card {
  border: 1px solid #eeeeee;
  border-radius: 14px;
  background: #ffffff;
}

.status-card {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 46px;
  color: #087f5b;
  font-weight: 900;
}

.status-card span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #12b886;
}

.sidebar-card {
  display: grid;
  gap: 8px;
  padding: 16px;
}

.sidebar-card strong {
  color: #111111;
  font-weight: 950;
}

.sidebar-card p {
  color: #777777;
  font-size: 0.86rem;
  line-height: 1.6;
}

.sidebar-bottom {
  margin-top: auto;
}

.nav-item.subtle {
  color: #444444;
}

.xhs-main {
  min-height: 100vh;
  padding-left: 260px;
}

@media (max-width: 900px) {
  .xhs-sidebar {
    position: sticky;
    top: 0;
    flex-direction: row;
    width: 100%;
    height: auto;
    align-items: center;
    overflow-x: auto;
    padding: 12px 16px;
  }

  .side-nav {
    display: flex;
    margin-top: 0;
  }

  .sidebar-card,
  .sidebar-bottom,
  .status-card,
  .login-button {
    display: none;
  }

  .xhs-main {
    padding-left: 0;
  }
}
</style>
