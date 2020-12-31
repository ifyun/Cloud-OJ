import Vue from "vue"
import App from "@/App"
import router from "@/router"
import ElementUI from "element-ui"
import "element-ui/lib/theme-chalk/index.css"
import axios from "axios"
import qs from "qs"
import md5 from "js-md5"

Vue.prototype.$axios = axios
Vue.prototype.qs = qs
Vue.prototype.$md5 = md5
Vue.config.productionTip = false

Vue.use(ElementUI)

/* eslint-disable no-new */
new Vue({
    el: "#app",
    router: router,
    render: (h) => h(App)
})