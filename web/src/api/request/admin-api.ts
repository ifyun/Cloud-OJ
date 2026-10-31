import useAxios, { ApiPath } from "@/api"
import { type QueuesInfo, type SolutionSummary } from "@/api/type"

const AdminApi = {
  async getSolutionSummary() {
    const res = await useAxios({
      url: `${ApiPath.SOLUTION_ADMIN}/summary`,
      method: "GET"
    })

    return res.data as SolutionSummary
  },
  async getQueuesInfo() {
    const res = await useAxios({
      url: ApiPath.QUEUE_INFO,
      method: "GET"
    })

    return res.data as QueuesInfo
  }
}

export default AdminApi
