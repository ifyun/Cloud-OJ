import {buildHeaders, returnError} from "@/api/utils"
import {PagedData, Problem, TestData, UserInfo} from "@/api/type"
import ApiPath from "@/api/request/ApiPath"
import axios, {AxiosResponse} from "axios"

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
     * @param userId 指定此参数可返回题目状态
     */
    getAllOpened(page: number, limit: number, keyword: string | null = null,
                 userId: string | null = null): Promise<PagedData<Problem>> {
        return new Promise<PagedData<Problem>>((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM,
                method: "GET",
                params: {
                    page,
                    limit,
                    keyword,
                    userId
                }
            }).then((res) => {
                if (res.status === 200) {
                    resolve(res.data as PagedData<Problem>)
                } else {
                    resolve({data: [], count: 0})
                }
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    getAll(page: number, limit: number, keyword: string | null = null,
           userInfo: UserInfo): Promise<PagedData<Problem>> {
        return new Promise<PagedData<Problem>>((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM_ADMIN,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit,
                    keyword
                }
            }).then((res) => {
                if (res.status === 200) {
                    resolve(res.data as PagedData<Problem>)
                } else {
                    resolve({data: [], count: 0})
                }
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 获取单个题目
     *
     * @param problemId 题目 Id
     * @param userInfo 指定此参数时请求 admin 接口
     */
    getSingle(problemId: number, userInfo: UserInfo | null = null): Promise<Problem> {
        let path: string, headers: any

        if (userInfo == null) {
            path = ApiPath.PROBLEM
            headers = null
        } else {
            path = ApiPath.PROBLEM_ADMIN
            headers = buildHeaders(userInfo)
        }

        return new Promise<Problem>((resolve, reject) => {
            axios({
                url: `${path}/${problemId}`,
                method: "GET",
                headers
            }).then((res) => {
                resolve(res.data as Problem)
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 切换开放/关闭
     * @param problemId 题目 Id
     * @param enable 是否开放
     * @param userInfo {@link UserInfo}
     */
    changeState(problemId: number, enable: boolean, userInfo: UserInfo): Promise<AxiosResponse> {
        return new Promise<AxiosResponse>((resolve, reject) => {
            axios({
                url: `${ApiPath.PROBLEM_ADMIN}/${problemId}`,
                method: "PUT",
                headers: buildHeaders(userInfo),
                params: {
                    enable
                }
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 保存题目
     * @param p {@link Problem}
     * @param userInfo {@link UserInfo}
     * @param create true -> 创建新题目
     */
    save(p: Problem, userInfo: UserInfo, create: boolean = false): Promise<AxiosResponse> {
        return new Promise<AxiosResponse>((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM_ADMIN,
                method: create ? "POST" : "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(p)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 删除题目
     * @param problemId 题目 Id
     * @param userInfo {@link UserInfo}
     */
    delete(problemId: number, userInfo: UserInfo): Promise<AxiosResponse> {
        return new Promise<AxiosResponse>((resolve, reject) => {
            axios({
                url: `${ApiPath.PROBLEM_ADMIN}/${problemId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 获取测试数据
     * @param problemId 题目 Id
     * @param userInfo {@link UserInfo}
     */
    getTestData(problemId: number, userInfo: UserInfo): Promise<Array<TestData>> {
        return new Promise<Array<TestData>>((resolve, reject) => {
            axios({
                url: `${ApiPath.TEST_DATA}/${problemId}`,
                method: "GET",
                headers: buildHeaders(userInfo)
            }).then(res => {
                if (res.status === 200) {
                    resolve(res.data as TestData[])
                } else {
                    resolve([])
                }
            }).catch(error => {
                reject(returnError(error))
            })
        })
    },

    /**
     * 删除测试数据
     * @param problemId 题目 Id
     * @param fileName 文件名
     * @param userInfo {@link UserInfo}
     */
    deleteTestData(problemId: number, fileName: string, userInfo: UserInfo): Promise<AxiosResponse> {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.TEST_DATA}/${problemId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo),
                params: {
                    name: fileName
                }
            }).then(res => {
                resolve(res)
            }).catch(error => {
                reject(returnError(error))
            })
        })
    }
}

export default ProblemApi
