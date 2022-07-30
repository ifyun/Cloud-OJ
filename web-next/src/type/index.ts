import {
  BashIcon,
  CIcon,
  CppIcon,
  CSIcon,
  GoIcon,
  JavaIcon,
  JSIcon,
  KotlinIcon,
  PyIcon
} from "@/components/icons"

export type LanguageOption = {
  value: number
  label: string
  version: string
}

/**
 * 代码编辑器触发提交时的数据
 */
export type SourceCode = {
  language: number
  code: string
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
  "#F1E05A",
  "#A97BFF",
  "#00ADD8"
]

/**
 * 语言图标组件，下标就是ID
 */
export const LanguageIcons: Array<any> = [
  { component: CIcon, color: "#d14748" },
  { component: CppIcon, color: "#673ab7" },
  { component: JavaIcon, color: "#ff9811" },
  { component: PyIcon, color: "#3776ab" },
  { component: BashIcon, color: "#4eaa25" },
  { component: CSIcon, color: "#239120" },
  { component: JSIcon, color: "#f7df1e" },
  { component: KotlinIcon, color: "#7f52ff" },
  { component: GoIcon, color: "#00add8" }
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
  { type: "error", text: "输出超限" }
]
