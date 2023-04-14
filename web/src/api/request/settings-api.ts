import { UserInfo, Settings } from "@/api/type"
import { buildHeaders, resolveError } from "@/api/utils"
import ApiPath from "./api-path"
import axios, { AxiosResponse } from "axios"

const SettingsApi = {
  get(userInfo: UserInfo): Promise<Settings> {
    return new Promise<Settings>((resolve, reject) => {
      axios({
        url: ApiPath.SETTINGS,
        method: "GET",
        headers: buildHeaders(userInfo)
      })
        .then((res) => resolve(res.data))
        .catch((error) => reject(resolveError(error)))
    })
  },

  save(settings: Settings, userInfo: UserInfo): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.SETTINGS,
        method: "PUT",
        headers: buildHeaders(userInfo),
        data: JSON.stringify(settings)
      })
        .then((res) => resolve(res))
        .catch((error) => reject(resolveError(error)))
    })
  }
}

export default SettingsApi
