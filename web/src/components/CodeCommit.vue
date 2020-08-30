<template>
  <el-container class="container">
    <el-page-header v-if="problem.problemId !== undefined"
                    :content="`${problemId}. ${problem.title}`"
                    @back="back"
                    style="align-self: flex-start">
    </el-page-header>
    <el-divider></el-divider>
    <div style="width: 100%" v-if="problem.problemId !== undefined">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-card style="height: 1002px;overflow: auto">
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
              <el-form-item label="编程语言">
                <el-select v-model="language" placeholder="请选择语言"
                           @change="languageChange">
                  <el-option v-for="lang in enabledLanguages"
                             :key="lang.name"
                             :label="lang.name"
                             :value="lang.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="success" icon="el-icon-s-promotion">提交运行</el-button>
              </el-form-item>
            </el-form>
            <codemirror v-model="code" :options="cmOptions"></codemirror>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </el-container>
</template>

<script>
import {searchParams} from "@/js/util";
import {codemirror} from 'vue-codemirror'
import 'codemirror/mode/clike/clike.js'
import 'codemirror/mode/python/python.js'
import 'codemirror/mode/shell/shell.js'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/monokai.css'
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
  {id: 0, name: 'C (gcc)'},
  {id: 1, name: 'C++ (g++ std=14)'},
  {id: 2, name: 'Java (1.8)'},
  {id: 3, name: 'Python (3.5)'},
  {id: 4, name: 'Bash Shell'},
  {id: 5, name: 'C# (Mono)'}
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
  data() {
    return {
      problemId: searchParams().problemId,
      contestId: searchParams().contestId,
      code: '',
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
      language: 0
    }
  },
  methods: {
    /**
     * 如果是来着竞赛/作业的题目，先获取允许的语言
     */
    getLanguages() {
      if (this.contestId !== undefined) {
        this.$axios({
          url: `${this.apiUrl}api/manager/contest/lang/${this.contestId}`,
          method: 'get'
        }).then((res) => {
          let languages = res.data.languages
          // 设置可用的语言
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
      let url = `${this.apiUrl}api/manager/problem/`
          + `${this.problemId}`
      if (this.contestId !== undefined) url += `?contestId=${this.contestId}`
      this.$axios({
        url: url,
        method: 'get'
      }).then((res) => {
        if (res.status === 200) {
          this.problem = res.data
          document.title = `${this.problem.title} · Cloud OJ`
        } else if (res.status === 204) {
          console.log('题目不存在')
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    languageChange(value) {
      this.cmOptions.mode = languageMode[value]
    },
    commitCode() {
      // TODO 提交代码
    },
    back() {
      window.history.back()
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 20px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>