import axios, { ApiPath, resolveError } from "@/api"
import type { Page, Ranking, RankingContest } from "@/api/type"
import { useStore } from "@/store"

const RankingApi = {
  get(page: number, size: number): Promise<Page<Ranking>> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.RANKING
        : ApiPath.RANKING_ADMIN

    return new Promise<Page<Ranking>>((resolve, reject) => {
      axios({
        url: path,
        method: "GET",
        params: {
          page,
          size
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<Ranking>)
          } else {
            resolve({ data: [], total: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getContestRanking(cid: number): Promise<RankingContest> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.CONTEST_RANKING
        : ApiPath.CONTEST_RANKING_ADMIN

    return new Promise<RankingContest>((resolve, reject) => {
      axios({
        url: `${path}/${cid}`,
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
