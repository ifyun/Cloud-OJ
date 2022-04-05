import { buildHeaders, returnError } from "@/api/utils"
import { UsernamePassword, UserInfo } from "@/api/type"
import ApiPath from "./ApiPath"
import axios, { AxiosResponse } from "axios"

/**
 * 授权验证接口
 */
class AuthApi {
  /**
   * 登录
   *
   * @param user {@link User}
   */
  login(user: UsernamePassword): Promise<UserInfo> {
    return new Promise<UserInfo>((resolve, reject) => {
      axios({
        url: ApiPath.LOGIN,
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        data: JSON.stringify(user)
      })
        .then((res) => {
          resolve(res.data as UserInfo)
        })
        .catch((error) => {
          reject(returnError(error))
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
          reject(returnError(error))
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
          reject(returnError(error))
        })
    })
  }
}

export default new AuthApi()
