import useAxios from "@/api"
import { JudgeResult, type Page, type SolutionFilter } from "@/api/type"

const SolutionApi = {
  async getByFilter(
    page: number,
    size: number,
    filter: SolutionFilter | null = null
  ) {
    const res = await useAxios({
      url: "/api/core/solution/admin/query",
      method: "POST",
      params: {
        page,
        size
      },
      data: filter
    })

    return (
      res.status === 200 ? res.data : { data: [], count: 0 }
    ) as Page<JudgeResult>
  },
  async getById(sid: string) {
    const res = await useAxios({
      url: `/api/core/solution/admin/${sid}`,
      method: "GET"
    })

    return res.data as JudgeResult
  }
}

export default SolutionApi
