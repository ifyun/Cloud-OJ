import useAxios, { ApiPath } from "@/api"
import type { Settings } from "@/api/type"

const SettingsApi = {
  async get() {
    const res = await useAxios({
      url: ApiPath.SETTINGS,
      method: "GET"
    })

    return res.data as Settings
  },
  save(settings: Settings) {
    return useAxios({
      url: ApiPath.SETTINGS,
      method: "PUT",
      data: JSON.stringify(settings)
    })
  }
}

export default SettingsApi
