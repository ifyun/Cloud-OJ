import axios, { ApiPath, resolveError } from "@/api"
import type { Page, Ranking, RankingContest } from "@/api/type"

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
          if (res.status === 200) {
            resolve(res.data as Page<Ranking>)
          } else {
            resolve({ data: [], count: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getContestRanking(cid: number): Promise<RankingContest> {
    return new Promise<RankingContest>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_RANKING}/${cid}`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data as RankingContest)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default RankingApi
