import Vue from "vue"
import App from "@/App"
import router from "@/router"
import ElementUI from "element-ui"
import "element-ui/lib/theme-chalk/index.css"
import md5 from "js-md5"
import {siteSetting} from "@/util"

Vue.prototype.$md5 = md5
Vue.prototype.siteSetting = siteSetting
Vue.config.productionTip = false

Vue.use(ElementUI)

/* eslint-disable no-new */
new Vue({
    el: "#app",
    router,
    render: (h) => h(App)
})