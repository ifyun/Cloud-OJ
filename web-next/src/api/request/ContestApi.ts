import { Contest, PagedData, Problem, UserInfo } from "@/api/type"
import { buildHeaders, returnError } from "@/api/utils"
import ApiPath from "./ApiPath"
import axios, { AxiosResponse } from "axios"

const ContestApi = {
  /**
   * 获取所有竞赛
   */
  getAll(
    page: number,
    limit: number,
    userInfo: UserInfo | null = null
  ): Promise<PagedData<Contest>> {
    let path: string
    let headers: any

    if (userInfo == null) {
      path = ApiPath.CONTEST
      headers = null
    } else {
      path = ApiPath.CONTEST_ADMIN
      headers = buildHeaders(userInfo)
    }

    return new Promise<PagedData<Contest>>((resolve, reject) => {
      axios({
        url: path,
        method: "GET",
        headers,
        params: {
          page,
          limit
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 获取竞赛中的所有题目
   */
  getProblems(
    contestId: number,
    page: number,
    limit: number,
    userInfo: UserInfo
  ): Promise<PagedData<Problem>> {
    return new Promise<PagedData<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem`,
        method: "GET",
        headers: buildHeaders(userInfo),
        params: {
          contestId,
          page,
          limit
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 获取不在指定竞赛中的所有题目
   */
  getProblemsNotInContest(
    contestId: number,
    page: number,
    limit: number,
    userInfo: UserInfo
  ): Promise<PagedData<Problem>> {
    return new Promise<PagedData<Problem>>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problems_not_in_contest/${contestId}`,
        method: "GET",
        headers: buildHeaders(userInfo),
        params: {
          page,
          limit
        }
      })
        .then((res) => {
          resolve(res.status === 200 ? res.data : { data: [], count: 0 })
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 获取竞赛中的指定题目
   */
  getProblem(
    contestId: number,
    problemId: number,
    userInfo: UserInfo
  ): Promise<Problem> {
    return new Promise<Problem>((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_PROBLEM}/${contestId}/${problemId}`,
        method: "GET",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res.data)
        })
        .catch((error) => {
          reject(returnError(error))
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
          reject(returnError(error))
        })
    })
  },

  /**
   * 保存/创建竞赛
   * @param contest {@link Contest}
   * @param userInfo {@link UserInfo}
   * @param create true -> 创建
   */
  save(
    contest: Contest,
    userInfo: UserInfo,
    create = false
  ): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: ApiPath.CONTEST_ADMIN,
        method: create ? "POST" : "PUT",
        headers: buildHeaders(userInfo),
        data: JSON.stringify(contest)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 向竞赛添加题目
   * @param contestId 竞赛 Id
   * @param problemId 题目Id
   * @param userInfo {@link UserInfo}
   */
  addProblem(
    contestId: number,
    problemId: number,
    userInfo: UserInfo
  ): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
        method: "POST",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 从竞赛中删除题目
   * @param contestId 题目 Id
   * @param problemId 竞赛 Id
   * @param userInfo {@link UserInfo}
   */
  removeProblem(contestId: number, problemId: number, userInfo: UserInfo) {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
        method: "DELETE",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  },

  /**
   * 删除竞赛
   * @param contestId 竞赛 Id
   * @param userInfo {@link UserInfo}
   */
  delete(contestId: number, userInfo: UserInfo): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.CONTEST_ADMIN}/${contestId}`,
        method: "DELETE",
        headers: buildHeaders(userInfo)
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(returnError(error))
        })
    })
  }
}

export default ContestApi
