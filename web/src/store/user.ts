import { UserInfo } from "@/api/type"
import { defineStore } from "pinia"

const TOKEN = "userToken"
const token = localStorage.getItem(TOKEN)

/**
 * 解析 JWT 中的用户信息
 *
 * @param token Base64Url
 * @returns
 */
function resolveToken(token: string): UserInfo {
  const claims = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")
  const binary = atob(claims)
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0))
  const info = JSON.parse(new TextDecoder("utf-8").decode(bytes))

  info.token = token

  return info
}

export const useUserStore = defineStore("userStore", {
  state: () => ({
    info: token == null ? null : resolveToken(token)
  }),
  getters: {
    isLoggedIn(): boolean {
      return this.info != null
    }
  },
  actions: {
    saveToken(token: string) {
      localStorage.setItem(TOKEN, token)
      this.info = resolveToken(token)
    },
    clearToken() {
      localStorage.removeItem(TOKEN)
      this.info = null
    }
  }
})
