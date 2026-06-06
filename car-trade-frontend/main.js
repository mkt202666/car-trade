import Vue from 'vue'
import App from './App'
import uView from 'uview-ui'
import store from './store'
import request from './api/request'

Vue.use(uView)

Vue.prototype.$store = store
Vue.prototype.$u.api = request

App.mpType = 'app'

const app = new Vue({ store, ...App })
app.$mount()
