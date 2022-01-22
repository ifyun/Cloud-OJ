<template>
  <n-switch v-model:value="isDarkTheme">
    <template #checked>
      <n-icon color="#FCE100">
        <dark-icon/>
      </n-icon>
    </template>
    <template #unchecked>
      <n-icon>
        <dark-icon/>
      </n-icon>
    </template>
  </n-switch>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {NIcon, NSwitch} from "naive-ui"
import {DarkModeRound as DarkIcon} from "@vicons/material"
import Mutations from "@/store/mutations"

@Options({
  name: "ThemeSwitch",
  components: {
    NSwitch,
    NIcon,
    DarkIcon
  }
})
export default class ThemeSwitch extends Vue {
  private store = useStore()

  get isDarkTheme(): boolean {
    return this.store.state.theme != null
  }

  set isDarkTheme(value: boolean) {
    this.store.commit(Mutations.CHANGE_THEME, value ? "dark" : "light")
  }
}
</script>
