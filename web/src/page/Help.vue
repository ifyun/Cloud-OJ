<template>
  <div style="height: 100%">
    <TopNavigation active="5"/>
    <div class="wrapper">
      <el-container class="container">
        <el-card>
          <markdown-it-vue :content="helpDoc"/>
        </el-card>
        <BottomArea class="bottom"/>
      </el-container>
    </div>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import MarkdownItVue from "markdown-it-vue"
import "markdown-it-vue/dist/markdown-it-vue.css"

export default {
  name: "Help",
  components: {
    TopNavigation,
    BottomArea,
    MarkdownItVue
  },
  beforeMount() {
    document.title = "帮助 - Cloud OJ"
    this.loadHelpDoc()
  },
  data() {
    return {
      helpDoc: ""
    }
  },
  methods: {
    loadHelpDoc() {
      this.$axios.get("./doc/help.md")
          .then((res) => {
            this.helpDoc = res.data
          })
    }
  }
}
</script>

<style scoped>
.wrapper {
  margin-top: 60px;
  height: calc(100% + 60px);
  overflow-y: auto;
}

.container {
  margin-top: 35px;
  padding: 0 20px;
  flex-direction: column;
  max-width: 1100px;
}
</style>