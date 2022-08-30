import type { Page, Ranking } from "@/api/type"
import { resolveError } from "@/api/utils"
import ApiPath from "./api-path"
import axios from "axios"

const RankingApi = {
  get(page: number, limit: number): Promise<Page<Ranking>> {
    return new Promise<Page<Ranking>>((resolve, reject) => {
      axios({
        url: ApiPath.RANKING,
        method: "GET",
        params: {
          page,
          limit
        }
      })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data as Page<Ranking>)
          } else {
            resolve({ data: [], count: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default RankingApi