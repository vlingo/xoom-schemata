import '@babel/polyfill'
import Vue from 'vue'
import vuetify from '@/plugins/vuetify'
import App from './App.vue'
import router from './router'
import store from './store'
import AsyncComputed from 'vue-async-computed'



Vue.config.productionTip = false
Vue.use(AsyncComputed)

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount('#app')
