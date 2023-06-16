import type { JudgeResult, Overview, Page, User, UserInfo } from "@/api/type"
import { buildHeaders, resolveError } from "@/api/utils"
import ApiPath from "./api-path"
import axios from "axios"

const UserApi = {
  /**
   * 获取所有用户
   * @param page 页数
   * @param limit 每页数量
   * @param params 搜索参数
   * @param userInfo {@link UserInfo}
   */
  getAll(
    page: number,
    limit: number,
    params: any,
    userInfo: UserInfo
  ): Promise<Page<User>> {
    return new Promise<Page<User>>((resolve, reject) => {
      axios({
        url: ApiPath.USER_ADMIN,
        method: "GET",
        headers: buildHeaders(userInfo),
        params: {
          page,
          limit,
          userId: params.userId,
          name: params.name
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  save(user: User, userInfo: UserInfo | null, create: boolean = false) {
    return new Promise((resolve, reject) => {
      axios({
        url: create ? ApiPath.USER : ApiPath.USER_ADMIN,
        method: create ? "POST" : "PUT",
        headers: buildHeaders(userInfo),
        data: JSON.stringify(user)
      })
        .then((res) => resolve(res))
        .catch((error) => reject(resolveError(error)))
    })
  },

  getProfile(userId: string): Promise<User> {
    return new Promise<User>((resolve, reject) => {
      axios({
        url: ApiPath.PROFILE,
        method: "GET",
        params: {
          userId
        }
      })
        .then((res) => {
          resolve(res.data as User)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getOverview(userId: string, year: number) {
    return new Promise<Overview>((resolve, reject) => {
      axios({
        url: ApiPath.OVERVIEW,
        method: "GET",
        params: {
          userId,
          year
        }
      })
        .then((res) => {
          resolve(res.data as Overview)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getSolutions(
    page: number,
    limit: number,
    userInfo: UserInfo,
    filter: number | null = null,
    filterValue: string | null = null
  ) {
    return new Promise<Page<JudgeResult>>((resolve, reject) => {
      axios({
        url: ApiPath.SOLUTION,
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${userInfo.token!}`,
          userId: userInfo.userId!
        },
        params: {
          page,
          limit,
          filter,
          filterValue
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<JudgeResult>)
          } else {
            resolve({ data: [], count: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default UserApi
