import axios, { AxiosError } from "axios"
import { ErrorMessage } from "./type"
import { useUserStore } from "@/store/user"

const ApiPath = {
  LOGIN: "/api/auth/login",
  LOGOFF: "/api/auth/logoff",
  REFRESH_TOKEN: "/api/auth/refresh_token",
  VERIFY: "/api/auth/verify",
  IMAGE: "/api/core/file/img",
  AVATAR: "/api/core/file/img/avatar",
  TEST_DATA: "/api/core/file/data",
  SPJ: "/api/core/file/spj",
  PROBLEM_IMAGE: "/api/core/file/img/problem",
  PROBLEM_ADMIN: "/api/core/problem/admin",
  PROBLEM: "/api/core/problem",
  USER: "/api/core/user",
  USER_ADMIN: "/api/core/user/admin",
  CONTEST: "/api/core/contest",
  CONTEST_ADMIN: "/api/core/contest/admin",
  CONTEST_INVITATION: "/api/core/contest/invitation",
  CONTEST_PROBLEM: "/api/core/contest/problem",
  CONTEST_KEY: "/api/core/contest/admin/key",
  CONTEST_PROBLEM_ORDER: "/api/core/contest/admin/problem/order",
  CONTEST_RANKING: "/api/core/ranking/contest",
  CONTEST_RANKING_ADMIN: "/api/core/ranking/admin/contest",
  RANKING: "/api/core/ranking",
  RANKING_ADMIN: "/api/core/ranking/admin",
  SOLUTION: "/api/core/solution",
  SOLUTION_ADMIN: "/api/core/solution/admin",
  SUBMIT: "/api/judge/submit",
  ADMIN_SUBMIT: "/api/judge/admin/submit",
  QUEUE_INFO: "/api/judge/admin/queue_info",
  PROFILE: "/api/core/user/profile",
  PROFILE_ADMIN: "/api/core/user/admin/profile",
  OVERVIEW: "/api/core/user/overview",
  SETTINGS: "/api/core/settings"
}

function resolveError(error: any): ErrorMessage {
  const err = error as AxiosError
  if (err.response) {
    if (
      err.response.data &&
      (err.response.headers["content-type"] as string).includes(
        "application/json"
      )
    ) {
      return ErrorMessage.from(err.response.data)
    } else {
      return new ErrorMessage(err.response.status, "发生了错误")
    }
  } else if (err.request) {
    return new ErrorMessage(0, "请求失败")
  } else {
    return new ErrorMessage(-1, "发生了错误")
  }
}

const useAxios = axios.create()

// 请求拦截器
useAxios.interceptors.request.use(
  (config) => {
    const user = useUserStore()
    if (user.info) {
      config.headers.Authorization = `Bearer ${user.info.token}`
    }

    if (!config.headers["Content-Type"]) {
      config.headers["Content-Type"] = "application/json"
    }

    return config
  },
  (error) => {
    return Promise.reject(resolveError(error))
  }
)

useAxios.interceptors.response.use(
  (res) => res,
  (error) => Promise.reject(resolveError(error))
)

export default useAxios
export { ApiPath }
