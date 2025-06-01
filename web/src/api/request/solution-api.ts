import { JudgeResult, type Page, SolutionFilter } from "@/api/type"
import axios, { ApiPath, resolveError } from "@/api"

const SolutionApi = {
  getByFilter(
    page: number,
    size: number,
    filter: SolutionFilter | null = null
  ): Promise<Page<JudgeResult>> {
    return new Promise<Page<JudgeResult>>((resolve, reject) => {
      axios({
        url: `${ApiPath.SOLUTION_ADMIN}/queries`,
        method: "POST",
        params: {
          page,
          size
        },
        data: JSON.stringify(filter)
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getById(sid: string): Promise<JudgeResult> {
    return new Promise<JudgeResult>((resolve, reject) => {
      axios({
        url: `${ApiPath.SOLUTION_ADMIN}/${sid}`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default SolutionApi
