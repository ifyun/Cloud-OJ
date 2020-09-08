<template>
  <el-container class="container">
    <el-page-header v-if="problem.problemId !== undefined"
                    :content="`${problemId}. ${problem.title}`"
                    @back="back">
    </el-page-header>
    <el-divider></el-divider>
    <el-row style="margin-bottom: 20px" :gutter="10"
            v-if="alertData.title !== undefined">
      <el-col :span="12">
        <el-row :gutter="10" type="flex" align="middle">
          <el-col :span="23">
            <el-alert effect="dark"
                      show-icon
                      :closable="false"
                      :type="alertData.type"
                      :title="alertData.title"
                      :description="alertData.desc">
            </el-alert>
          </el-col>
          <el-col :span="1">
            <el-button icon="el-icon-refresh" type="text"
                       :disabled="disableRefresh"
                       @click="getResult(solutionId, 1)">
            </el-button>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <div style="width: 100%" v-if="problem.problemId !== undefined">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-card style="height: 964px;overflow: auto">
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
              <el-form-item label="主题">
                <el-select v-model="cmOptions.theme">
                  <el-option v-for="theme in codeStyle"
                             :key="theme.id"
                             :label="theme.name"
                             :value="theme.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-row>
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
  </el-container>
</template>

<script>
import {apiPath, handle401, searchParams, userInfo} from "@/js/util";
import {codemirror} from 'vue-codemirror'
import 'codemirror/mode/clike/clike.js'
import 'codemirror/mode/python/python.js'
import 'codemirror/mode/shell/shell.js'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/monokai.css'
import 'codemirror/theme/material-darker.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/theme/solarized.css'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/edit/closebrackets.js'

let languageMode = [
  'text/x-csrc',
  'text/x-c++src',
  'text/x-java',
  'text/x-python',
  'text/x-sh',
  'text/x-csharp'
]

let languageOptions = [
  {id: 0, name: 'C', version: 'gcc'},
  {id: 1, name: 'C++', version: 'g++(std=14)'},
  {id: 2, name: 'Java', version: '1.8'},
  {id: 3, name: 'Python', version: '3.5'},
  {id: 4, name: 'Bash Shell'},
  {id: 5, name: 'C#', version: 'Mono'}
]

export default {
  name: "CodeCommit",
  components: {
    codemirror
  },
  beforeMount() {
    this.getLanguages()
    this.getProblem()
    this.cmOptions.mode = languageMode[this.language]
  },
  computed: {
    disableCommit: vm => {
      return vm.code.trim().length === 0
    }
  },
  data() {
    return {
      problemId: searchParams().problemId,
      contestId: searchParams().contestId,
      code: '',
      codeStyle: [
        {id: 'monokai', name: 'Monokai'},
        {id: 'material-darker', name: 'Material Darker'},
        {id: 'dracula', name: 'Dracula'},
        {id: 'solarized', name: 'Solarized'}
      ],
      cmOptions: {
        mode: '',
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
      alertData: {},
      disableRefresh: true
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    getLanguages() {
      // 如果是来着竞赛/作业的题目，先获取允许的语言
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
      let url = `${apiPath.problem}/${this.problemId}`
      let params = {}
      if (this.contestId !== undefined) {
        params = {contestId: this.contestId}
      }
      this.$axios({
        url: url,
        method: 'get',
        params: params
      }).then((res) => {
        if (res.status === 200) {
          this.problem = res.data
          document.title = `${this.problem.title} · Cloud OJ`
        } else if (res.status === 204) {
          alert('题目不存在')
        }
      }).catch((error) => {
        console.log(error)
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
      this.alertData = {}
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
        this.alertData = {
          type: 'info',
          title: '已提交，正在等待结果',
          desc: '你的代码已被接受，请稍候...'
        }
        this.solutionId = res.data
        this.getResult(res.data, 1)
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: '提交失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      })
    },
    /**
     * 获取结果
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
        if (res.status === 204 && count <= 6) {
          setTimeout(() => {
            this.getResult(solutionId, count + 1)
          }, 1000)
        } else if (count > 6) {
          this.alertData = {
            type: 'info',
            title: '未获取到结果',
            desc: '可能提交人数过多，可手动刷新'
          }
          this.disableRefresh = false
        } else {
          this.getResultText(res.data)
          this.disableRefresh = true
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: '无法获取结果',
            message: `${res.status} ${res.statusText}`
          })
          this.disableRefresh = false
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
          this.alertData = {
            type: 'success',
            title: '完全正确',
            desc: '已通过全部测试点！'
          }
          break
        case 1:
          this.alertData = {
            type: 'error',
            title: '时间超限',
            desc: '时间复杂度有待优化！'
          }
          break
        case 2:
          this.alertData = {
            type: 'warning',
            title: `部分通过(${data.passRate * 100})`,
            desc: '漏掉了部分情况哦！'
          }
          break
        case 3:
          this.alertData = {
            type: 'error',
            title: '答案错误',
            desc: '继续努力！'
          }
          break
        case 4:
          this.alertData = {
            type: 'warning',
            title: '编译错误',
            desc: '请仔细检查代码！'
          }
      }
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 20px;
  padding: 0 20px;
  flex-direction: column;
}

.judge-result {
  margin-bottom: 15px;
  position: relative;
}

.refresh-button {
  position: absolute;
  right: 0;
}
</style>