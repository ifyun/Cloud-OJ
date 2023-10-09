import { UserInfo } from "@/api/type"
import { defineStore } from "pinia"

const TOKEN = "userToken"
const token = localStorage.getItem(TOKEN)

function resolveToken(token: string): UserInfo {
  const json: any = JSON.parse(
    decodeURIComponent(escape(window.atob(token.split(".")[1])))
  )
  const userInfo: UserInfo = new UserInfo()
  userInfo.token = token
  userInfo.role = json.role
  userInfo.uid = Number(json.sub)
  userInfo.username = json.username
  userInfo.nickname = json.nickname
  userInfo.email = json.email ?? ""
  userInfo.section = json.section ?? ""
  userInfo.hasAvatar = json.hasAvatar
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
