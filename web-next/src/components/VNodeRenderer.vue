<script lang="ts">
import {h, PropType, VNode} from "vue"

export default {
  props: {
    vNode: {
      type: [Object as PropType<VNode>, null]
    }
  },
  render(props: any) {
    if (props.vNode != null) {
      return h(props.vNode)
    }

    return null
  }
}
</script>
