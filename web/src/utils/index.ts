import { h } from "vue"
import { NIcon } from "naive-ui"
import { Contest } from "@/api/type"
import { InfoRound, PlayArrowRound } from "@vicons/material"

type StateTag = {
  type: "info" | "error" | "success"
  state: string
  icon: any
}

function setTitle(title: string) {
  document.title = `${title} - Cloud OJ`
}

const renderIcon = (icon: any, color: string | undefined = undefined) => {
  return () =>
    h(NIcon, color == null ? null : { color }, {
      default: () => h(icon)
    })
}

function stateTag(c: Contest): StateTag {
  if (c.ended) {
    return {
      type: "error",
      state: "已结束",
      icon: InfoRound
    }
  } else if (c.started) {
    return {
      type: "success",
      state: "进行中",
      icon: PlayArrowRound
    }
  } else {
    return {
      type: "info",
      state: "未开始",
      icon: InfoRound
    }
  }
}

function timeUsage(val?: number): string {
  if (val) {
    return `${(val / 1000).toFixed(2)} ms`
  }

  return "-"
}

function ramUsage(val?: number): string {
  if (val) {
    if (val >= 1024) {
      return `${(val / 1024).toFixed(2)} MB`
    } else {
      return `${val} KB`
    }
  }

  return "-"
}

export { default as LanguageUtil } from "./LanguageUtil"
export { default as LogFormatter } from "./LogFormatter"
export { setTitle, renderIcon, stateTag, timeUsage, ramUsage }
export type { StateTag }
