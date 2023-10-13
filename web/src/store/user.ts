import { UserInfo } from "@/api/type"
import { defineStore } from "pinia"

const TOKEN = "userToken"
const token = localStorage.getItem(TOKEN)

/**
 * 解析 JWT 中的用户信息
 * @param token Base64Url
 * @returns
 */
function resolveToken(token: string): UserInfo {
  const claims = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")
  const userInfo = JSON.parse(decodeURIComponent(escape(window.atob(claims))))
  userInfo.token = token

  return userInfo
}

export const useUserStore = defineStore("userInfo", {
  state: () => ({
    userInfo: token == null ? null : resolveToken(token)
  }),
  getters: {
    isLoggedIn(): boolean {
      return this.userInfo != null
    }
  },
  actions: {
    saveToken(token: string) {
      localStorage.setItem(TOKEN, token)
      this.userInfo = resolveToken(token)
    },
    clearToken() {
      localStorage.removeItem(TOKEN)
      this.userInfo = null
    }
  }
})
