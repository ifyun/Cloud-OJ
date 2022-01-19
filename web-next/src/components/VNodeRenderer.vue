<script lang="tsx">
import {PropType, VNode} from "vue"
import {Vue} from "vue-class-component"
import {Prop} from "vue-property-decorator"

export default class VNodeRenderer extends Vue {
  @Prop({type: Object as PropType<VNode>})
  private vNode?: VNode

  render() {
    return this.vNode
  }
}
</script>
