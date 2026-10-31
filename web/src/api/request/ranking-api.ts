import useAxios from "@/api"
import type { Page, Ranking, RankingContest } from "@/api/type"
import { useUserStore } from "@/store/user"

const RankingApi = {
  async get(page: number, size: number) {
    const userInfo = useUserStore().info
    const url = "/api/core/ranking/"
    const path = userInfo == null || userInfo.role === 1 ? "" : "admin"
    const res = await useAxios({
      url: url + path,
      method: "GET",
      params: {
        page,
        size
      }
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<Ranking>
  },
  async getContestRanking(cid: number) {
    const userInfo = useUserStore().info
    const url = "/api/core/ranking/contest"
    const path = userInfo == null || userInfo.role === 1 ? "" : "admin"
    const res = await useAxios({
      url: `${url}/${path}/${cid}`,
      method: "GET"
    })

    return res.data as RankingContest
  }
}

export default RankingApi
