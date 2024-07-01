import axios, { ApiPath, resolveError } from "@/api"
import type { Page, Problem, ProblemData } from "@/api/type"
import { useStore } from "@/store"
import type { AxiosResponse } from "axios"

/**
 * 题目接口
 */
const ProblemApi = {
  /**
   * 获取开放的题目
   */
  getAllOpened(
    page: number,
    size: number,
    keyword: string | null = null
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM,
        method: "GET",
        params: {
          page,
          size,
          keyword
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<Problem>)
          } else {
            resolve({ data: [], total: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  getAll(
    page: number,
    size: number,
    keyword: string | null = null
  ): Promise<Page<Problem>> {
    return new Promise<Page<Problem>>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM_ADMIN,
        method: "GET",
        params: {
          page,
          size,
          keyword
        }
      })
        .then((res) => {
          if (res.status === 200) {
            resolve(res.data as Page<Problem>)
          } else {
            resolve({ data: [], total: 0 })
          }
        })
        .catch((error) => {
          reject(resolveError(error))
        })
    })
  },

  /**
   * 获取单个题目
   */
  getSingle(pid: number): Promise<Problem> {
    const userInfo = useStore().user.userInfo
    const path =
      userInfo == null || userInfo.role === 1
        ? ApiPath.PROBLEM
        : ApiPath.PROBLEM_ADMIN

    return new Promise<Problem>((resolve, reject) => {
      axios({
        url: `${path}/${pid}`,
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
   * 设在题目开放/关闭
   */
  changeState(pid: number, enable: boolean): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.PROBLEM_ADMIN}/${pid}`,
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
   */
  save(problem: Problem, create = false): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: ApiPath.PROBLEM_ADMIN,
        method: create ? "POST" : "PUT",
        data: JSON.stringify(problem)
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
   * 保存测试数据配置
   */
  saveDataConf(
    pid: number,
    data: { [key: string]: number }
  ): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.PROBLEM_ADMIN}/data_conf`,
        method: "POST",
        data: JSON.stringify({
          problemId: pid,
          conf: data
        })
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
   */
  delete(pid: number): Promise<AxiosResponse> {
    return new Promise<AxiosResponse>((resolve, reject) => {
      axios({
        url: `${ApiPath.PROBLEM_ADMIN}/${pid}`,
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
   */
  getProblemData(pid: number): Promise<ProblemData> {
    return new Promise<ProblemData>((resolve, reject) => {
      axios({
        url: `${ApiPath.TEST_DATA}/${pid}`,
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
  downloadData(pid: number, fileName: string): Promise<void> {
    const url = `${ApiPath.TEST_DATA}/download/${pid}/${fileName}`

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
   */
  deleteTestData(pid: number, fileName: string): Promise<AxiosResponse> {
    return new Promise((resolve, reject) => {
      axios({
        url: `${ApiPath.TEST_DATA}/${pid}`,
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
