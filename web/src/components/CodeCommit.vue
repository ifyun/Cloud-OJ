<template>
  <Error style="margin-top: 35px" v-if="error.code !== undefined"
         :error="error"/>
  <el-container v-else class="container">
    <el-page-header v-if="problem.problemId !== undefined" style="margin-top: 5px"
                    :content="`${problemId}. ${problem.title}`"
                    @back="back">
    </el-page-header>
    <div style="width: 100%; margin-top: 25px" v-if="problem.problemId !== undefined">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-card style="height: 884px;overflow: auto">
            <h4>题目描述</h4>
            <pre class="problem-content">{{ problem.description }}</pre>
            <h4>输入说明</h4>
            <pre class="problem-content">{{ problem.input }}</pre>
            <h4>输出说明</h4>
            <pre class="problem-content">{{ problem.output }}</pre>
            <el-divider></el-divider>
            <h4>限时</h4>
            <pre class="problem-content">{{ problem.timeout }}ms</pre>
            <h4>输入示例</h4>
            <pre class="sample">{{ problem.sampleInput }}</pre>
            <h4>输出示例</h4>
            <pre class="sample">{{ problem.sampleOutput }}</pre>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <el-form :inline="true">
              <el-form-item label="代码高亮">
                <el-select v-model="cmOptions.theme">
                  <el-option v-for="theme in codeStyle"
                             :key="theme.id"
                             :label="theme.name"
                             :value="theme.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-row>
                <el-form-item label="编程语言">
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
                <el-form-item>
                  <el-button type="success" icon="el-icon-s-promotion"
                             :disabled="disableCommit"
                             @click="commitCode">提交运行
                  </el-button>
                </el-form-item>
              </el-row>
            </el-form>
            <codemirror v-model="code" :options="cmOptions">
            </codemirror>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <el-dialog title="获取判题结果"
               :visible.sync="resultDialog.visible"
               :close-on-click-modal="false"
               :close-on-press-escape="false">
      <el-steps simple :active="resultDialog.active" finish-status="success">
        <el-step title="写入代码"></el-step>
        <el-step title="等待判题"></el-step>
        <el-step title="获取结果"></el-step>
      </el-steps>
      <el-alert v-if="result.title !== undefined" style="margin-top: 15px"
                show-icon :closable="false" :type="result.type"
                :title="result.title"
                :description="result.desc">
      </el-alert>
      <el-button style="margin-top: 25px" icon="el-icon-refresh"
                 :disabled="resultDialog.disableRefresh"
                 @click="getResult(solutionId, 1)">
        <span>重试</span>
      </el-button>
    </el-dialog>
  </el-container>
</template>

<script>
import Error from "@/components/Error"
import {apiPath, toLoginPage, searchParams, userInfo} from "@/script/util";
import {codemirror} from 'vue-codemirror'
import 'codemirror/mode/clike/clike.js'
import 'codemirror/mode/python/python.js'
import 'codemirror/mode/shell/shell.js'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/monokai.css'
import 'codemirror/theme/material.css'
import 'codemirror/theme/material-darker.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/edit/closebrackets.js'

const languageMode = [
  'text/x-csrc',
  'text/x-c++src',
  'text/x-java',
  'text/x-python',
  'text/x-sh',
  'text/x-csharp',
  'text/javascript',
  'text/x-kotlin'
]

const languageOptions = [
  {id: 0, name: 'C', version: 'gcc'},
  {id: 1, name: 'C++', version: 'g++(std=14)'},
  {id: 2, name: 'Java', version: '1.8'},
  {id: 3, name: 'Python', version: '3.5'},
  {id: 4, name: 'Bash'},
  {id: 5, name: 'C#', version: 'Mono'},
  {id: 6, name: 'JavaScript', version: 'Node v14'},
  {id: 7, name: 'Kotlin', version: '1.4.10'}
]

const ACCEPT = 2, IN_QUEUE = 1, JUDGED = 0

export default {
  name: "CodeCommit",
  components: {
    codemirror,
    Error
  },
  mounted() {
    this.getLanguages()
    this.getProblem()
    this.getCachedCode()
  },
  computed: {
    disableCommit: vm => {
      return vm.code.trim().length === 0
    }
  },
  data() {
    return {
      error: {
        code: undefined,
        text: ''
      },
      problemId: searchParams().problemId,
      contestId: searchParams().contestId,
      code: '',
      codeStyle: [
        {id: 'monokai', name: 'Monokai'},
        {id: 'material', name: 'Material'},
        {id: 'material-darker', name: 'Material Darker'},
        {id: 'dracula', name: 'Dracula'},
      ],
      cmOptions: {
        mode: 'text/x-csrc',
        theme: 'monokai',
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
      solutionId: '',
      result: {},
      resultDialog: {
        disableRefresh: true,
        visible: false,
        active: undefined
      }
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    getCachedCode() {
      let code = window.sessionStorage.getItem('code');
      if (code != null) {
        code = JSON.parse(code)
        this.code = code['code']
        this.language = code['language']
        this.languageChange(this.language)
        this.$forceUpdate()
        window.sessionStorage.removeItem('code')
      }
    },
    getLanguages() {
      // 如果是竞赛/作业的题目，先获取允许的语言
      if (this.contestId !== undefined) {
        this.$axios({
          url: `${apiPath.contest}/lang/${this.contestId}`,
          method: 'get'
        }).then((res) => {
          let languages = res.data.languages
          // 计算可用的语言
          languageOptions.forEach((value, index) => {
            let t = 1 << index
            if ((languages & t) === t)
              this.enabledLanguages.push(value)
          })
          this.language = this.enabledLanguages[0].id
        }).catch((error) => {
          console.log(error)
        })
      } else {
        this.enabledLanguages = languageOptions
      }
    },
    getProblem() {
      let url = apiPath.problem
      let headers = {}, params = {}
      if (userInfo() != null && userInfo()['roleId'] >= 2) {
        url = `${apiPath.problem}/pro`
        params.userId = userInfo().userId
        headers.token = userInfo().token
      }
      if (this.contestId !== undefined) {
        params.contestId = this.contestId
      }
      this.$axios({
        url: `${url}/${this.problemId}`,
        method: 'get',
        headers: headers,
        params: params
      }).then((res) => {
        if (res.status === 200) {
          this.problem = res.data
          document.title = `${this.problem.title} · Cloud OJ`
        } else if (res.status === 204) {
          this.error = {
            code: 404,
            text: '题目不存在'
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
          this.$notify.error({
            offset: 50,
            title: '获取题目失败',
            message: `${res.status} ${errorText}`
          })
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
        alert('请先登录！')
        return
      }
      this.resultDialog.active = undefined
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
        method: 'post',
        headers: {
          'token': userInfo().token,
          'Content-Type': 'application/json'
        },
        params: {
          userId: userInfo().userId
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
          this.$notify.error({
            offset: 50,
            title: '提交失败',
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
      this.$axios({
        url: apiPath.commit,
        method: 'get',
        headers: {
          'token': userInfo().token
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
            type: 'info',
            title: '未获取到结果',
            desc: '可能提交人数过多，可手动刷新'
          }
          this.resultDialog.disableRefresh = false
        } else {
          switch (res.data['state']) {
            case ACCEPT:
              this.resultDialog.active = 1
              setTimeout(() => {
                this.getResult(solutionId, count + 1)
              }, 1000)
              break
            case IN_QUEUE:
              this.resultDialog.active = 2
              setTimeout(() => {
                this.getResult(solutionId, count + 1)
              }, 1000)
              break
            case JUDGED:
              this.resultDialog.active = 3
              this.getResultText(res.data)
              this.resultDialog.disableRefresh = true
          }
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          this.$notify.error({
            offset: 50,
            title: '无法获取结果',
            message: `${res.status} ${res.statusText}`
          })
          this.resultDialog.disableRefresh = false
        }
      })
    },
    /**
     * 根据结果显示提示信息
     * @param data JudgeResult对象
     */
    getResultText(data) {
      switch (data.result) {
        case 0:
          this.result = {
            type: 'success',
            title: '完全正确',
            desc: '已通过全部测试点'
          }
          break
        case 1:
          this.result = {
            type: 'warning',
            title: `时间超限(${data["passRate"] * 100})`,
            desc: '时间复杂度有待优化'
          }
          break
        case 2:
          this.result = {
            type: 'warning',
            title: `内存超限(${data["passRate"] * 100})`,
            desc: '空间复杂度有待优化'
          }
          break
        case 3:
          this.result = {
            type: 'warning',
            title: `部分通过(${data["passRate"] * 100})`,
            desc: '可能漏掉了部分情况'
          }
          break
        case 4:
          this.result = {
            type: 'error',
            title: '答案错误',
            desc: '继续努力'
          }
          break
        case 5:
          this.result = {
            type: 'info',
            title: '编译错误',
            desc: '请仔细检查代码'
          }
          break
        case 6:
          this.result = {
            type: 'info',
            title: '运行错误',
            desc: '对于解释型语言，请检查是否存在语法错误'
          }
          break
        case 7:
          this.result = {
            type: 'error',
            title: '判题异常',
            desc: '判题服务器出现了异常或者你提交了恶意代码'
          }
      }
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 25px;
  padding: 0 20px;
  flex-direction: column;
  min-width: 1200px !important;
}
</style>