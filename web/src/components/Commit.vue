<template>
  <Error v-if="error.code != null"
         :error="error"/>
  <el-container v-else class="container">
    <el-card style="width: 100%" v-if="problem.problemId !== undefined">
      <el-row :gutter="5">
        <el-col :span="12">
          <div class="content" style="overflow: auto" :style="{height: contentHeight()}">
            <span>{{ `${problemId}. ${problem.title}` }}</span>
            <div style="margin-top: 15px">
              <el-button-group class="limits">
                <el-button size="mini" icon="el-icon-warning">
                  {{ problem.score }} 分
                </el-button>
                <el-button size="mini" icon="el-icon-time">
                  {{ problem.timeout }} ms
                </el-button>
                <el-button size="mini" icon="el-icon-cpu">
                  64 MB
                </el-button>
              </el-button-group>
              <markdown-it-vue ref="md" :options="mdOptions" :content="problem.description"/>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="content content-code" :style="{height: contentHeight()}">
            <div :style="{height: codeHeight()}">
              <div class="toolbar">
                <img class="lang-icon" :src="languageIcons[language].icon"
                     :alt="languageIcons[language].name">
                <el-select v-model="language" size="small"
                           @change="languageChange">
                  <el-option v-for="lang in enabledLanguages" :key="lang.name"
                             :label="`语言: ${lang.name}`" :value="lang.id">
                    <span style="float: left">{{ lang.name }}</span>
                    <span style="float: right">{{ lang.version }}</span>
                  </el-option>
                </el-select>
                <el-select v-model="cmOptions.theme" size=small
                           @change="savePreference">
                  <el-option v-for="theme in codeStyle" :key="theme.id"
                             :label="`主题: ${theme.name}`" :value="theme.id">
                    <span style="float: left">{{ theme.name }}</span>
                  </el-option>
                </el-select>
                <el-button size="mini" type="success" :disabled="disableCommit" @click="commitCode">
                  <Icon name="play" class="el-icon--left" scale="0.89"/>
                  <span>运行</span>
                </el-button>
                <el-button size="mini" type="info" :disabled="disableLastResult"
                           @click="resultDialog.visible = true">
                  <Icon name="history" class="el-icon--left" scale="0.89"/>
                  <span>上次结果</span>
                </el-button>
              </div>
              <codemirror v-model="code" :options="cmOptions">
              </codemirror>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    <el-dialog title="获取判题结果" width="600px"
               :visible.sync="resultDialog.visible"
               :close-on-click-modal="false"
               :close-on-press-escape="false">
      <el-steps class="steps" simple :active="resultDialog.active"
                process-status="process" finish-status="success">
        <el-step :icon="resultDialog.active == null ? 'el-icon-loading': null"
                 :title="resultDialog.steps[resultDialog.step]"/>
      </el-steps>
      <el-alert v-if="result.title !== undefined" style="margin-top: 15px"
                show-icon :closable="false" :type="result.type"
                :title="result.title" :description="result.desc">
      </el-alert>
      <div v-if="result.errorInfo !== undefined">
        <pre class="result-error">{{ result.errorInfo }}</pre>
      </div>
      <el-button style="margin-top: 10px" icon="el-icon-refresh"
                 size="medium" round :disabled="resultDialog.disableRefresh"
                 @click="getResult(solutionId, 1)">
        <span>重试</span>
      </el-button>
    </el-dialog>
  </el-container>
</template>

<script>
import Error from "@/components/Error"
import {Notice, toLoginPage, searchParams, userInfo, prettyMemory} from "@/util"
import {ContestApi, JudgeApi, ProblemApi} from "@/service"
import {codemirror} from "vue-codemirror"
import "codemirror/mode/clike/clike.js"
import "codemirror/mode/python/python.js"
import "codemirror/mode/shell/shell.js"
import "codemirror/mode/javascript/javascript"
import "codemirror/lib/codemirror.css"
import "codemirror/theme/eclipse.css"
import "codemirror/theme/monokai.css"
import "codemirror/theme/material.css"
import "codemirror/theme/darcula.css"
import "codemirror/theme/panda-syntax.css"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import MarkdownItVue from "markdown-it-vue"
import "markdown-it-vue/dist/markdown-it-vue.css"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/play"
import "vue-awesome/icons/history"
import {languageIcons} from "@/util/data"

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
  {id: 0, name: "C", version: "gcc(c11)"},
  {id: 1, name: "C++", version: "g++(c++17)"},
  {id: 2, name: "Java", version: "1.8"},
  {id: 3, name: "Python", version: "3.5"},
  {id: 4, name: "Bash"},
  {id: 5, name: "C#", version: "Mono"},
  {id: 6, name: "JavaScript", version: "Node.js"},
  {id: 7, name: "Kotlin", version: "1.4.10"}
]

const ACCEPT = 2, IN_QUEUE = 1, JUDGED = 0

export default {
  name: "Commit",
  components: {
    codemirror,
    MarkdownItVue,
    Error,
    Icon
  },
  computed: {
    disableCommit: (vm) => {
      return vm.code.trim().length === 0
    },
    disableLastResult: (vm) => {
      return vm.result.title === undefined
    }
  },
  watch: {
    code(val) {
      window.sessionStorage.setItem("code", JSON.stringify({
        problemId: this.problemId,
        language: this.language,
        content: val
      }))
    }
  },
  beforeMount() {
    const p = this.siteSetting.preference
    this.language = p.language
    this.cmOptions.theme = p.highlight
    this.languageChange()
    this.getProblem()
    this.getCachedCode()
  },
  mounted() {
    const ctx = this
    window.onresize = () => {
      ctx.windowHeight = document.body.clientHeight
    }
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
        msg: ""
      },
      problemId: searchParams().problemId,
      contestId: searchParams().contestId,
      code: "",
      codeStyle: [
        {id: "eclipse", name: "Eclipse"},
        {id: "monokai", name: "Monokai"},
        {id: "material", name: "Material"},
        {id: "darcula", name: "Darcula"},
        {id: "panda-syntax", name: "Panda"}
      ],
      languageIcons,
      cmOptions: {
        mode: "text/x-csrc",
        theme: "darkula",
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
          "在队列中，等待判题...",
          "判题完成"
        ]
      }
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    contentHeight() {
      const offset = 120
      if (this.windowHeight <= 900) {
        return `${900 - offset}px`
      } else if (this.windowHeight >= 1300) {
        return `${1300 - offset}px`
      } else {
        return `${this.windowHeight - offset}px`
      }
    },
    codeHeight() {
      const offset = 155
      if (this.windowHeight <= 900) {
        return `${900 - offset}px`
      } else if (this.windowHeight >= 1200) {
        return `${1200 - offset}px`
      } else {
        return `${this.windowHeight - offset}px`
      }
    },
    getCachedCode() {
      const code = JSON.parse(window.sessionStorage.getItem("code"))
      if (code != null && Number(code.problemId) === Number(this.problemId)) {
        this.code = code.content
        this.language = code.language
        this.languageChange(this.language)
        this.$forceUpdate()
      }
    },
    calcLanguages() {
      if (this.contestId !== undefined) {
        let languages = this.problem.languages
        // 计算可用的语言
        languageOptions.forEach((value, index) => {
          let t = 1 << index
          if ((languages & t) === t) {
            this.enabledLanguages.push(value)
          }
        })
        this.language = this.enabledLanguages[0].id
      } else {
        this.enabledLanguages = languageOptions
      }
    },
    getProblem() {
      let promise

      if (this.contestId != null) {
        promise = ContestApi.getProblem(this.contestId, this.problemId, userInfo())
      } else if (userInfo() != null && userInfo()["roleId"] >= 2) {
        promise = ProblemApi.get(this.problemId, userInfo())
      } else {
        promise = ProblemApi.get(this.problemId)
      }

      promise.then((data) => {
        this.siteSetting.setTitle(data.title)
        this.problem = data
        this.calcLanguages()
      }).catch((error) => {
        if (error.code === 401) {
          toLoginPage()
        } else {
          this.error = error
        }
      })
    },
    languageChange() {
      this.cmOptions.mode = languageMode[parseInt(this.language)]
      this.savePreference()
    },
    savePreference() {
      this.siteSetting.preference = {
        language: this.language,
        highlight: this.cmOptions.theme
      }
      this.siteSetting.savePreference()
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
      if (this.contestId !== undefined) {
        data.contestId = this.contestId
      }
      JudgeApi.commit(data, userInfo())
          .then((data) => {
            this.resultDialog.visible = true
            this.solutionId = data
            this.getResult(data, 1)
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: "提交失败",
                message: `${error.code} ${error.msg}`
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
      JudgeApi.getResult(solutionId, userInfo())
          .then((data) => {
            if (data == null && count <= 15) {
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
              switch (data["state"]) {
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
                  this.getResultText(data)
                  this.result.errorInfo = data.errorInfo
                  this.resultDialog.disableRefresh = true
              }
            }
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
            } else {
              this.result = {
                type: "warning",
                title: "获取判题结果失败",
                desc: `${error.code} ${error.msg}，可稍候重试`
              }
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
            type: "success",
            title: "完全正确",
            desc: `耗时: ${data.time}ms, 内存占用: ${prettyMemory(data.memory)}`
          }
          break
        case 1:
          this.result = {
            type: "warning",
            title: `时间超限(${data["passRate"] * 100})`,
            desc: `耗时: ${data.time}ms, 内存占用: ${prettyMemory(data.memory)}`
          }
          break
        case 2:
          this.result = {
            type: "warning",
            title: `内存超限(${data["passRate"] * 100})`,
            desc: `耗时: ${data.time}ms, 内存占用: ${prettyMemory(data.memory)}`
          }
          break
        case 3:
          this.result = {
            type: "warning",
            title: `部分通过(${data["passRate"] * 100})`,
            desc: "你可能漏掉了部分情况"
          }
          break
        case 4:
          this.result = {
            type: "error",
            title: "答案错误",
            desc: `耗时: ${data.time}ms, 内存占用: ${prettyMemory(data.memory)}`
          }
          break
        case 5:
          this.result = {
            type: "info",
            title: "编译错误"
          }
          break
        case 6:
          this.result = {
            type: "info",
            title: "运行错误"
          }
          break
        case 7:
          this.result = {
            type: "error",
            title: "内部错误"
          }
          break
        case 8:
          this.result = {
            type: "warning",
            title: "输出超限"
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
  min-width: 1250px !important;
  max-width: 1600px !important;
}

.content {
  padding: 0 15px;
}

.content-code {
  border-left: 1px solid #EBEEF5;
}

.limits {
  margin-bottom: 25px
}

.limits button {
  cursor: default;
}

.toolbar {
  margin-bottom: 3px;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.toolbar * {
  margin-left: 3px;
}

.toolbar *:first-child {
  margin-left: 0;
}

.lang-icon {
  height: 24px;
  width: 24px;
  padding: 2px;
  border-radius: 2px;
  border: 1px solid #DCDFE6;
}

.steps {
  padding: 12px 20px;
}
</style>