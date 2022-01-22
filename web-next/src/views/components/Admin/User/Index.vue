<template>
  <div class="user-admin">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space>
          <n-input-group>
            <n-select v-model:value="searchType" :options="searchTypes" style="width: 150px"/>
            <n-input clearable show-count maxlength="15" v-model:value="keyword"/>
            <n-button type="primary" @click="search">
              <template #icon>
                <n-icon>
                  <search-icon/>
                </n-icon>
              </template>
              搜索
            </n-button>
          </n-input-group>
        </n-space>
        <n-data-table :columns="columns" :data="users.data" :loading="pagination.loading"/>
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                      :item-count="users.count" @update:page="pageChange">
          <template #prefix="{itemCount}">
            共 {{ itemCount }} 项
          </template>
        </n-pagination>
      </n-space>
    </n-card>
  </div>
</template>

<script lang="tsx">
import {useStore} from "vuex"
import {useRouter} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import {CalendarCheck as DateIcon, UserShield as RoleIcon, UserTag as UserIcon} from "@vicons/fa"
import {PersonSearchRound as SearchIcon} from "@vicons/material"
import UserAvatar from "@/components/UserAvatar.vue"
import {ErrorMsg, PagedData, User, UserInfo} from "@/api/type"
import {UserApi} from "@/api/request"
import {setTitle} from "@/utils"
import moment from "moment"
import Mutations from "@/store/mutations";

const roles = [
  {text: "用户", type: "info"},
  {text: "题目管理员", type: "warning"},
  {text: "用户管理员", type: "warning"},
  {text: "ROOT", type: "error"}
]

@Options({
  name: "UserAdmin",
  components: {
    NCard,
    NSpace,
    NBreadcrumb,
    NBreadcrumbItem,
    NIcon,
    NInputGroup,
    NSelect,
    NInput,
    NButton,
    NDataTable,
    NPagination,
    SearchIcon,
    UserAvatar
  }
})
export default class UserAdmin extends Vue {
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private searchTypes = [
    {label: "用户 ID", value: 1},
    {label: "用户名", value: 2}
  ]

  private searchType: number = 1
  private keyword: string = ""

  private users: PagedData<User> = {
    data: [],
    count: 0
  }

  private pagination = {
    page: 1,
    pageSize: 15,
    loading: true
  }

  private columns = [
    {
      title: "ID",
      key: "userId",
      width: 150
    },
    {
      title: () => (
          <NSpace size="small" align="center">
            <NIcon style="display: flex">
              <UserIcon/>
            </NIcon>
            <span>用户名</span>
          </NSpace>
      ),
      render: (row: User) => (
          <NSpace align="center">
            <UserAvatar size="small" userId={row.userId}/>
            <NButton text={true}><b>{row.name}</b></NButton>
          </NSpace>
      )
    },
    {
      title: () => (
          <NSpace size="small" justify="center" align="center">
            <NIcon style="display: flex">
              <RoleIcon/>
            </NIcon>
            <span>权限</span>
          </NSpace>
      ),
      render: (row: User) => (
          <NTag size="small" type={roles[row.roleId!].type as any}>{roles[row.roleId!].text}</NTag>
      ),
      align: "center"
    },
    {
      title: () => (
          <NSpace size="small" justify="end" align="center">
            <NIcon style="display: flex">
              <DateIcon/>
            </NIcon>
            <span>注册时间</span>
          </NSpace>
      ),
      align: "right",
      render: (row: User) => (<span>{moment(row.createAt).format("YYYY-MM-DD")}</span>)
    }
  ]

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  get searchParams(): any {
    const keyword = this.keyword.trim()
    if (keyword.length === 0) {
      return {}
    }
    return this.searchType === 1 ? {userId: keyword} : {name: keyword}
  }

  beforeMount() {
    setTitle("用户管理")
    this.store.commit(Mutations.SET_BREADCRUMB,
        <NBreadcrumb>
          <NBreadcrumbItem>用户管理</NBreadcrumbItem>
        </NBreadcrumb>
    )
    const query = this.$route.query
    console.debug(query)
    if ("page" in query) {
      this.pagination.page = Number(query.page)
    }
    if ("keyword" in query) {
      this.keyword = String(query.keyword)
    }
    if ("searchType" in query) {
      const val = Number(query.searchType)
      this.searchType = val in [1, 2] ? val : 1
    }
    this.queryUsers()
  }

  search() {
    this.pagination.page = 1
    this.pageChange(this.pagination.page)
  }

  pageChange(page: number) {
    const keyword = this.keyword.trim()
    const query: any = {page}
    if (keyword.length > 0) {
      query.keyword = keyword
      query.searchType = this.searchType
    }
    this.router.push({
      query
    })
  }

  queryUsers() {
    UserApi.getAll(
        this.pagination.page,
        this.pagination.pageSize,
        this.searchParams,
        this.userInfo
    ).then((data) => {
      this.users = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.pagination.loading = false
    })
  }
}
</script>

<style scoped>
.user-admin {
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
}
</style>
