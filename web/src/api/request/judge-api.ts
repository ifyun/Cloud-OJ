import axios, { ApiPath, resolveError } from "@/api"
import type { SubmitData } from "@/api/type"
import { useStore } from "@/store"

const JudgeApi = {
  /**
   * 提交代码
   */
  submit(data: SubmitData): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const role = useStore().user.userInfo!.role
      const path = role === 1 ? ApiPath.SUBMIT : ApiPath.ADMIN_SUBMIT

      axios({
        url: path,
        method: "POST",
        data: JSON.stringify(data, (_, v) => v ?? undefined)
      })
        .then((res) => {
          resolve(res.data as number)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default JudgeApi
