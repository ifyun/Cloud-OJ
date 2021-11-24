<template>
  <n-avatar round :src="url" :size="size" @error="url= ''" style="vertical-align: middle"/>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {Prop, Watch} from "vue-property-decorator"
import {NAvatar} from "naive-ui"

@Options({
  name: "UserAvatar",
  components: {
    NAvatar
  }
})
export default class UserAvatar extends Vue {
  @Prop(String)
  private userId?: string

  @Prop([String, Number])
  private size: string | number = "medium"

  private url: string = ""

  @Watch("userId", {immediate: true})
  userIdChanged(value: string) {
    this.url = `/api/file/image/avatar/${value}.png`
  }
}
</script>
