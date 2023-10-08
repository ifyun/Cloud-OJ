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
  version: string
}

export const LanguageOptions: Array<LanguageOption> = [
  { value: 0, label: "C", version: "11" },
  { value: 1, label: "C++", version: "14" },
  { value: 2, label: "Java", version: "Language Level 1.8" },
  { value: 3, label: "Python", version: "3.x" },
  { value: 4, label: "Bash Shell", version: "" },
  { value: 5, label: "C#", version: "Mono" },
  { value: 6, label: "JavaScript", version: "Node.js" },
  { value: 7, label: "Kotlin", version: "1.8.x" },
  { value: 8, label: "Go", version: "1.20.x" }
]

export const LanguageNames = [
  "C",
  "C++",
  "Java",
  "Python",
  "Bash",
  "C#",
  "JavaScript",
  "Kotlin",
  "Go"
]

export const LanguageColors = [
  "#555555",
  "#F34B7D",
  "#B07219",
  "#3572A5",
  "#89E051",
  "#178600",
  "#F1E05A",
  "#A97BFF",
  "#00ADD8"
]

type ResultType = "default" | "error" | "info" | "success" | "warning"

export const ResultTypes: Array<ResultType> = [
  "success",
  "warning",
  "warning",
  "warning",
  "error",
  "error",
  "error",
  "error",
  "error"
]
