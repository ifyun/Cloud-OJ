import Vue from 'vue'
import Login from './Login.vue';
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import md5 from 'js-md5';
import axios from 'axios'
import qs from 'qs'

Vue.prototype.$axios = axios
Vue.prototype.$md5 = md5
Vue.prototype.qs = qs

Vue.use(ElementUI);

new Vue({
    render: h => h(Login),
}).$mount('#login')