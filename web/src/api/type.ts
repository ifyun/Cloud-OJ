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
  hasAvatar: boolean = false
  createAt?: number
}

/**
 * 用户信息（登录成功后）
 */
export class UserInfo {
  userId?: string
  name?: string
  email: string = ""
  section: string = ""
  token?: string
  hasAvatar: boolean = false
  roleId = 1
}

export class ErrorMessage {
  timestamp?: number
  status: number
  error?: string
  message: string

  constructor(status: number, msg: string) {
    this.status = status
    this.message = msg
    this.error = msg
  }

  static from(data: any): ErrorMessage {
    const obj = new ErrorMessage(data.status, data.message)

    if (!data.message) {
      obj.message = data.error
    }

    obj.error = data.error
    obj.timestamp = data.timestamp

    return obj
  }

  toString() {
    return `${this.status}: ${this.message}`
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
  problemId?: number
  title = ""
  description = ""
  languages: number | null = null
  category = ""
  tags: Array<string> = []
  enable = false
  createAt?: number
  score?: number
  timeout?: number
  memoryLimit?: number
  outputLimit?: number
}

export class Contest {
  contestId?: number
  contestName = ""
  languages = 0
  problemCount = 0
  startAt?: number
  endAt?: number
  started = false
  ended = false
  timeRange?: [number, number]
}

export class Ranking {
  rank: number = 0
  name: string = ""
  userId: string = ""
  hasAvatar: boolean = false
  committed: number = 0
  passed: number = 0
  score: number = 0
}

/**
 * 分页数据
 */
export type Page<T> = {
  data: T[]
  count: number
}

export type SubmitData = {
  userId: string
  problemId: number
  contestId: number | null
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

  passed?: number

  total?: number
  errorInfo?: string
  submitTime?: number
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

export class Settings {
  icp?: string
  icpUrl?: string
  alwaysShowRanking?: boolean
  showAllContest?: boolean
  showPassedPoints?: boolean
}
