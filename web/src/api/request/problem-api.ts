import useAxios from "@/api"
import type { Page, Problem, ProblemData } from "@/api/type"
import { useUserStore } from "@/store/user"

const ProblemApi = {
  async getAllOpened(
    page: number,
    size: number,
    keyword: string | null = null
  ) {
    const res = await useAxios({
      url: "/api/core/problem",
      method: "GET",
      params: {
        page,
        size,
        keyword
      }
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<Problem>
  },
  async getAll(page: number, size: number, keyword: string | null = null) {
    const res = await useAxios({
      url: "/api/core/problem/admin",
      method: "GET",
      params: {
        page,
        size,
        keyword
      }
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<Problem>
  },
  async getSingle(pid: number) {
    const userInfo = useUserStore().info
    const url = "/api/core/problem"
    const path = userInfo == null || userInfo.role === 1 ? "" : "admin"
    const res = await useAxios({
      url: `${url}/${path}/${pid}`,
      method: "GET"
    })

    return res.data as Problem
  },
  changeState(pid: number, enable: boolean) {
    return useAxios({
      url: `/api/core/problem/admin/${pid}`,
      method: "PUT",
      params: {
        enable
      }
    })
  },
  save(problem: Problem, create = false) {
    return useAxios({
      url: "/api/core/problem/admin",
      method: create ? "POST" : "PUT",
      data: JSON.stringify(problem)
    })
  },
  saveDataConf(pid: number, data: { [key: string]: number }) {
    return useAxios({
      url: "/api/core/problem/admin/data_conf",
      method: "POST",
      data: JSON.stringify({
        problemId: pid,
        conf: data
      })
    })
  },
  delete(pid: number) {
    return useAxios({
      url: `/api/core/problem/admin/${pid}`,
      method: "DELETE"
    })
  },
  async getProblemData(pid: number) {
    const res = await useAxios({
      url: `/api/core/file/data/${pid}`,
      method: "GET"
    })

    return res.data as ProblemData
  },
  async downloadData(pid: number, fileName: string) {
    const url = `/api/core/file/data/raw/${pid}/${fileName}`
    const res = await useAxios({
      url,
      method: "GET",
      responseType: "blob"
    })
    const objectUrl = window.URL.createObjectURL(res.data)
    const anchor = document.createElement("a")
    anchor.href = objectUrl
    anchor.download = fileName
    anchor.click()
    window.URL.revokeObjectURL(objectUrl)
  },
  deleteTestData(pid: number, fileName: string) {
    return useAxios({
      url: `/api/core/file/data/${pid}`,
      method: "DELETE",
      params: {
        name: fileName
      }
    })
  },
  saveSPJ(pid: number, source: string) {
    return useAxios({
      url: "/api/core/file/spj",
      method: "POST",
      data: JSON.stringify({ pid, source })
    })
  },
  deleteSPJ(pid: number) {
    return useAxios({
      url: `/api/core/file/spj/${pid}`,
      method: "DELETE"
    })
  }
}

export default ProblemApi
