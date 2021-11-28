<template>
  <n-avatar round :src="url" :size="size" @error="url= ''" style="vertical-align: middle"/>
</template>

<script lang="ts">
import {Options} from "vue-class-component"
import {Prop, Watch} from "vue-property-decorator"
import {NAvatar} from "naive-ui"
import {VueComponent} from "@/vue-ts-component"

interface Props {
  userId: string
  size?: string | number
}

@Options({
  name: "UserAvatar",
  components: {
    NAvatar
  }
})
export default class UserAvatar extends VueComponent<Props> {
  @Prop(String)
  private readonly userId?: string

  @Prop({type: [String, Number], default: "medium"})
  private readonly size?: string | number

  private url: string = ""

  @Watch("userId", {immediate: true})
  userIdChanged(value: string) {
    this.url = `/api/file/image/avatar/${value}.png`
  }
}
</script>
