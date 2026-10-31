import useAxios from "@/api"
import type { Settings } from "@/api/type"

const SettingsApi = {
  async get() {
    const res = await useAxios({
      url: "/api/core/settings",
      method: "GET"
    })

    return res.data as Settings
  },
  save(settings: Settings) {
    return useAxios({
      url: "/api/core/settings",
      method: "PUT",
      data: JSON.stringify(settings)
    })
  }
}

export default SettingsApi
