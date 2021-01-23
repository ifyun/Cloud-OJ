import axios from "axios"
import qs from "qs"

const ApiPath = {
    LOGIN: "/api/auth/login",
    LOGOFF: "/api/auth/logoff",
    IMAGE: "/api/file/image",
    AVATAR: "/api/file/image/avatar",
    PROBLEM: "/api/manager/problem",
    PROBLEM_IMAGE: "/api/file/image/problem",
    PROBLEM_ADMIN: "/api/manager/problem/pro",
    TEST_DATA: "/api/file/test_data",
    USER: "/api/manager/user",
    USER_ADMIN: "/api/manager/user/pro",
    CONTEST: "/api/manager/contest",
    CONTEST_ADMIN: "/api/manager/contest/pro",
    CONTEST_PROBLEM: "/api/manager/contest/problem",
    CONTEST_RANKING: "/api/manager/ranking/contest",
    CONTEST_RANKING_ADMIN: "/api/manager/ranking/pro/contest",
    CONTEST_SCORE_DETAIL: "/api/manager/ranking/pro/contest/detail",
    RANKING: "/api/manager/ranking",
    HISTORY: "/api/manager/history",
    COMMIT: "/api/judgement/commit",
    QUEUE_INFO: "/api/judgement/pro/queue_info",
    PROFILE: "/api/manager/user/profile",
    OVERVIEW: "/api/manager/user/overview",
    BACKUP: "/api/manager/backup",
    SETTINGS: "/api/manager/settings"
}

function resolveError(error) {
    const res = error.response
    return {
        code: res.status,
        msg: res.data === undefined ?
            res.responseText : res.data.msg
    }
}

function buildHeaders(userInfo) {
    return userInfo != null ? {
        "userId": userInfo.userId,
        "token": userInfo.token,
        "Content-Type": "application/json"
    } : {
        "Content-Type": "application/json"
    }
}

const AuthApi = {
    login(user) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.LOGIN,
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                data: qs.stringify(user)
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    logoff(userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.LOGOFF,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const UserApi = {
    getAll(page, limit, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.USER_ADMIN,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    save(user, userInfo = null, create = false) {
        return new Promise((resolve, reject) => {
            axios({
                url: create ? ApiPath.USER : ApiPath.USER_ADMIN,
                method: create ? "POST" : "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(user)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    delete(userId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.USER_ADMIN}/${userId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getProfile(userId) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.PROFILE,
                method: "GET",
                params: {
                    "userId": userId
                }
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    updateProfile(profile, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.PROFILE,
                method: "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(profile)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getStatistics(userId, year) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.OVERVIEW,
                method: "GET",
                params: {
                    userId,
                    year
                }
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getCommitHistory(page, limit, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.HISTORY,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const ContestApi = {
    /**
     * 获取所有竞赛/作业
     */
    getAll(page, limit, userInfo = null) {
        let reqUrl, reqHeaders
        if (userInfo == null) {
            reqUrl = ApiPath.CONTEST
            reqHeaders = null
        } else {
            reqUrl = ApiPath.CONTEST_ADMIN
            reqHeaders = buildHeaders(userInfo)
        }
        return new Promise((resolve, reject) => {
            axios({
                url: reqUrl,
                method: "GET",
                headers: reqHeaders,
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 获取已开始竞赛/作业中的题目
     */
    getProblemsFromStarted(contestId, page, limit, userInfo) {
        return new Promise((resolve, reject) => {
            const userId = userInfo.userId
            axios({
                url: ApiPath.CONTEST_PROBLEM,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    contestId,
                    userId,
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 获取不在指定竞赛/作业中的题目
     */
    getProblemsNotInContest(contestId, page, limit, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_ADMIN}/problems_not_in_contest/${contestId}`,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 获取竞赛/作业中的题目
     */
    getProblems(contestId, page, limit, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_ADMIN}/problem`,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    contestId,
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolve(error))
            })
        })
    },
    /**
     * 获取竞赛/作业中单个题目
     */
    getProblem(contestId, problemId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_PROBLEM}/${contestId}/${problemId}`,
                method: "GET",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 向竞赛/作业中添加题目
     */
    addProblem(contestId, problemId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
                method: "POST",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    removeProblem(contestId, problemId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_ADMIN}/problem/${contestId}/${problemId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    get(contestId) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST}/detail`,
                method: "GET",
                params: {
                    contestId
                }
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    save(contest, userInfo, create = false) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.CONTEST_ADMIN,
                method: create ? "POST" : "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(contest)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    delete(contestId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.CONTEST_ADMIN}/${contestId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const ProblemApi = {
    /**
     * 获取开放题目
     */
    getAllOpened(page, limit, keyword = null, userId = null) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM,
                method: "GET",
                params: {
                    keyword,
                    userId,
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 获取所有题目
     */
    getAll(page, limit, keyword = null, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM_ADMIN,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    keyword,
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 获取单个题目
     */
    get(problemId, userInfo = null) {
        let reqUrl, reqHeaders
        if (userInfo == null) {
            reqUrl = ApiPath.PROBLEM
            reqHeaders = null
        } else {
            reqUrl = ApiPath.PROBLEM_ADMIN
            reqHeaders = buildHeaders(userInfo)
        }
        return new Promise((resolve, reject) => {
            axios({
                url: `${reqUrl}/${problemId}`,
                method: "GET",
                headers: reqHeaders
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 保存题目，create = true 创建新题目
     */
    save(problem, userInfo, create = false) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.PROBLEM_ADMIN,
                method: create ? "POST" : "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(problem)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    /**
     * 切换开放/关闭状态
     */
    changeState(problemId, enable, userInfo) {
        return new Promise((resolve, reject) => {
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
                reject(resolveError(error))
            })
        })
    },
    delete(problemId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.PROBLEM_ADMIN}/${problemId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const TestDataApi = {
    get(problemId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.TEST_DATA}/${problemId}`,
                method: "GET",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    delete(problemId, fileName, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.TEST_DATA}/${problemId}`,
                method: "DELETE",
                headers: buildHeaders(userInfo),
                params: {
                    "name": fileName
                }
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const JudgeApi = {
    commit(data, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.COMMIT,
                method: "POST",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(data)
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getResult(solutionId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.COMMIT,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    "solutionId": solutionId
                }
            }).then((res) => {
                resolve(res.status === 204 ? null : res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const RankingApi = {
    getRanking(page, limit) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.RANKING,
                method: "GET",
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 204 ? null : res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getContestRanking(contestId, page, limit, userInfo = null) {
        let reqUrl = ApiPath.CONTEST_RANKING
        if (userInfo != null && userInfo["roleId"] >= 2) {
            reqUrl = ApiPath.CONTEST_RANKING_ADMIN
        }
        return new Promise((resolve, reject) => {
            axios({
                url: `${reqUrl}/${contestId}`,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                resolve(res.status === 204 ? null : res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    getDetail(contestId, userId, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.CONTEST_SCORE_DETAIL,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    contestId,
                    userId
                }
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

const SettingsApi = {
    getQueueInfo(userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.QUEUE_INFO,
                method: "GET",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    get() {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.SETTINGS,
                method: "GET"
            }).then((res) => {
                resolve(res.data)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    update(settings, userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: ApiPath.SETTINGS,
                method: "PUT",
                headers: buildHeaders(userInfo),
                data: JSON.stringify(settings)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    },
    deleteLogo(userInfo) {
        return new Promise((resolve, reject) => {
            axios({
                url: `${ApiPath.IMAGE}/logo`,
                method: "DELETE",
                headers: buildHeaders(userInfo)
            }).then((res) => {
                resolve(res)
            }).catch((error) => {
                reject(resolveError(error))
            })
        })
    }
}

export {
    ApiPath,
    AuthApi,
    UserApi,
    ContestApi,
    ProblemApi,
    TestDataApi,
    JudgeApi,
    RankingApi,
    SettingsApi
}