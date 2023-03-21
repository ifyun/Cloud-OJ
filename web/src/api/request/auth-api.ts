import { buildHeaders, resolveError } from "@/api/utils"
import type { UserInfo, UsernamePassword } from "@/api/type"
import ApiPath from "./api-path"
import type { AxiosResponse } from "axios"
import axios from "axios"

/**
 * 授权验证接口
 */
class AuthApi {
  /**
   * 登录
   *
   * @param user {@link User}
   */
  login(user: UsernamePassword): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      axios({
        url: ApiPath.LOGIN,
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        data: JSON.stringify(user)
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }

  /**
   * 登出
   *
   * @param userInfo {@link UserInfo}
   */
  logoff(userInfo: UserInfo): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.LOGOFF,
        method: "DELETE",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }

  /**
   * 验证 Token
   *
   * @param userInfo {@link UserInfo}
   * @returns
   */
  verify(userInfo: UserInfo): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.VERIFY,
        method: "GET",
        headers: buildHeaders(userInfo)
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

export default new AuthApi()
