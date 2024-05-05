import axios, { ApiPath, resolveError } from "@/api"
import type { UsernamePassword } from "@/api/type"
import type { AxiosResponse } from "axios"

/**
 * 授权验证接口
 */
const AuthApi = {
  /**
   * 登录
   */
  login(user: UsernamePassword): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      axios({
        url: ApiPath.LOGIN,
        method: "POST",
        data: JSON.stringify(user)
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 登出
   */
  logoff(): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.LOGOFF,
        method: "DELETE"
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 刷新 Token
   */
  refresh_token(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      axios({
        url: ApiPath.REFRESH_TOKEN,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 验证 Token
   */
  verify(): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.VERIFY,
        method: "GET"
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default AuthApi
