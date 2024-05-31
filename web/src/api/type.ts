/**
 * 登录参数
 */
export class UsernamePassword {
  username: string
  password: string

  constructor(username: string, password: string) {
    this.username = username
    this.password = password
  }
}

export class User {
  uid?: number
  username?: string
  nickname?: string
  password?: string
  confirmPassword: string = ""
  role?: number
  email?: string
  section?: string
  hasAvatar: boolean = false
  star: boolean = false
  createAt?: number
}

/**
 * 用户信息（登录成功后）
 */
export class UserInfo {
  uid?: number
  username?: string
  nickname?: string
  email?: string
  section?: string
  token?: string
  hasAvatar: boolean = false
  role = 1
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

export class ProblemData {
  pid?: number
  title: string = ""
  spj: boolean = false
  SPJSource: string = ""
  testData: Array<TestData> = []
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
  result?: number
  resultText?: string
  timeout?: number
  memoryLimit?: number
  outputLimit?: number
  order: number = 0
}

export class Contest {
  contestId?: number
  contestName = ""
  inviteKey?: string
  languages = 0
  problemCount = 0
  startAt?: number
  endAt?: number
  started = false
  ended = false
  /**
   * 给时间选择器使用
   */
  timeRange?: [number, number]
}

export class ScoreDetail {
  problemId?: number
  score?: number
  result?: number
}

export class Ranking {
  rank: number = 0
  uid?: number
  username: string = ""
  nickname: string = ""
  badge: string = ""
  hasAvatar: boolean = false
  star: boolean = false
  committed: number = 0
  passed: number = 0
  score: number = 0
  details?: Array<ScoreDetail>
}

export class RankingContest {
  contest?: Contest
  problemIds?: Array<number>
  ranking?: Array<Ranking>
}

/**
 * 分页数据
 */
export type Page<T> = {
  data: T[]
  total: number
}

export type SubmitData = {
  uid: number
  problemId: number
  contestId: number | null
  language: number
  sourceCode: string
  type: number
}

export class JudgeResult {
  solutionId?: string
  uid?: number
  problemId?: number
  language?: number
  title?: string
  state?: number
  result?: number
  stateText?: string
  resultText?: string
  score?: number
  total?: number
  passed?: number
  passRate?: number
  time?: number
  memory?: number
  errorInfo?: string
  sourceCode?: string
  /**
   * UNIX_TIMESTAMP(毫秒)
   */
  submitTime?: number
}

export type Language = {
  count: number
  language: number
}

export type Results = {
  [key: string]: number
  total: number
}

export type Activity = {
  date: string
  count: number
}

export class Overview {
  preference: Array<Language> = []
  results: Results = {
    AC: 0,
    WA: 0,
    CE: 0,
    RE: 0,
    MLE: 0,
    TLE: 0,
    total: 0
  }
  // eg: "YYYY-MM-DD": 10
  activities: { [date: string]: number } = {}
}

export class QueueInfo {
  name: string = ""
  messageCount: number = 0
  consumerCount: number = 0
}

export class Settings {
  alwaysShowRanking?: boolean
  showAllContest?: boolean
  showPassedPoints?: boolean
  autoDelSolutions?: boolean
}
