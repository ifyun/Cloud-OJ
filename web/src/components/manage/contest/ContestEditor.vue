<template>
  <div>
    <el-form label-width="100px"
             ref="edit-contest"
             :rules="contestRules"
             :model="contest"
             :status-icon="true">
      <el-form-item label="名称" prop="contestName">
        <el-input v-model="contest.contestName">
        </el-input>
      </el-form-item>
      <el-form-item label="开始时间" prop="startAt">
        <el-date-picker type="datetime" v-model="contest.startAt">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endAt">
        <el-date-picker type="datetime" v-model="contest.endAt">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="语言限制" prop="languages">
        <el-checkbox-group v-model="enabledLanguages">
          <el-checkbox name="language"
                       v-for="lang in languages"
                       :label="lang.id"
                       :key="lang.id">{{ lang.name }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-check"
                   @click="onSave(saveType)">
          保存
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import moment from "moment"
import {userInfo, toLoginPage, Notice} from "@/script/util"
import {apiPath} from "@/script/env"

const MAX_LANG_ID = 7

const languageOptions = [
  {id: 0, name: "C"},
  {id: 1, name: "C++"},
  {id: 2, name: "Java"},
  {id: 3, name: "Python"},
  {id: 4, name: "Bash Shell"},
  {id: 5, name: "C#"},
  {id: 6, name: "JavaScript"},
  {id: 7, name: "Kotlin"}
]

export default {
  name: "ContestEditor",
  props: {
    dialogVisible: Boolean,
    contest: Object,
    saveType: String
  },
  watch: {
    contest: {
      immediate: true,
      handler() {
        this.listLanguages()
      }
    }
  },
  data() {
    let validateEndTime = (rule, value, callback) => {
      if (new Date(value) <= new Date(this.contest.startAt))
        return callback(new Error("结束时间必须大于开始时间"))
      callback()
    }
    let validateLanguages = (rule, value, callback) => {
      if (this.enabledLanguages.length === 0)
        return callback(new Error("请至少选择一种语言"))
      callback()
    }
    return {
      contestRules: {
        contestName: [
          {required: true, message: "请输入竞赛/作业名称", trigger: "blur"},
          {min: 4, max: 20, message: "长度在 4 ~ 20 个字符", trigger: "blur"}
        ],
        startAt: [
          {required: true, message: "请选择开始时间", trigger: "blur"}
        ],
        endAt: [
          {required: true, message: "请选择结束时间", trigger: "blur"},
          {validator: validateEndTime, trigger: "blur"}
        ],
        languages: [
          {required: true, validator: validateLanguages, trigger: "change"}
        ]
      },
      enabledLanguages: [],
      languages: languageOptions
    }
  },
  methods: {
    listLanguages() {
      this.enabledLanguages = []
      let formLoaded = this.$refs["edit-contest"] !== undefined
      if (formLoaded)
        this.$refs["edit-contest"].clearValidate()
      if (Object.keys(this.contest).length > 0) {
        let lang = this.contest.languages
        // 列出语言
        for (let i = 0; i <= MAX_LANG_ID; i++) {
          let t = 1 << i
          if ((lang & t) === t) {
            this.enabledLanguages.push(i)
          }
        }
      }
    },
    onSave(type) {
      this.$refs["edit-contest"].validate((valid) => {
        if (valid) {
          let languages = 0
          // 计算允许的语言
          this.enabledLanguages.forEach((v) => {
            languages = languages | 1 << v
          })

          let contest = this.contest
          contest.languages = languages
          contest.startAt = moment(contest.startAt).format("YYYY-MM-DD HH:mm:ss")
          contest.endAt = moment(contest.endAt).format("YYYY-MM-DD HH:mm:ss")

          this.$axios({
            url: apiPath.contestManage,
            method: type,
            headers: {
              "Content-Type": "application/json",
              "token": userInfo().token,
              "userId": userInfo().userId
            },
            data: JSON.stringify(contest)
          }).then((res) => {
            Notice.notify.success(this, {
              title: `【${contest.contestName}】已保存`,
              message: `${res.status} ${res.statusText}`
            })
            this.$emit("update:dialogVisible", false)
            this.$emit("refresh")
          }).catch((error) => {
            let res = error.response
            if (res.status === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: `【${contest.contestName}】保存失败`,
                message: `${res.status} ${res.statusText}`
              })
            }
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>

</style>