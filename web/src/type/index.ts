/**
 * 代码编辑器触发提交时的数据
 */
export type SourceCode = {
  language: number
  code: string
}

export type LanguageOption = {
  value: number
  label: string
}

export const LanguageOptions: Array<LanguageOption> = [
  { value: 0, label: "C" },
  { value: 1, label: "C++" },
  { value: 2, label: "Java" },
  { value: 3, label: "Python" },
  { value: 4, label: "Bash Shell" },
  { value: 5, label: "C#" },
  { value: 6, label: "JavaScript" },
  { value: 7, label: "Kotlin" },
  { value: 8, label: "Go" }
]

export const LanguageNames: { [key: number]: string } = {
  0: "C",
  1: "C++",
  2: "Java",
  3: "Python",
  4: "Bash",
  5: "C#",
  6: "JavaScript",
  7: "Kotlin",
  8: "Go"
}

export const LanguageColors: { [key: number]: string } = {
  0: "#555555",
  1: "#F34B7D",
  2: "#B07219",
  3: "#3572A5",
  4: "#89E051",
  5: "#178600",
  6: "#F1E05A",
  7: "#A97BFF",
  8: "#00ADD8"
}

export const ResultColors: { [key: string]: string } = {
  AC: "#22c55e",
  PA: "#06b6d4",
  WA: "#ef4444",
  TLE: "#3b82f6",
  MLE: "#8b5cf6",
  OLE: "#f97316",
  RE: "#b45309",
  CE: "#64748b"
}

export const ResultNames: { [key: string]: string } = {
  AC: "完全正确",
  PA: "部分通过",
  WA: "答案错误",
  TLE: "时间超限",
  MLE: "内存超限",
  OLE: "输出超限",
  RE: "运行错误",
  CE: "编译错误",
  IE: "内部错误"
}

export const ResultTypes: { [key: number]: any } = {
  0: "success",
  1: "warning",
  2: "warning",
  3: "warning",
  4: "error",
  5: "error",
  6: "error",
  7: "error",
  8: "error"
}
