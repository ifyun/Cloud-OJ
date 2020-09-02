import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import Index from './Index.vue'
import axios from 'axios'
import qs from 'qs'

Vue.prototype.$axios = axios
Vue.prototype.qs = qs

Vue.use(ElementUI);

new Vue({
    render: h => h(Index),
}).$mount('#index')
