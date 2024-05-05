import axios, { ApiPath, resolveError } from "@/api"
import type { JudgeResult, Overview, Page, User } from "@/api/type"
import { AxiosResponse } from "axios"

const UserApi = {
  /**
   * 获取所有用户
   */
  getByFilter(
    page: number,
    size: number,
    filter: number | null = null,
    filterValue: string | null = null
  ): Promise<Page<User>> {
    return new Promise<Page<User>>((resolve, reject) => {
      axios({
        url: ApiPath.USER_ADMIN,
        method: "GET",
        params: {
          page,
          size,
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

  update(user: User, admin: boolean = false): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: admin ? ApiPath.USER_ADMIN : ApiPath.PROFILE,
        method: "PUT",
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
    size: number,
    filter: number | null = null,
    filterValue: string | null = null
  ) {
    return new Promise<Page<JudgeResult>>((resolve, reject) => {
      axios({
        url: ApiPath.SOLUTION,
        method: "GET",
        params: {
          page,
          size,
          filter,
          filterValue
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<JudgeResult>)
          } else {
            resolve({ data: [], total: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getSolution(sid: number): Promise<JudgeResult> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.SOLUTION}/${sid}`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default UserApi
