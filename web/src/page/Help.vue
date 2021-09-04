<template>
  <el-card id="root" class="borderless">
    <div slot="header" class="clearfix">
      <span><i class="el-icon-s-help el-icon--left"></i>帮助文档</span>
    </div>
    <el-skeleton :loading="loading" :rows="20" animated>
      <template>
        <markdown-it id="content" :content="helpDoc"/>
      </template>
    </el-skeleton>
  </el-card>
</template>

<script>
import MarkdownIt from "@/components/MarkdownIt"
import axios from "axios"

export default {
  name: "Help",
  components: {
    MarkdownIt
  },
  beforeMount() {
    this.$siteSetting.setTitle("帮助")
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
      axios.get("/doc/help.md")
          .then((res) => {
            this.helpDoc = res.data
            this.loading = false
          })
    }
  }
}
</script>

<style scoped lang="scss">
#root {
  margin: 0 auto;
  width: 1100px;

  #content {
    padding: 5px 10px;
  }
}
</style>