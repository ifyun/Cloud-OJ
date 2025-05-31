import { useStore } from "@/store"
import _axios, { AxiosError } from "axios"
import { ErrorMessage, UserInfo } from "./type"

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
  SETTINGS: "/api/core/settings",
  LOG: "/api/core/log"
}

function resolveError(error: any): ErrorMessage {
  const err = error as AxiosError
  if (err.response) {
    if (
      err.response.data &&
      err.response.headers["content-type"] === "application/json"
    ) {
      return ErrorMessage.from(err.response.data)
    } else {
      return new ErrorMessage(err.response.status, err.response.statusText)
    }
  } else if (err.request) {
    return new ErrorMessage(0, "请求失败")
  } else {
    return new ErrorMessage(-1, err.message)
  }
}

function buildHeaders(userInfo: UserInfo | null): any {
  if (userInfo == null) {
    return {
      "Content-Type": "application/json"
    }
  } else {
    return {
      "Content-Type": "application/json",
      Authorization: `Baerer ${userInfo.token}`
    }
  }
}

const axios = _axios.create()

// 请求拦截器
axios.interceptors.request.use(
  (config) => {
    const user = useStore().user
    config.headers = buildHeaders(user.userInfo)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default axios
export { ApiPath, resolveError }
