import { buildHeaders, resolveError } from "@/api/utils"
import type { JudgeResult, SubmitData, UserInfo } from "@/api/type"
import ApiPath from "./api-path"
import axios from "axios"

const JudgeApi = {
  /**
   * 提交代码
   * @param data {@link SubmitData}
   * @param userInfo {@link UserInfo}
   */
  submit(data: SubmitData, userInfo: UserInfo): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      axios({
        url: userInfo.role == 1 ? ApiPath.SUBMIT : ApiPath.ADMIN_SUBMIT,
        method: "POST",
        headers: buildHeaders(userInfo),
        data: JSON.stringify(data, (_, v) => v ?? undefined)
      })
        .then((res) => {
          resolve(res.data as number)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },
  // TODO 此接口当前没有被使用
  /**
   * 获取判题结果
   * @param solutionId
   * @param userInfo {@link UserInfo}
   */
  getResult(
    solutionId: string,
    userInfo: UserInfo
  ): Promise<JudgeResult | null> {
    return new Promise<JudgeResult | null>((resolve, reject) => {
      axios({
        url: `${ApiPath.SOLUTION}/${solutionId}`,
        method: "GET",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res.status === 204 ? null : (res.data as JudgeResult))
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default JudgeApi
