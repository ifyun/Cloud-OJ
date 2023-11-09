import axios, { ApiPath, resolveError } from "@/api"
import type { Contest, Page, Problem } from "@/api/type"
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
        url: `${ApiPath.CONTEST_GEN_KEY}/${cid}`,
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
  getAll(page: number, limit: number): Promise<Page<Contest>> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.CONTEST
        : ApiPath.CONTEST_ADMIN

    return new Promise<Page<Contest>>((resolve, reject) => {
      axios({
        url: path,
        method: "GET",
        params: {
          page,
          limit
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取已开始竞赛/作业中的题目
   */
  getProblemsFromStarted(contestId: number): Promise<Array<Problem>> {
    return new Promise<Array<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM}`,
        method: "GET",
        params: {
          contestId
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
  getProblems(contestId: number): Promise<Array<Problem>> {
    return new Promise<Array<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem`,
        method: "GET",
        params: {
          contestId
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
    contestId: number,
    page: number,
    limit: number
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/available_problem/${contestId}`,
        method: "GET",
        params: {
          page,
          limit
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * @param contestId 改变竞赛题目顺序
   */
  changeOrder(
    contestId: number,
    problems: Array<number>
  ): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM_ORDER}/${contestId}`,
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
  getProblem(contestId: number, problemId: number): Promise<Problem> {
    return new Promise<Problem>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM}/${contestId}/${problemId}`,
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
   * @param contestId 竞赛 Id
   */
  getById(contestId: number): Promise<Contest> {
    return new Promise<Contest>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST}/detail`,
        method: "GET",
        params: {
          contestId
        }
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
   * @param contest {@link Contest}
   * @param create true -> 创建
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
   * @param contestId 竞赛 Id
   * @param problemId 题目Id
   */
  addProblem(contestId: number, problemId: number): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
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
   * @param contestId 题目 Id
   * @param problemId 竞赛 Id
   */
  removeProblem(contestId: number, problemId: number) {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
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
   * @param contestId 竞赛 Id
   */
  delete(contestId: number): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/${contestId}`,
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
