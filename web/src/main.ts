import router from "@/router"
import { createPinia } from "pinia"
import "vfonts/FiraCode.css"
import "vfonts/Inter.css"
import { createApp } from "vue"
import App from "./App.vue"
import "./style.scss"

const pinia = createPinia()
pinia.use(() => ({ router }))

createApp(App).use(router).use(pinia).mount("#app")
