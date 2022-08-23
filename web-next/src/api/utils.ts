import type { AxiosError } from "axios"
import { ErrorMsg, UserInfo } from "@/api/type"

function returnError(error: any): ErrorMsg {
  const err = error as AxiosError
  if (err.response) {
    const msg =
      typeof err.response.data === "undefined"
        ? err.response.statusText
        : (err.response.data as any).msg
    return new ErrorMsg(err.response.status, msg)
  } else if (err.request) {
    return new ErrorMsg(0, "请求失败")
  } else {
    return new ErrorMsg(-1, "请求失败")
  }
}

function buildHeaders(userInfo: UserInfo): any {
  if (userInfo == null) {
    return {
      "Content-Type": "application/json"
    }
  } else {
    return {
      "Content-Type": "application/json",
      token: userInfo.token
    }
  }
}

export { returnError, buildHeaders }
