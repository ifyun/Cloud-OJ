import axios, { ApiPath, resolveError } from "@/api"
import { type QueuesInfo, type SolutionSummary } from "@/api/type"

const AdminApi = {
  getSolutionSummary(): Promise<SolutionSummary> {
    return new Promise<SolutionSummary>((resolve, reject) => {
      axios({
        url: `${ApiPath.SOLUTION_ADMIN}/summary`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getQueuesInfo() {
    return new Promise<QueuesInfo>((resolve, reject) => {
      axios({
        url: ApiPath.QUEUE_INFO,
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

export default AdminApi
