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
  userId = ""
  name = ""
  password?: string
  confirmPassword?: string
  roleId?: number
  email = ""
  section = ""
  createAt = ""
}

/**
 * 用户信息（登录成功后）
 */
export class UserInfo {
  userId?: string
  name?: string
  token?: string
  roleId = 0
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
  fileName = ""
  size = 0
}

/**
 * 题目
 */
export class Problem {
  problemId: number | undefined
  title = ""
  description = ""
  type = 0
  category = ""
  tags: Array<string> = []
  enable = false
  createAt = ""
  score: number | undefined
  timeout: number | undefined
  memoryLimit: number | undefined
  outputLimit: number | undefined
}

export class Contest {
  contestId: number | undefined
  contestName = ""
  languages = 0
  problemCount = 0
  startAt = ""
  endAt = ""
  started = false
  ended = false
  timeRange?: Array<number>
}

export class Ranking {
  rank = 0
  name = ""
  userId = ""
  committed = 0
  passed = 0
  score = 0
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
  solutionId?: string
  problemId?: number
  language?: number
  title?: string
  state?: number
  result?: number
  time?: number
  memory?: number
  passRate?: number
  errorInfo?: string
  submitTime?: string
}

export type Language = {
  count: number
  language: number
}

export type Statistics = {
  AC: number
  WA: number
  CE: number
  RE: number
  MLE: number
  TLE: number
  total: number
}

export type Activity = {
  date: string
  count: number
}

export class Overview {
  preference: Array<Language> = []
  statistics: Statistics = {
    AC: 0,
    WA: 0,
    CE: 0,
    RE: 0,
    MLE: 0,
    TLE: 0,
    total: 0
  }
  activities: Array<Activity> = []
}
