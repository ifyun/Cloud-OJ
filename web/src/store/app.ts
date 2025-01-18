import { ErrorMessage } from "@/api/type"
import { darkTheme, type GlobalTheme } from "naive-ui"
import { defineStore } from "pinia"

const THEME = "theme"
const theme = localStorage.getItem(THEME)

export interface State {
  menuCollapsed: boolean
  breadcrumb: Array<string> | null
  theme: GlobalTheme | null
  error: ErrorMessage | null
}

export const useAppStore = defineStore("app", {
  state: (): State => ({
    menuCollapsed: false,
    breadcrumb: null,
    theme: theme === "dark" ? darkTheme : null,
    error: null
  }),
  actions: {
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
    },
    setError(value: ErrorMessage | null) {
      this.error = value
      if (value != null) {
        this.router.replace({ name: "error" })
      }
    }
  }
})
