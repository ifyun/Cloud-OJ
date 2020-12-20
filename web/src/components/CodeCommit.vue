<template>
  <Error v-if="error.code != null"
         :error="error"/>
  <el-container v-else class="container">
    <el-page-header v-if="problem.problemId !== undefined"
                    :content="`${problemId}. ${problem.title}`"
                    @back="back">
    </el-page-header>
    <div style="width: 100%; margin-top: 20px"
         v-if="problem.problemId !== undefined">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-card style="overflow: auto"
                   :style="{height: calcContentHeight()}">
            <div style="margin: 10px">
              <el-button-group style="margin-bottom: 25px">
                <el-button size="small" icon="el-icon-warning">
                  {{ problem.score }} 分
                </el-button>
                <el-button size="small" icon="el-icon-time">
                  {{ problem.timeout }} ms
                </el-button>
                <el-button size="small" icon="el-icon-cpu">
                  64 MB
                </el-button>
              </el-button-group>
              <markdown-it-vue ref="md" :options="mdOptions" :content="problem.description"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card :style="{height: calcContentHeight()}">
            <el-form :inline="true" size="medium">
              <el-row>
                <el-col :span="12">
                  <el-form-item label="语言">
                    <el-select v-model="language" placeholder="请选择语言"
                               @change="languageChange">
                      <el-option v-for="lang in enabledLanguages"
                                 :key="lang.name"
                                 :label="lang.name"
                                 :value="lang.id">
                        <span style="float: left">{{ lang.name }}</span>
                        <span style="float: right">{{ lang.version }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="主题" style="float: right">
                    <el-select v-model="cmOptions.theme">
                      <el-option v-for="theme in codeStyle"
                                 :key="theme.id"
                                 :label="theme.name"
                                 :value="theme.id">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <div :style="{height: calcCodeHeight()}">
              <codemirror v-model="code" :options="cmOptions">
              </codemirror>
            </div>
            <el-row style="margin-top: 25px">
              <el-col :span="14">
                <el-button size="medium" type="success" round
                           :disabled="disableCommit" @click="commitCode">
                  <Icon name="play"/>
                  <span style="margin-left: 10px">提交运行</span>
                </el-button>
              </el-col>
              <el-col :span="10">
                <el-alert type="info" :closable="false" show-icon title="将文件拖入编辑框可导入代码"/>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <el-dialog title="获取判题结果" width="600px"
               :visible.sync="resultDialog.visible"
               :close-on-click-modal="false"
               :close-on-press-escape="false">
      <el-steps class="steps" simple :active="resultDialog.active"
                process-status="process" finish-status="success">
        <el-step :icon="resultDialog.active == null ? 'el-icon-loading': null"
                 :title="resultDialog.steps[resultDialog.step]"/>
      </el-steps>
      <el-alert v-if="result.title !== undefined" style="margin-top: 15px" effect="dark"
                show-icon :closable="false" :type="result.type"
                :title="result.title" :description="result.desc">
      </el-alert>
      <el-button style="margin-top: 25px" icon="el-icon-refresh"
                 size="medium" round :disabled="resultDialog.disableRefresh"
                 @click="getResult(solutionId, 1)">
        <span>重试</span>
      </el-button>
    </el-dialog>
  </el-container>
</template>

<script>
import Error from "@/components/Error"
import {toLoginPage, searchParams, userInfo, Notice} from "@/script/util"
import {apiPath} from "@/script/env"
import {codemirror} from "vue-codemirror"
import "codemirror/mode/clike/clike.js"
import "codemirror/mode/python/python.js"
import "codemirror/mode/shell/shell.js"
import "codemirror/mode/javascript/javascript"
import "codemirror/lib/codemirror.css"
import "codemirror/theme/monokai.css"
import "codemirror/theme/material.css"
import "codemirror/theme/material-darker.css"
import "codemirror/theme/dracula.css"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import MarkdownItVue from "markdown-it-vue"
import "markdown-it-vue/dist/markdown-it-vue.css"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/play"

const languageMode = [
  "text/x-csrc",
  "text/x-c++src",
  "text/x-java",
  "text/x-python",
  "text/x-sh",
  "text/x-csharp",
  "text/javascript",
  "text/x-kotlin"
]

const languageOptions = [
  {id: 0, name: "C", version: "gcc"},
  {id: 1, name: "C++", version: "g++"},
  {id: 2, name: "Java", version: "1.8"},
  {id: 3, name: "Python", version: "3.5"},
  {id: 4, name: "Bash"},
  {id: 5, name: "C#", version: "Mono"},
  {id: 6, name: "JavaScript", version: "Node v14"},
  {id: 7, name: "Kotlin", version: "1.4.10"}
]

const ACCEPT = 2, IN_QUEUE = 1, JUDGED = 0

export default {
  name: "CodeCommit",
  components: {
    codemirror,
    MarkdownItVue,
    Error,
    Icon
  },
  computed: {
    disableCommit: vm => {
      return vm.code.trim().length === 0
    }
  },
  mounted() {
    const ctx = this
    window.onresize = () => {
      ctx.windowHeight = document.body.clientHeight
    }
    this.getLanguages()
    this.getProblem()
    this.getCachedCode()
  },
  data() {
    return {
      windowHeight: document.body.clientHeight,
      mdOptions: {
        markdownIt: {
          html: true
        }
      },
      error: {
        code: null,
        text: ""
      },
      problemId: searchParams().problemId,
      contestId: searchParams().contestId,
      code: "",
      codeStyle: [
        {id: "monokai", name: "Monokai"},
        {id: "material", name: "Material"},
        {id: "material-darker", name: "Material Darker"},
        {id: "dracula", name: "Dracula"},
      ],
      cmOptions: {
        mode: "text/x-csrc",
        theme: "monokai",
        tabSize: 4,
        smartIndent: true,
        indentUnit: 4,
        lineNumbers: true,
        line: true,
        showHint: true,
        matchBrackets: true,
        autoCloseBrackets: true
      },
      problem: {},
      contest: {},
      enabledLanguages: [],
      language: 0,
      solutionId: "",
      result: {},
      resultDialog: {
        disableRefresh: true,
        visible: false,
        active: null,
        step: 0,
        steps: [
          "已提交，等待写入...",
          "在判题队列中，正在等待结果...",
          "判题完成"
        ]
      }
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    calcContentHeight() {
      if (this.windowHeight <= 900)
        return "800px"
      else if (this.windowHeight >= 1200)
        return "1100px"
      else
        return `${this.windowHeight - 130}px`
    },
    calcCodeHeight() {
      if (this.windowHeight <= 900)
        return "640px"
      else if (this.windowHeight >= 1200)
        return "850px"
      else
        return `${this.windowHeight - 290}px`
    },
    getCachedCode() {
      let code = window.sessionStorage.getItem("code");
      if (code != null) {
        code = JSON.parse(code)
        this.code = code["code"]
        this.language = code["language"]
        this.languageChange(this.language)
        this.$forceUpdate()
      }
    },
    getLanguages() {
      // 如果是竞赛/作业的题目，先获取允许的语言
      if (this.contestId !== undefined) {
        this.$axios({
          url: `${apiPath.contest}/lang/${this.contestId}`,
          method: "get"
        }).then((res) => {
          let languages = res.data.languages
          // 计算可用的语言
          languageOptions.forEach((value, index) => {
            let t = 1 << index
            if ((languages & t) === t) {
              this.enabledLanguages.push(value)
            }
          })
          this.language = this.enabledLanguages[0].id
        }).catch((error) => {
          const res = error.response
          Notice.notify.error(this, {
            title: "无法获取语言",
            message: `${res.status} ${res.statusText}`
          })
        })
      } else {
        this.enabledLanguages = languageOptions
      }
    },
    getProblem() {
      let url = apiPath.problem
      let headers = {}, params = {}
      if (userInfo() != null && userInfo()["roleId"] >= 2) {
        url = `${apiPath.problem}/pro`
        headers.userId = userInfo().userId
        headers.token = userInfo().token
      }
      if (this.contestId !== undefined) {
        url = `${apiPath.contest}/problem/${this.contestId}`
        headers.userId = userInfo().userId
        headers.token = userInfo().token
      }
      this.$axios({
        url: `${url}/${this.problemId}`,
        method: "get",
        headers: headers,
        params: params
      }).then((res) => {
        if (res.status === 200) {
          this.problem = res.data
          document.title = `${this.problem.title} - Cloud OJ`
        } else if (res.status === 204) {
          this.error = {
            code: 404,
            text: "题目不存在"
          }
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          let data = res.data;
          let errorText = data === undefined ? res.statusText : data.msg
          this.error = {
            code: res.status,
            text: errorText
          }
        }
      })
    },
    languageChange(value) {
      this.cmOptions.mode = languageMode[value]
    },
    /**
     * 提交代码
     */
    commitCode() {
      if (userInfo() == null) {
        alert("请先登录！")
        return
      }
      this.resultDialog.active = null
      this.resultDialog.step = 0
      this.result = {}
      let data = {
        userId: userInfo().userId,
        problemId: this.problemId,
        language: this.language,
        sourceCode: this.code
      }
      if (this.contestId !== undefined)
        data.contestId = this.contestId
      this.$axios({
        url: apiPath.commit,
        method: "post",
        headers: {
          "userId": userInfo().userId,
          "token": userInfo().token,
          "Content-Type": "application/json"
        },
        data: JSON.stringify(data)
      }).then((res) => {
        this.resultDialog.visible = true
        this.solutionId = res.data
        this.getResult(res.data, 1)
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: "提交失败",
            message: `${res.status} ${res.data === undefined ? res.statusText : res.data.msg}`
          })
        }
      })
    },
    /**
     * 获取结果
     *
     * @param solutionId 答案 uuid
     * @param count 重试次数
     */
    getResult(solutionId, count) {
      this.resultDialog.disableRefresh = true
      this.$axios({
        url: apiPath.commit,
        method: "get",
        headers: {
          "token": userInfo().token,
          "userId": userInfo().userId
        },
        params: {
          solutionId: this.solutionId,
          userId: userInfo().userId
        }
      }).then((res) => {
        if (res.status === 204 && count <= 15) {
          setTimeout(() => {
            this.getResult(solutionId, count + 1)
          }, 1000)
        } else if (count > 15) {
          this.result = {
            type: "info",
            title: "未获取到结果",
            desc: "可能提交人数过多，可手动刷新"
          }
          this.resultDialog.disableRefresh = false
        } else {
          switch (res.data["state"]) {
            case ACCEPT:
              this.resultDialog.step = 0
              setTimeout(() => {
                this.getResult(solutionId, count + 1)
              }, 1000)
              break
            case IN_QUEUE:
              this.resultDialog.step = 1
              setTimeout(() => {
                this.getResult(solutionId, count + 1)
              }, 1000)
              break
            case JUDGED:
              this.resultDialog.step = 2
              this.resultDialog.active = 1
              this.getResultText(res.data)
              this.resultDialog.disableRefresh = true
          }
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: "无法获取结果",
            message: `${res.status} ${res.statusText}`
          })
          this.resultDialog.disableRefresh = false
        }
      })
    },
    /**
     * 根据结果显示提示信息
     */
    getResultText(data) {
      switch (data.result) {
        case 0:
          this.result = {
            type: 'success',
            title: "完全正确",
            desc: "已通过全部测试点"
          }
          break
        case 1:
          this.result = {
            type: "warning",
            title: `时间超限(${data["passRate"] * 100})`,
            desc: "时间复杂度有待优化"
          }
          break
        case 2:
          this.result = {
            type: "warning",
            title: `内存超限(${data["passRate"] * 100})`,
            desc: "空间复杂度有待优化"
          }
          break
        case 3:
          this.result = {
            type: "warning",
            title: `部分通过(${data["passRate"] * 100})`,
            desc: "可能漏掉了部分情况"
          }
          break
        case 4:
          this.result = {
            type: "error",
            title: "答案错误",
            desc: "继续努力"
          }
          break
        case 5:
          this.result = {
            type: "info",
            title: "编译错误",
            desc: "请仔细检查代码"
          }
          break
        case 6:
          this.result = {
            type: "info",
            title: "运行错误",
            desc: "对于解释型语言，请检查是否存在语法错误"
          }
          break
        case 7:
          this.result = {
            type: "error",
            title: "判题异常",
            desc: "判题服务器出现了异常或者你提交了恶意代码"
          }
      }
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 20px;
  flex-direction: column;
  min-width: 1200px !important;
}

.steps {
  padding: 12px 20px;
}
</style>