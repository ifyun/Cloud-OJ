import axios, { ApiPath, resolveError } from "@/api"
import { Settings } from "@/api/type"
import type { AxiosResponse } from "axios"

const SettingsApi = {
  get(): Promise<Settings> {
    return new Promise<Settings>((resolve, reject) => {
      axios({
        url: ApiPath.SETTINGS,
        method: "GET"
      })
        .then((res) => resolve(res.data))
        .catch((error) => reject(resolveError(error)))
    })
  },

  save(settings: Settings): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.SETTINGS,
        method: "PUT",
        data: JSON.stringify(settings)
      })
        .then((res) => resolve(res))
        .catch((error) => reject(resolveError(error)))
    })
  }
}

export default SettingsApi
