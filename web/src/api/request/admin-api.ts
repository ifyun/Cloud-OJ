import { ApiPath } from "@/api"
import { type QueuesInfo } from "@/api/type"

class QueuesInfoPoller {
  private sse: EventSource

  constructor(
    token: string | undefined,
    onMessage: (data: QueuesInfo) => void,
    onError: (error: string) => void
  ) {
    this.sse = new EventSource(`${ApiPath.QUEUE_INFO}?token=${token}`)
    this.sse.onmessage = (event) => {
      const data = JSON.parse(event.data) as QueuesInfo
      onMessage(data)
    }

    this.sse.onerror = (event) => {
      const data = (event as MessageEvent).data
      if (data) {
        onError(data)
      }
    }
  }

  // 组件解除挂载时调用
  public close() {
    this.sse.close()
  }
}

export { QueuesInfoPoller }
