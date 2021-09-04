import Vue from "vue"
import App from "@/App"
import router from "@/router"
import ElementUI from "element-ui"
import "element-ui/lib/theme-chalk/index.css"
import {siteSetting} from "@/util"

const eventbus = new Vue()

Object.defineProperties(Vue.prototype, {
    $bus: {
        get: function () {
            return eventbus
        }
    },
    $siteSetting: {
        get: function () {
            return siteSetting
        }
    }
})

Vue.config.productionTip = false

Vue.use(ElementUI)

/* eslint-disable no-new */
new Vue({
    el: "#app",
    router,
    render: (h) => h(App)
})