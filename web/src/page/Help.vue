<template>
  <div style="height: 100%">
    <el-container class="container">
      <el-card class="borderless">
        <div slot="header" class="clearfix">
          <span><i class="el-icon-s-help el-icon--left"></i>帮助文档</span>
        </div>
        <el-skeleton :loading="loading" :rows="20" animated>
          <template>
            <markdown-it :content="helpDoc"/>
          </template>
        </el-skeleton>
      </el-card>
      <bottom-area class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import BottomArea from "@/components/common/BottomArea"
import MarkdownIt from "@/components/MarkdownIt"
import axios from "axios"

export default {
  name: "Help",
  components: {
    MarkdownIt,
    BottomArea
  },
  beforeMount() {
    this.siteSetting.setTitle("帮助")
    this.loadHelpDoc()
  },
  data() {
    return {
      loading: true,
      helpDoc: ""
    }
  },
  methods: {
    loadHelpDoc() {
      axios.get("./doc/help.md")
          .then((res) => {
            this.helpDoc = res.data
            this.loading = false
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