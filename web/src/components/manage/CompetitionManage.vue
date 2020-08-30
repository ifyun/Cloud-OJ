<template>
  <div>
    <span>竞赛/作业管理</span>
    <el-divider></el-divider>
    <el-form>
      <el-form-item>
        <el-button type="primary" icon="el-icon-circle-plus"
                   @click="addContest">创建新竞赛/作业
        </el-button>
      </el-form-item>
    </el-form>
    <!-- Contest Table -->
    <el-table :data="contests.data" stripe border>
      <el-table-column label="ID" prop="contestId" width="100px" align="center">
      </el-table-column>
      <el-table-column label="名称" prop="contestName" width="260px">
      </el-table-column>
      <el-table-column label="开始时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.startAt }}</i>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.endAt }}</i>
        </template>
      </el-table-column>
      <!-- Operation -->
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-dropdown style="margin-right: 10px"
                       @command="onEditClick">
            <el-button size="mini"
                       @click="selectedIndex=scope.$index"
                       icon="el-icon-edit-outline">
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="0">编辑</el-dropdown-item>
              <el-dropdown-item command="1">题目管理</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="danger" icon="el-icon-delete"
                     @click="onDeleteClick(scope.$index)">
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[10, 25, 50]"
                   :page-size="pageSize"
                   :total="contests.count"
                   :current-page.sync="currentPage"
                   @size-change="onSizeChange"
                   @current-change="onCurrentPageChange">
    </el-pagination>
    <!-- Edit Contest Dialog -->
    <el-dialog :title="selectedContest.contestName"
               :visible.sync="showEditDialog">
      <el-form label-width="100px" :model="selectedContest">
        <el-form-item label="名称">
          <el-input v-model="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker type="datetime" v-model="selectedContest.startAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker type="datetime" v-model="selectedContest.endAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="语言限制">
          <el-checkbox-group v-model="enabledLanguages">
            <el-checkbox name="language"
                         v-for="lang in languageOptions"
                         :label="lang.id"
                         :key="lang.id">{{ lang.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary">保 存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- New Contest Dialog -->
    <el-dialog title="创建新竞赛/作业"
               :visible.sync="showAddDialog">
      <el-form label-width="100px" ref="new-contest"
               :model="newContest"
               :rules="newContestRules">
        <el-form-item label="名称" prop="contestName">
          <el-input v-model="newContest.contestName"
                    placeholder="竞赛/作业名称">
          </el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startAt">
          <el-date-picker type="datetime" v-model="newContest.startAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endAt">
          <el-date-picker type="datetime" v-model="newContest.endAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="语言限制" prop="languages">
          <el-checkbox-group v-model="enabledLanguages">
            <el-checkbox name="language"
                         v-for="lang in languageOptions"
                         :label="lang.id"
                         :key="lang.id">{{ lang.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary"
                     @click="onAddClick('new-contest')">创 建
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- Delete Confirm Dialog -->
    <el-dialog title="提示" :visible.sync="showDeleteDialog">
      <el-alert type="warning" show-icon
                :title="`你正在删除：${this.selectedContest.contestName}`"
                :closable="false">
      </el-alert>
      <el-form>
        <el-form-item label="输入名称确认删除">
          <el-input v-model="checkName"
                    :placeholder="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="onDelete">删除</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {apiPath, getUserInfo} from "@/js/util";

export default {
  name: "CompetitionManage",
  beforeMount() {
    this.getContests()
  },
  data() {
    let validateEndTime = (rule, value, callback) => {
      if (new Date(value) <= new Date(this.newContest.startAt))
        return callback(new Error('结束时间必须大于开始时间'))
    }
    let validateLanguages = (rule, value, callback) => {
      if (this.enabledLanguages.length === 0)
        return callback(new Error('请至少选择一种语言'))
    }
    return {
      userInfo: getUserInfo(),
      contests: {
        data: [],
        count: 0
      },
      selectedIndex: null,
      selectedContest: {
        contestId: null,
        contestName: '',
        startAt: '',
        endAt: '',
        languages: ''
      },
      newContest: {},
      newContestRules: {
        contestName: [
          {required: true, message: '请输入竞赛/作业名称', trigger: 'blur'},
          {min: 4, max: 20, message: '长度在 4 ~ 20 个字符', trigger: 'blur'}
        ],
        startAt: [
          {required: true, message: '请选择开始时间', trigger: 'blur'}
        ],
        endAt: [
          {required: true, message: '请选择结束时间', trigger: 'blur'},
          {validator: validateEndTime, trigger: 'blur'}
        ],
        languages: [
          {validator: validateLanguages, trigger: 'change'}
        ]
      },
      languageOptions: [
        {id: 0, name: 'C'},
        {id: 1, name: 'C++'},
        {id: 2, name: 'Java'},
        {id: 3, name: 'Python'},
        {id: 4, name: 'Bash Shell'},
        {id: 5, name: 'C#'}
      ],
      enabledLanguages: [],
      pageSize: 25,
      currentPage: 1,
      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      checkName: ''
    }
  },
  methods: {
    getContests() {
      this.$axios({
        url: `${this.apiUrl}${apiPath.contestManage}`
            + `?page=${this.currentPage}&limit=${this.pageSize}`
            + `&userId=${this.userInfo.userId}`,
        method: 'get',
        headers: {
          'token': this.userInfo.token
        }
      }).then((res) => {
        this.contests = res.data
      }).catch((error) => {
        console.log(error)
      })
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getContests()
    },
    onCurrentPageChange() {
      this.getContests()
    },
    onEditClick(command) {
      if (command === '0') {
        this.selectedContest = this.contests.data[this.selectedIndex]

        let lang = this.selectedContest.languages
        this.enabledLanguages = []

        for (let i = 0; i <= 5; i++) {
          let t = 1 << i
          if ((lang & t) === t) {
            this.enabledLanguages.push(i)
          }
        }

        this.showEditDialog = true
      } else if (command === '1') {
        // TODO 管理题目
      }
    },
    addContest() {
      this.newContest = {}
      this.enabledLanguages = []
      this.showAddDialog = true
    },
    onAddClick(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // TODO 添加
        } else {
          return false;
        }
      })
    },
    onDeleteClick(index) {
      this.showDeleteDialog = true
      this.selectedIndex = index
      this.selectedContest = this.contests.data[index]
    },
    onDelete() {
      if (this.checkName === this.selectedContest.contestName) {
        console.log("删除")
      }
    }
  }
}
</script>

<style scoped>
.el-checkbox + .el-checkbox,
.el-checkbox.is-bordered + .el-checkbox.is-bordered {
  margin-left: 0;
}
</style>