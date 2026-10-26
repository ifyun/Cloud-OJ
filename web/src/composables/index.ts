import { onActivated, onDeactivated, onMounted, onUnmounted } from "vue"

/**
 * 兼容不确定 keep-alive 是否生效的组件
 *
 * 例如可能使用 v-if 的组件
 *
 * @param enter 初始化
 * @param exit 退出
 */
export function useActivated(
  enter: () => void | Promise<void>,
  exit?: () => void
) {
  let activated = true
  let deactivated = false

  onMounted(() => {
    deactivated = false
    enter()
  })

  onActivated(() => {
    if (activated) {
      activated = false
      return
    }

    enter()
  })

  if (exit) {
    onDeactivated(() => {
      exit()
    })

    onUnmounted(() => {
      if (!deactivated) {
        deactivated = true
        exit()
      }
    })
  }
}
