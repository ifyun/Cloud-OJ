<template>
  <div id="root">
    <error-info v-if="error.code != null" :error="error"/>
    <div v-else>
      <el-card id="content" class="borderless" style="width: 100%">
        <el-row :gutter="5">
          <el-col :span="12">
            <div class="problem" style="overflow: auto" :style="{height: contentHeight()}">
              <el-tabs class="borderless no-shadow" type="border-card">
                <el-tab-pane>
                <span slot="label">
                  <i class="el-icon-tickets el-icon--left"/>
                  <span>题目描述</span>
                </span>
                  <el-skeleton :loading="loading" :rows="10" animated>
                    <template>
                      <div v-if="problem.problemId !== undefined">
                        <span id="title">{{ `${problem.problemId}. ${problem.title}` }}</span>
                        <div>
                          <div class="limits">
                            <el-tag type="info" effect="dark" size="small">
                              <i class="el-icon-question el-icon--left"/>
                              {{ problem.score }} 分
                            </el-tag>
                            <el-tag type="success" effect="dark" size="small">
                              <i class="el-icon-time el-icon--left"/>
                              {{ problem.timeout }} ms
                            </el-tag>
                            <el-tag type="primary" effect="dark" size="small">
                              <i class="el-icon-cpu el-icon--left"/>
                              {{ problem.type === 0 ? problem.memoryLimit : 0 }} MB
                            </el-tag>
                          </div>
                          <markdown-it :content="problem.description"/>
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </el-tab-pane>
                <el-tab-pane>
                <span slot="label">
                  <Icon name="history" class="el-icon--left" scale="0.8"/>
                  <span>提交记录</span>
                </span>
                  <history-list v-if="userInfo != null" :single-mode="true" :problem-id="problemId"
                                :title="problem.title" @changeCode="setCode"/>
                  <el-result v-else icon="warning" sub-title="请登录后查看">
                  </el-result>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="editor" :style="{height: contentHeight()}">
              <div :style="{height: codeHeight()}" style="position: relative">
                <div class="commit-toolbar">
                  <img class="lang-icon" :src="languageIcons[language].icon"
                       :alt="languageIcons[language].name">
                  <el-select v-model="language" size="small" style="flex: 1"
                             @change="languageChange">
                    <el-option v-for="lang in enabledLanguages" :key="lang.name"
                               :label="`语言: ${lang.name}`" :value="lang.id">
                      <span style="float: left">{{ lang.name }}</span>
                      <span style="float: right">{{ lang.version }}</span>
                    </el-option>
                  </el-select>
                  <el-select v-model="cmOptions.theme" size="small" style="flex: 1"
                             @change="savePreference">
                    <el-option v-for="theme in codeStyle" :key="theme.id"
                               :label="`主题: ${theme.name}`" :value="theme.id">
                      <span style="float: left">{{ theme.name }}</span>
                      <span style="float: right">{{ theme.type }}</span>
                    </el-option>
                  </el-select>
                  <el-button size="mini" type="success" style="height: 32px"
                             :disabled="code.trim().length === 0" @click="commitCode">
                    <Icon name="play" class="el-icon--left" scale="0.85"/>
                    <span>提交运行</span>
                  </el-button>
                </div>
                <textarea id="editor" ref="editor"></textarea>
                <div v-if="code.trim() === ''" id="hint">
                  <Icon class="el-icon--left" name="file-import"/>
                  <span>拖入文件可导入代码</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <el-dialog custom-class="result-dialog" width="520px" :visible.sync="resultDialog.visible"
                 :close-on-click-modal="false" :close-on-press-escape="false"
                 :title="`提交 ${problem.problemId}. ${problem.title}`">
        <el-steps class="steps" simple :active="resultDialog.active"
                  process-status="wait" finish-status="success">
          <el-step title="提交代码"></el-step>
          <el-step title="等待判题"></el-step>
          <el-step title="获取结果"></el-step>
        </el-steps>
        <el-result v-if="result.title != null" :icon="result.type"
                   :title="result.title" :sub-title="result.desc">
        </el-result>
        <pre v-if="result.errorInfo != null && result.errorInfo.length > 0"
             class="result-error">{{ result.errorInfo }}</pre>
        <div v-if="result.title != null" slot="footer">
          <el-button icon="el-icon-refresh" size="small"
                     :disabled="resultDialog.disableRefresh"
                     @click="getResult(solutionId, 1)">
            <span>重试</span>
          </el-button>
          <el-button type="success" icon="el-icon-right"
                     size="small" @click="resultDialog.visible = false">
            <span>继续</span>
          </el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import ErrorInfo from "@/components/ErrorInfo"
import HistoryList from "@/components/HistoryList"
import MarkdownIt from "@/components/MarkdownIt"
import {Notice, prettyMemory, userInfo} from "@/util"
import {ContestApi, JudgeApi, ProblemApi} from "@/service"
import codemirror from "codemirror"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/clike/clike.js"
import "codemirror/mode/go/go.js"
import "codemirror/mode/python/python.js"
import "codemirror/mode/shell/shell.js"
import "codemirror/mode/javascript/javascript.js"
import "codemirror/mode/sql/sql.js"
import "codemirror/theme/eclipse.css"
import "codemirror/theme/monokai.css"
import "codemirror/theme/material.css"
import "codemirror/theme/darcula.css"
import "codemirror/theme/panda-syntax.css"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import "codemirror/addon/fold/foldcode.js"
import "codemirror/addon/fold/foldgutter.js"
import "codemirror/addon/fold/brace-fold.js"
import "codemirror/addon/fold/indent-fold.js"
import "codemirror/addon/fold/foldgutter.css"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/play"
import "vue-awesome/icons/history"
import "vue-awesome/icons/file-import"
import {languages, sqlTypes} from "@/util/data"

const CodeMirror = window.CodeMirror || codemirror

const languageMode = [
  "text/x-csrc",
  "text/x-c++src",
  "text/x-java",
  "text/x-python",
  "text/x-sh",
  "text/x-csharp",
  "text/javascript",
  "text/x-kotlin",
  "text/x-go"
]

const languageOptions = [
  {id: 0, name: "C", version: "gcc"},
  {id: 1, name: "C++", version: "g++"},
  {id: 2, name: "Java", version: "1.8"},
  {id: 3, name: "Python", version: "3.5"},
  {id: 4, name: "Bash Shell"},
  {id: 5, name: "C#", version: "Mono"},
  {id: 6, name: "JavaScript", version: "Node.js"},
  {id: 7, name: "Kotlin"},
  {id: 8, name: "Go"}
]

const sqlOptions = [
  {id: 0, name: "SQLite", mode: "text/x-mysql"}
]

const ACCEPT = 2, IN_QUEUE = 1, JUDGED = 0

export default {
  name: "Commit",
  components: {
    HistoryList,
    ErrorInfo,
    MarkdownIt,
    Icon
  },
  watch: {
    code(val) {
      window.localStorage.setItem("code", JSON.stringify({
        problemId: this.problemId,
        language: this.language,
        content: val
      }))
    },
    cmOptions: {
      handler(newVal) {
        this.editor.setOption("mode", newVal.mode)
        this.editor.setOption("theme", newVal.theme)
      },
      deep: true
    }
  },
  data() {
    return {
      loading: true,
      windowHeight: document.body.clientHeight,
      userInfo: userInfo(),
      error: {
        code: null,
        msg: ""
      },
      problemId: this.$route.query.problemId,
      contestId: this.$route.query.contestId,
      code: "",
      /* 高亮主题选项 */
      codeStyle: [
        {id: "eclipse", name: "Eclipse", type: "Light"},
        {id: "monokai", name: "Monokai", type: "Dark"},
        {id: "material", name: "Material", type: "Dark"},
        {id: "darcula", name: "Darcula", type: "Dark"},
        {id: "panda-syntax", name: "Panda", type: "Dark"}
      ],
      languageIcons: languages,
      editor: null,
      /* CodeMirror 配置 */
      cmOptions: {
        mode: "text/x-csrc",
        theme: "darcula",
        tabSize: 4,
        smartIndent: true,
        indentUnit: 4,
        lineNumbers: true,
        matchBrackets: true,
        autoCloseBrackets: true,
        foldGutter: true,
        gutters: [
          "CodeMirror-linenumbers",
          "CodeMirror-foldgutter",
          "CodeMirror-lint-markers"
        ]
      },
      problem: {},
      contest: {},
      enabledLanguages: [],
      language: 0,
      solutionId: "",
      /* 判题结果 UI 数据 */
      result: {
        type: null,
        title: null,
        desc: null,
        errorInfo: null
      },
      resultDialog: {
        disableRefresh: true,
        visible: false,
        active: 1
      }
    }
  },
  beforeMount() {
    const p = this.$siteSetting.preference

    if (p != null) {
      // 加载上次使用的语言的高亮主题
      this.cmOptions.theme = p.highlight
      if (this.contestId == null) {
        this.language = p.language
      }
    }

    this.getProblem()
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.editor, this.cmOptions)
    // CodeMirror 编辑器改变更新到 code 属性
    this.editor.on("change", (cm) => {
      this.code = cm.getValue()
    })
    const ctx = this
    window.onresize = () => {
      ctx.windowHeight = document.body.clientHeight
    }
    this.getCachedCode()
    /* eslint-disable no-console */
    console.log(
        "%c别学了，打游戏不香吗?",
        "color: white; background: orange; padding: 2px 8px;" +
        "font-family: sans-serif;"
    )
  },
  methods: {
    /* 计算题目内容区域高度 */
    contentHeight() {
      const offset = 200
      if (this.windowHeight <= 900) {
        return `${900 - offset}px`
      } else if (this.windowHeight >= 1300) {
        return `${1300 - offset}px`
      } else {
        return `${this.windowHeight - offset}px`
      }
    },
    /* 计算代码编辑器高度 */
    codeHeight() {
      const offset = 238  // content offset + toolbar height
      if (this.windowHeight <= 900) {
        return `${900 - offset}px`
      } else if (this.windowHeight >= 1300) {
        return `${1300 - offset}px`
      } else {
        return `${this.windowHeight - offset}px`
      }
    },
    setCode(code, lang) {
      this.editor.setValue(code)
      this.language = lang
      this.languageChange()
    },
    /* 从 localStorage 获取上一次的代码 */
    getCachedCode() {
      const code = JSON.parse(window.localStorage.getItem("code"))
      if (code != null && Number(code.problemId) === Number(this.problemId)) {
        this.setCode(code.content, code.language)
      }
    },
    calcLanguages() {
      // SQL 题目
      if (this.problem.type === 1) {
        this.languageIcons = sqlTypes
        this.enabledLanguages = sqlOptions
        this.language = sqlOptions[0].id
        return
      }
      // 程序设计题目
      if (typeof this.contestId !== "undefined") {
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
      this.languageChange()
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
        this.$siteSetting.setTitle(`${data.problemId}. ${data.title}`)
        this.problem = data
        this.calcLanguages()
      }).catch((error) => {
        if (error.code === 401) {
          this.$bus.$emit("login")
        } else if (error.code === 404) {
          this.error = {
            code: 404,
            msg: "题目不存在"
          }
        } else {
          this.error = error
        }
      }).finally(() => {
        this.loading = false
      })
    },
    languageChange() {
      if (this.problem.type === 0) {
        this.cmOptions.mode = languageMode[parseInt(this.language)]
        this.savePreference()
      } else {
        this.cmOptions.mode = sqlOptions[0].mode
      }
    },
    savePreference() {
      this.$siteSetting.preference = {
        language: this.language,
        highlight: this.cmOptions.theme
      }
      this.$siteSetting.savePreference()
    },
    /**
     * 提交代码
     */
    commitCode() {
      if (userInfo() == null) {
        this.$bus.$emit("login")
        return
      }

      this.resultDialog.active = null
      this.resultDialog.step = 0

      this.result.title = null
      this.result.errorInfo = null

      let data = {
        userId: userInfo().userId,
        problemId: this.problemId,
        language: this.language,
        sourceCode: this.code.trim(),
        type: this.problem.type
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
              this.$bus.$emit("login")
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
              }, 500)
            } else if (count > 15) {
              this.result = {
                type: "info",
                title: "未获取到结果",
                desc: "可能提交人数过多，可手动刷新或稍后到提交记录查看",
                errorInfo: null
              }
              this.resultDialog.disableRefresh = false
            } else {
              switch (data["state"]) {
                case ACCEPT:
                  this.resultDialog.active < 2 && (this.resultDialog.active = 2)
                  setTimeout(() => {
                    this.getResult(solutionId, count + 1)
                  }, 500)
                  break
                case IN_QUEUE:
                  this.resultDialog.active < 3 && (this.resultDialog.active = 3)
                  setTimeout(() => {
                    this.getResult(solutionId, count + 1)
                  }, 500)
                  break
                case JUDGED:
                  this.resultDialog.active < 3 && (this.resultDialog.active = 3)
                  this.getResultText(data)
                  this.result.errorInfo = data.errorInfo
                  this.resultDialog.disableRefresh = true
              }
            }
          })
          .catch((error) => {
            if (error.code === 401) {
              this.$bus.$emit("login")
            } else {
              this.result = {
                type: "warning",
                title: "获取判题结果失败",
                desc: `${error.code} ${error.msg}`,
                errorInfo: null,
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
            title: "编译错误",
            desc: "请仔细检查代码"
          }
          break
        case 6:
          this.result = {
            type: "warning",
            title: "运行错误",
            desc: "你的程序无法正常运行，如果是脚本语言请检查语法"
          }
          break
        case 7:
          this.result = {
            type: "error",
            title: "内部错误",
            desc: "判题程序异常，请稍后重试或与管理员联系"
          }
          break
        case 8:
          this.result = {
            type: "warning",
            title: "输出超限",
            desc: "你的程序产生的输出已超出题目最限制"
          }
      }
    }
  }
}
</script>

<style lang="scss">
.commit-toolbar {
  .el-input__inner {
    border: none !important;
  }
}
</style>

<style scoped lang="scss">
#root {
  margin: 0 auto;
  min-width: 1100px;
  max-width: 1280px;
}

#content {
  .problem {
    padding-right: 10px;

    #title {
      color: var(--color-text-primary);
      font-size: var(--text-title);
      margin: 0 0 10px 0;
      display: inline-block;
    }

    .limits {
      margin-bottom: 15px;

      * {
        margin-left: 5px;

        &:first-child {
          margin-left: 0;
        }
      }
    }
  }

  .editor {
    padding-left: 15px;
    border-left: 1px solid #EBEEF5;
  }
}

.commit-toolbar {
  padding: 3px;
  border-radius: 2px;
  display: flex;
  flex-direction: row;
  align-items: center;
  background-color: #F5F7FA;

  * {
    margin-left: 3px;

    &:first-child {
      padding-left: 4px;
    }
  }
}

.lang-icon {
  height: 25px;
  width: 25px;
  min-width: 25px;
  padding: 2px;
}

#hint {
  position: absolute;
  bottom: 30px;
  width: 100%;
  font-size: 14px;
  color: #7A7A7A;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
}

.result-dialog {
  .steps {
    padding: 15px 20px;
    margin-bottom: 20px;
  }

  .el-result {
    padding-top: 10px;
    padding-bottom: 0;
  }

  .result-error {
    margin-top: 20px;
  }
}
</style>