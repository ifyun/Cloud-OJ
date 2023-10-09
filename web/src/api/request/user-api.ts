import axios, { ApiPath, resolveError } from "@/api"
import type { JudgeResult, Overview, Page, User } from "@/api/type"
import { AxiosResponse } from "axios"

const UserApi = {
  /**
   * 获取所有用户
   * @param page 页数
   * @param limit 每页数量
   * @param params 搜索参数
   */
  getByFilter(
    page: number,
    limit: number,
    filter: number | null = null,
    filterValue: string | null = null
  ): Promise<Page<User>> {
    return new Promise<Page<User>>((resolve, reject) => {
      axios({
        url: ApiPath.USER_ADMIN,
        method: "GET",
        params: {
          page,
          limit,
          filter,
          filterValue
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

  save(user: User, create: boolean = false): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: create ? ApiPath.USER : ApiPath.USER_ADMIN,
        method: create ? "POST" : "PUT",
        data: JSON.stringify(user)
      })
        .then((res) => resolve(res))
        .catch((error) => reject(resolveError(error)))
    })
  },

  getProfile(uid: number): Promise<User> {
    return new Promise<User>((resolve, reject) => {
      axios({
        url: ApiPath.PROFILE,
        method: "GET",
        params: {
          uid
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

  getOverview(uid: number, year: number) {
    return new Promise<Overview>((resolve, reject) => {
      axios({
        url: ApiPath.OVERVIEW,
        method: "GET",
        params: {
          uid,
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
    filter: number | null = null,
    filterValue: string | null = null
  ) {
    return new Promise<Page<JudgeResult>>((resolve, reject) => {
      axios({
        url: ApiPath.SOLUTION,
        method: "GET",
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
