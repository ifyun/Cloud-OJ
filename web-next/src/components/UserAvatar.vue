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
  size: string | number
}

@Options({
  name: "UserAvatar",
  components: {
    NAvatar
  }
})
export default class UserAvatar extends VueComponent<Props> {
  @Prop()
  private readonly userId?: string

  @Prop()
  private readonly size: string | number = "medium"

  private url: string = ""

  @Watch("userId", {immediate: true})
  userIdChanged(value: string) {
    this.url = `/api/file/image/avatar/${value}.png`
  }
}
</script>
