import { defineStore } from "pinia"
import { GlobalTheme, darkTheme } from "naive-ui"
import { nextTick } from "vue"

const THEME = "theme"
const theme = localStorage.getItem(THEME)

interface State {
  reload: boolean
  menuCollapsed: boolean
  breadcrumb: Array<string> | null
  theme: GlobalTheme | null
}

export const useAppStore = defineStore("app", {
  state: (): State => ({
    reload: false,
    menuCollapsed: false,
    breadcrumb: null,
    theme: theme === "dark" ? darkTheme : null
  }),
  actions: {
    refresh() {
      this.reload = true
      nextTick(() => (this.reload = false))
    },
    setBreadcrumb(value: Array<string> | null) {
      this.breadcrumb = value
    },
    menuCollapse() {
      this.menuCollapsed = !this.menuCollapsed
    },
    setTheme(value: string | null) {
      if (value === "dark") {
        this.theme = darkTheme
        localStorage.setItem(THEME, "dark")
      } else {
        this.theme = null
        localStorage.removeItem(THEME)
      }
    }
  }
})
