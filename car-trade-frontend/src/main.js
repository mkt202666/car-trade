import { createApp } from 'vue'
import uviewPlus from 'uview-plus'
import App from './App'
import store from './store'
import request, { setupHttp } from './api/request'

const app = createApp(App)

app.use(uviewPlus)
app.use(store)

setupHttp()
uni.$u.api = request
app.config.globalProperties.$u.api = request

app.mount('#app')
