import { Log } from "@/api/type"
import dayjs from "dayjs"

function shortenClassName(className: string): string {
  let len = className.length
  if (len > 40) {
    const arr = className.split(".")
    for (let i = 0; i < arr.length - 1; i++) {
      if (arr[i].startsWith("[")) {
        continue
      }

      const iLen = arr[i].length
      arr[i] = arr[i].substring(0, 1)
      len -= iLen - 1

      if (len <= 40) {
        break
      }
    }

    return arr.join(".")
  }

  return className
}

const LogFormatter = {
  format(log: Log) {
    return (
      `<span class="log-time">${dayjs(log.time).format("YYYY-MM-DD HH:mm:ss.SSS")}</span> ` +
      `<span class="log-level-${log.level.toLowerCase()}">${log.level.padStart(5)}</span> - ` +
      `<span class="log-instance-id">[${log.instanceId.padStart(23)}]</span> ` +
      `<span class="log-thread">[${log.thread.slice(-15).padStart(15)}]</span> ` +
      `<span class="log-class-name">${shortenClassName(log.className).padEnd(50)}</span> ` +
      `<span class="log-msg">: ${log.message}</span>`
    )
  }
}

export default LogFormatter
