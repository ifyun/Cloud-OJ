import useAxios from "@/api"
import type { UsernamePassword } from "@/api/type"

/**
 * 授权验证接口
 */
const AuthApi = {
  async login(user: UsernamePassword) {
    const res = await useAxios({
      url: "/api/auth/login",
      method: "POST",
      data: JSON.stringify(user)
    })
    // JWT
    return res.data as string
  },
  logoff() {
    return useAxios({
      url: "/api/auth/logoff",
      method: "DELETE"
    })
  },
  async refreshToken() {
    const res = await useAxios({
      url: "/api/auth/refresh_token",
      method: "GET"
    })

    return res.data as string
  },
  verify() {
    return useAxios({
      url: "/api/auth/verify",
      method: "GET"
    })
  }
}

export default AuthApi
