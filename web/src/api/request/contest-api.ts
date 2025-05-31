import axios, { ApiPath, resolveError } from "@/api"
import {
  type Contest,
  ContestFilter,
  type Page,
  type Problem
} from "@/api/type"
import { useStore } from "@/store"
import type { AxiosResponse } from "axios"

const ContestApi = {
  /**
   * 使用邀请码加入竞赛
   */
  joinContest(cid: number, key: string): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_INVITATION}/${cid}`,
        method: "POST",
        params: {
          key
        }
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 生成新邀请码
   */
  newInviteKey(cid: number): Promise<string> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_KEY}/${cid}`,
        method: "PUT"
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取所有竞赛
   */
  getAll(
    page: number,
    size: number,
    filter: ContestFilter | null = null
  ): Promise<Page<Contest>> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.CONTEST
        : ApiPath.CONTEST_ADMIN

    return new Promise<Page<Contest>>((resolve, reject) => {
      axios({
        url: `${path}/queries`,
        method: "POST",
        params: {
          page,
          size
        },
        data: JSON.stringify(filter)
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], total: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取已开始竞赛/作业中的题目
   */
  getProblemsFromStarted(cid: number): Promise<Array<Problem>> {
    return new Promise<Array<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM}`,
        method: "GET",
        params: {
          cid
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : [])
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取竞赛中的所有题目
   */
  getProblems(cid: number): Promise<Array<Problem>> {
    return new Promise<Array<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem`,
        method: "GET",
        params: {
          cid
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : [])
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取不在指定竞赛中的所有题目
   */
  getProblemsNotInContest(
    cid: number,
    page: number,
    size: number
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem_idle/${cid}`,
        method: "GET",
        params: {
          page,
          size
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], total: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 改变竞赛题目顺序
   */
  changeOrder(cid: number, problems: Array<number>): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM_ORDER}/${cid}`,
        method: "PUT",
        data: JSON.stringify(problems)
      })
        .then((res) => resolve(res))
        .catch((error) => reject(resolveError(error)))
    })
  },

  /**
   * 获取竞赛中的指定题目
   */
  getProblem(cid: number, pid: number): Promise<Problem> {
    return new Promise<Problem>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM}/${cid}/${pid}`,
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

  /**
   * 获取竞赛详细信息
   */
  getById(cid: number): Promise<Contest> {
    return new Promise<Contest>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST}/${cid}`,
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

  /**
   * 保存/创建竞赛
   */
  save(contest: Contest, create = false): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.CONTEST_ADMIN,
        method: create ? "POST" : "PUT",
        data: JSON.stringify(contest)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 向竞赛添加题目
   */
  addProblem(cid: number, pid: number): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${cid}/${pid}`,
        method: "POST"
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 从竞赛中删除题目
   */
  removeProblem(cid: number, pid: number) {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${cid}/${pid}`,
        method: "DELETE"
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 删除竞赛
   */
  delete(cid: number): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/${cid}`,
        method: "DELETE"
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  }
}

export default ContestApi
