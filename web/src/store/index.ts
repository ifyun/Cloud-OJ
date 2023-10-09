import { defineStore } from "pinia"
import { useAppStore } from "./app"
import { useUserStore } from "./user"

export const useStore = defineStore("store", {
  state: () => ({
    app: useAppStore(),
    user: useUserStore()
  })
})
