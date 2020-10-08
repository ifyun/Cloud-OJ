<template>
  <div>
    <TopNavigation active="5"/>
    <el-container class="container">
      <el-card style="margin-bottom: 30px">
        <markdown-it-vue :content="helpDoc">
        </markdown-it-vue>
      </el-card>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import MarkdownItVue from "markdown-it-vue"
import 'markdown-it-vue/dist/markdown-it-vue.css'

export default {
  name: "Help",
  components: {
    TopNavigation,
    MarkdownItVue
  },
  mounted() {
    this.loadHelpDoc()
  },
  data() {
    return {
      helpDoc: ''
    }
  },
  methods: {
    loadHelpDoc() {
      this.$axios.get('./doc/help.md')
          .then((res) => {
            this.helpDoc = res.data
          }).catch((error) => {
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 25px;
  padding: 0 20px;
  flex-direction: column;
  max-width: 1100px;
}
</style>