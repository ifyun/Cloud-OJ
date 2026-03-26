import router from "@/router"
import { createPinia } from "pinia"
import { createApp } from "vue"
import App from "./App.vue"
import "./style.scss"

const pinia = createPinia()
pinia.use(() => ({ router }))

createApp(App).use(router).use(pinia).mount("#app")
console.log("Timezone:", Intl.DateTimeFormat().resolvedOptions().timeZone)

// 最小化弹回修复
const _replaceState = window.history.replaceState
window.history.replaceState = function (...args) {
  if (document.visibilityState === "hidden") return
  return _replaceState.apply(this, args)
}
