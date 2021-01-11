<template>
  <div style="height: 100%">
    <TopNavigation active="5"/>
    <el-container class="container">
      <el-card>
        <markdown-it-vue :options="mdOptions" :content="helpDoc"/>
      </el-card>
      <BottomArea class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import axios from "axios"
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
      helpDoc: "",
      mdOptions: {
        markdownIt: {
          html: true
        }
      }
    }
  },
  methods: {
    loadHelpDoc() {
      axios.get("./doc/help.md")
          .then((res) => {
            this.helpDoc = res.data
          })
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 20px;
  flex-direction: column;
  max-width: 1100px;
}
</style>