import useAxios from "@/api"
import { type Contest, type Page, type Problem } from "@/api/type"
import { useUserStore } from "@/store/user"

const ContestApi = {
  joinContest(cid: number, key: string) {
    return useAxios({
      url: `/api/core/contest/join/${cid}`,
      method: "POST",
      params: {
        key
      }
    })
  },
  async newInviteKey(cid: number): Promise<string> {
    const res = await useAxios({
      url: `/api/core/contest/admin/key/${cid}`,
      method: "PUT"
    })

    return res.data as string
  },
  async getAll(
    page: number,
    size: number,
    filter: { [key: string]: any } | null = null
  ) {
    const userInfo = useUserStore().info
    const url = "/api/core/contest"
    const path = userInfo === null || userInfo.role === 1 ? "" : "admin"
    const res = await useAxios({
      url: `${url}/${path}/query`,
      method: "POST",
      params: {
        page,
        size
      },
      data: filter
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<Contest>
  },
  async getProblemsFromStarted(cid: number) {
    const res = await useAxios({
      url: "/api/core/contest/problems",
      method: "GET",
      params: {
        cid
      }
    })

    return (res.status === 200 ? res.data : []) as Problem[]
  },
  async getProblems(cid: number) {
    const res = await useAxios({
      url: "/api/core/contest/admin/problems",
      method: "GET",
      params: {
        cid
      }
    })

    return (res.status === 200 ? res.data : []) as Problem[]
  },
  async getProblemsNotInContest(cid: number, page: number, size: number) {
    const res = await useAxios({
      url: "/api/core/contest/admin/problem/unused",
      method: "GET",
      params: {
        cid,
        page,
        size
      }
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<Problem>
  },
  changeOrder(cid: number, problems: Array<number>) {
    return useAxios({
      url: "/api/core/contest/admin/problem/order",
      method: "PUT",
      params: {
        cid
      },
      data: problems
    })
  },
  async getProblem(cid: number, pid: number) {
    const res = await useAxios({
      url: "/api/core/contest/problem",
      method: "GET",
      params: {
        cid,
        pid
      }
    })

    return res.data as Problem
  },
  async getById(cid: number): Promise<Contest> {
    const res = await useAxios({
      url: `/api/core/contest/${cid}`,
      method: "GET"
    })

    return res.data as Contest
  },
  save(contest: Contest, create = false) {
    return useAxios({
      url: "/api/core/contest/admin",
      method: create ? "POST" : "PUT",
      data: contest
    })
  },
  addProblem(cid: number, pid: number) {
    return useAxios({
      url: "/api/core/contest/admin/problem",
      method: "POST",
      params: {
        cid,
        pid
      }
    })
  },
  removeProblem(cid: number, pid: number) {
    return useAxios({
      url: "/api/core/contest/admin/problem",
      method: "DELETE",
      params: {
        cid,
        pid
      }
    })
  },
  delete(cid: number) {
    return useAxios({
      url: `/api/core/contest/admin/${cid}`,
      method: "DELETE"
    })
  }
}

export default ContestApi
