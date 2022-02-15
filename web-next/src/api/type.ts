/**
 * 登录参数
 */
export class UsernamePassword {
    username: string
    password: string

    constructor(userId: string, password: string) {
        this.username = userId
        this.password = password
    }
}

export class User {
    userId: string = ""
    name: string = ""
    password: string = ""
    confirmPassword: string = ""
    roleId?: number
    email: string = ""
    section: string = ""
    createAt: string = ""
}

/**
 * 用户信息（登录成功后）
 */
export class UserInfo {
    userId?: string
    name?: string
    token?: string
    roleId: number = 0
    roleName?: string
}

/**
 * axios 错误信息封装
 */
export class ErrorMsg {
    code: number
    msg: string

    constructor(code: number, msg: string) {
        this.code = code
        this.msg = msg
    }

    toString() {
        return `${this.code}: ${this.msg}`
    }
}

export class TestData {
    fileName: string = ""
    size: number = 0
}

/**
 * 题目
 */
export class Problem {
    problemId: number | undefined
    title: string = ""
    description: string = ""
    type: number = 0
    category: string = ""
    tags: Array<string> = []
    enable: boolean = false
    createAt: string = ""
    score: number | undefined
    timeout: number | undefined
    memoryLimit: number | undefined
    outputLimit: number | undefined
}

export class Contest {
    contestId: number | undefined
    contestName: string = ""
    languages: number = 0
    problemCount: number = 0
    startAt: string = ""
    endAt: string = ""
    started: boolean = false
    ended: boolean = false
    timeRange?: Array<number>
}

export class Ranking {
    rank: number = 0
    name: string = ""
    userId: string = ""
    committed: number = 0
    passed: number = 0
    score: number = 0
}

/**
 * 分页数据
 */
export type PagedData<T> = {
    data: T[]
    count: number
}

export type SubmitData = {
    userId: string
    problemId: number
    language: number
    sourceCode: string
    type: number
}

export class JudgeResult {
    state?: number
    result?: number
    time?: number
    memory?: number
    passRate?: number
    errorInfo?: string
}
