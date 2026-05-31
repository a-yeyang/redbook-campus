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
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/" aria-label="Redbook Campus 首页">
        <span class="brand-mark">R</span>
        <span>
          <strong>Redbook Campus</strong>
          <em>校园图文社区</em>
        </span>
      </RouterLink>

      <nav>
        <RouterLink to="/">发现</RouterLink>
        <a href="/#publish">发布</a>
        <RouterLink to="/login">{{ hasToken ? '登录态' : '登录' }}</RouterLink>
      </nav>

      <div class="session-pill" :class="{ active: hasToken }">
        <span></span>
        {{ hasToken ? '已登录' : '未登录' }}
      </div>
    </header>

    <RouterView />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  padding-bottom: 42px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 18px 0 26px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #1d2733;
}

.brand-mark {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--color-primary), #f06b6d 58%, var(--color-warm));
  color: #ffffff;
  font-size: 1.25rem;
  font-weight: 900;
}

.brand strong {
  display: block;
  color: var(--color-heading);
  font-size: 1.12rem;
  font-weight: 900;
  line-height: 1.15;
}

.brand em {
  display: block;
  color: var(--color-muted);
  font-size: 0.78rem;
  font-style: normal;
  font-weight: 700;
}

nav {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

nav a {
  border-radius: 6px;
  color: var(--color-muted);
  font-weight: 700;
  padding: 8px 12px;
}

nav a:hover {
  background: var(--color-surface-muted);
  color: var(--color-heading);
}

nav a.router-link-exact-active {
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
}

.session-pill {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-height: 36px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  background: var(--color-surface);
  color: var(--color-muted);
  font-size: 0.86rem;
  font-weight: 800;
  padding: 0 12px;
}

.session-pill span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #a9b5c3;
}

.session-pill.active {
  border-color: rgba(19, 133, 117, 0.24);
  color: var(--color-accent);
}

.session-pill.active span {
  background: var(--color-accent);
}

@media (max-width: 520px) {
  .topbar {
    align-items: stretch;
    flex-wrap: wrap;
    padding-bottom: 18px;
  }

  nav {
    order: 3;
    width: 100%;
  }

  nav a {
    flex: 1;
    text-align: center;
  }

  .session-pill {
    margin-left: auto;
  }
}
</style>
