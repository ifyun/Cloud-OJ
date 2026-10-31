import { defineStore } from "pinia"

export const useFilterStore = defineStore("filterStore", {
  state: () => ({
    filter: {} as { [key: string]: any }
  }),
  actions: {
    clear() {
      this.filter = {}
    }
  }
})
