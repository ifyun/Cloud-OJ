import useAxios from "@/api"
import {
  type JudgeResult,
  type Page,
  type SolutionFilter,
  type User,
  type UserFilter,
  type UserSummary
} from "@/api/type"
import { useUserStore } from "@/store/user"

const UserApi = {
  async getByFilter(
    page: number,
    size: number,
    filter: UserFilter | null = null
  ) {
    const res = await useAxios({
      url: "/api/core/user/admin/query",
      method: "POST",
      params: {
        page,
        size
      },
      data: JSON.stringify(filter)
    })

    return (
      res.status === 200 ? res.data : { data: [], count: 0 }
    ) as Page<User>
  },
  save(user: User, create: boolean = false) {
    return useAxios({
      url: `/api/core/user/${create ? "" : "admin"}`,
      method: create ? "POST" : "PUT",
      data: JSON.stringify(user)
    })
  },
  update(user: User, admin: boolean = false) {
    return useAxios({
      url: `/api/core/user/${admin ? "admin" : "profile"}`,
      method: "PUT",
      data: JSON.stringify(user)
    })
  },
  async getProfile(uid: number) {
    const userInfo = useUserStore().info
    const url = "/api/core/user/"
    const path =
      userInfo == null || userInfo.role === 1 ? "profile" : "admin/profile"
    const res = await useAxios({
      url: url + path,
      method: "GET",
      params: {
        uid
      }
    })

    return res.data as User
  },
  async getSummary(uid: number, year?: number) {
    const res = await useAxios({
      url: "/api/core/user/summary",
      method: "GET",
      params: {
        uid,
        year
      }
    })

    return res.data as UserSummary
  },
  async getSolutions(
    page: number,
    size: number,
    filter: SolutionFilter | null = null
  ) {
    const res = await useAxios({
      url: "/api/core/solution/query",
      method: "POST",
      params: {
        page,
        size
      },
      data: filter
    })

    return (
      res.status === 200 ? res.data : { data: [], total: 0 }
    ) as Page<JudgeResult>
  },
  async getSolution(sid: string) {
    const res = await useAxios({
      url: `/api/core/solution/${sid}`,
      method: "GET"
    })

    return res.data as JudgeResult
  },
  updateAvatar(blob: Blob) {
    const form = new FormData()
    form.append("file", blob, "avatar")

    return useAxios({
      url: "/api/core/file/img/avatar",
      method: "POST",
      headers: {
        "Content-Type": "multipart/form-data"
      },
      data: form
    })
  }
}

export default UserApi
