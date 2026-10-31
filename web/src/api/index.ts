import axios, { AxiosError } from "axios"
import { ErrorMessage } from "./type"
import { useUserStore } from "@/store/user"

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
