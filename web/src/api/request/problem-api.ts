import axios, { ApiPath, resolveError } from "@/api"
import type { Page, Problem, ProblemData, TestData } from "@/api/type"
import { useStore } from "@/store"
import type { AxiosResponse } from "axios"

/**
 * 题目接口
 */
const ProblemApi = {
  /**
   * 获取开放的题目
   *
   * @param page 页序号
   * @param limit 数量
   * @param keyword 指定关键字用于搜索
   */
  getAllOpened(
    page: number,
    limit: number,
    keyword: string | null = null
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM,
        method: "GET",
        params: {
          page,
          limit,
          keyword
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<Problem>)
          } else {
            resolve({ data: [], count: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getAll(
    page: number,
    limit: number,
    keyword: string | null = null
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM_ADMIN,
        method: "GET",
        params: {
          page,
          limit,
          keyword
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<Problem>)
          } else {
            resolve({ data: [], count: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取单个题目
   *
   * @param problemId 题目 Id
   * @param userInfo 指定此参数时请求 admin 接口
   */
  getSingle(problemId: number): Promise<Problem> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.PROBLEM
        : ApiPath.PROBLEM_ADMIN

    return new Promise<Problem>((resolve, reject) => {
      axios({
        url: `${path}/${problemId}`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data as Problem)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 切换开放/关闭
   * @param problemId 题目 Id
   * @param enable 是否开放
   */
  changeState(problemId: number, enable: boolean): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.PROBLEM_ADMIN}/${problemId}`,
        method: "PUT",
        params: {
          enable
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
   * 保存题目
   * @param p {@link Problem}
   * @param create true -> 创建新题目
   */
  save(p: Problem, create = false): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM_ADMIN,
        method: create ? "POST" : "PUT",
        data: JSON.stringify(p)
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
   * 删除题目
   * @param problemId 题目 Id
   */
  delete(problemId: number): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.PROBLEM_ADMIN}/${problemId}`,
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
   * 获取测试数据
   * @param problemId 题目 Id
   */
  getProblemData(problemId: number): Promise<ProblemData> {
    return new Promise<ProblemData>((resolve, reject) => {
      axios({
        url: `${ApiPath.TEST_DATA}/${problemId}`,
        method: "GET"
      })
        .then((res) => {
          resolve(res.data as ProblemData)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 下载测试数据
   */
  downloadData(problemId: number, fileName: string): Promise<void> {
    const url = `${ApiPath.TEST_DATA}/download/${problemId}/${fileName}`

    return new Promise((_, reject) => {
      axios({
        url,
        method: "GET",
        responseType: "blob"
      })
        .then((res) => {
          const objectUrl = window.URL.createObjectURL(res.data)
          const anchor = document.createElement("a")
          anchor.href = objectUrl
          anchor.download = fileName
          anchor.click()
          window.URL.revokeObjectURL(objectUrl)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 删除测试数据
   * @param problemId 题目 Id
   * @param fileName 文件名
   */
  deleteTestData(problemId: number, fileName: string): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.TEST_DATA}/${problemId}`,
        method: "DELETE",
        params: {
          name: fileName
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

  saveSPJ(pid: number, source: string): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: ApiPath.SPJ,
        method: "POST",
        data: JSON.stringify({ pid, source })
      })
        .then((res) => {
          resolve(res)
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  deleteSPJ(pid: number): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.SPJ}/${pid}`,
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

export default ProblemApi
