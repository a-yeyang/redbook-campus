import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const url = new URL(window.location.href)
const tokenFromQuery = url.searchParams.get('token')

if (tokenFromQuery) {
  localStorage.setItem('authToken', tokenFromQuery)
  localStorage.setItem('Authorization', `Bearer ${tokenFromQuery}`)
  url.searchParams.delete('token')
  window.history.replaceState({}, document.title, `${url.pathname}${url.search}${url.hash}`)
}

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
