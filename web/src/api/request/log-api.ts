import axios, { ApiPath, resolveError } from "@/api"
import type { Log } from "@/api/type"

const LogApi = {
  getLatest10Logs(time: number | null = null): Promise<Array<Log>> {
    return new Promise<Array<Log>>((resolve, reject) => {
      axios({
        url: ApiPath.LOG,
        method: "GET",
        params: {
          time
        }
      })
        .then((res) => {
          if (res.status === 204) {
            resolve([])
          }
          resolve(res.data)
        })
        .catch((error) => reject(resolveError(error)))
    })
  }
}

export default LogApi
