export type LanguageOption = {
  value: number
  label: string
  version: string
}

export const LanguageOptions: Array<LanguageOption> = [
  { value: 0, label: "C", version: "" },
  { value: 1, label: "C++", version: "" },
  { value: 2, label: "Java", version: "OpenJDK 1.8" },
  { value: 3, label: "Python", version: "3.5" },
  { value: 4, label: "Bash Shell", version: "" },
  { value: 5, label: "C#", version: "Mono" },
  { value: 6, label: "JavaScript", version: "Node.js" },
  { value: 7, label: "Kotlin", version: "1.4.10" },
  { value: 8, label: "Go", version: "1.15.7" }
]

export const LanguageNames = [
  "C",
  "C++",
  "Java",
  "Python",
  "Bash",
  "JavaScript",
  "Kotlin",
  "Go"
]

export const ResultTypes = [
  { type: "success", text: "完全正确" },
  { type: "warning", text: "时间超限" },
  { type: "warning", text: "内存超限" },
  { type: "warning", text: "部分通过" },
  { type: "error", text: "答案错误" },
  { type: "error", text: "编译错误" },
  { type: "error", text: "运行错误" },
  { type: "error", text: "内部错误" },
  { type: "warning", text: "输出超限" }
]
