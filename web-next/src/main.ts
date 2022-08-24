import { createApp } from "vue"
import router from "@/router"
import store from "@/store"
import "vfonts/Inter.css"
import "vfonts/FiraCode.css"
import App from "./App.vue"
import "./style.scss"

createApp(App).use(router).use(store).mount("#app")
