import { createApp } from "vue"
import axios from "axios"
import router from "@/router"
import store from "@/store"
import "vfonts/Inter.css"
import "vfonts/FiraCode.css"
import App from "./App.vue"
import "./style.scss"

axios.defaults.validateStatus = () => true
createApp(App).use(router).use(store).mount("#app")
