import useAxios, { ApiPath } from "@/api"
import type { UsernamePassword } from "@/api/type"

/**
 * 授权验证接口
 */
const AuthApi = {
  async login(user: UsernamePassword) {
    const res = await useAxios({
      url: ApiPath.LOGIN,
      method: "POST",
      data: JSON.stringify(user)
    })
    // JWT
    return res.data as string
  },
  logoff() {
    return useAxios({
      url: ApiPath.LOGOFF,
      method: "DELETE"
    })
  },
  async refreshToken() {
    const res = await useAxios({
      url: ApiPath.REFRESH_TOKEN,
      method: "GET"
    })

    return res.data as string
  },
  verify() {
    return useAxios({
      url: ApiPath.VERIFY,
      method: "GET"
    })
  }
}

export default AuthApi
