import useAxios, { ApiPath } from "@/api"
import type { SubmitData } from "@/api/type"
import { useUserStore } from "@/store/user"

const JudgeApi = {
  async submit(data: SubmitData) {
    const role = useUserStore().info!.role
    const path = role === 1 ? ApiPath.SUBMIT : ApiPath.ADMIN_SUBMIT
    const res = await useAxios({
      url: path,
      method: "POST",
      data: JSON.stringify(data, (_, v) => v ?? undefined)
    })

    return res.data as number
  }
}

export default JudgeApi
