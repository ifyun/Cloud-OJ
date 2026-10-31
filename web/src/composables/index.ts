import { nextTick, ref } from "vue"

export function useContextMenu() {
  const menuVisible = ref(false)
  const menuPoint = ref({
    x: 0,
    y: 0
  })

  function openMenu(e: MouseEvent, onOpen: () => void) {
    e.preventDefault()
    menuVisible.value = false

    nextTick(() => {
      onOpen()
      menuPoint.value = { x: e.clientX, y: e.clientY }
      menuVisible.value = true
    })
  }

  return {
    menuVisible,
    menuPoint,
    openMenu
  }
}
