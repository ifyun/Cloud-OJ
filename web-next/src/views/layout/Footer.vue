<template>
  <div class="footer">
    <n-button text tag="a" href="https://github.com/imcloudfloating" target="_blank">
      © Cloud Li
    </n-button>
    <n-divider vertical/>
    <n-button text tag="a" href="https://github.com/imcloudfloating/Cloud-OJ" target="_blank">
      <template #icon>
        <n-icon size="16">
          <github-icon/>
        </n-icon>
      </template>
      GitHub
    </n-button>
    <n-divider vertical/>
    <n-button text>MIT License</n-button>
  </div>
</template>

<script>
import {Options, Vue} from "vue-class-component"
import {NButton, NDivider, NIcon} from "naive-ui"
import {Github as GithubIcon} from "@vicons/fa"

@Options({
  components: {
    NDivider,
    NIcon,
    NButton,
    GithubIcon
  }
})
export default class Footer extends Vue {
}
</script>

<style scoped lang="scss">
.footer {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 15px 0;
}
</style>
