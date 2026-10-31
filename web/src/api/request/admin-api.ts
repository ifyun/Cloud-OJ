import useAxios from "@/api"
import { type QueuesInfo, type SolutionSummary } from "@/api/type"

const AdminApi = {
  async getSolutionSummary() {
    const res = await useAxios({
      url: "/api/core/solution/admin/summary",
      method: "GET"
    })

    return res.data as SolutionSummary
  },
  async getQueuesInfo() {
    const res = await useAxios({
      url: "/api/judge/admin/queue_info",
      method: "GET"
    })

    return res.data as QueuesInfo
  }
}

export default AdminApi
