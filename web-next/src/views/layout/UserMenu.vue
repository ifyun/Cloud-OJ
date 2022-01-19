<template>
  <n-space v-if="isLoggedIn" align="center" size="small">
    <n-avatar class="avatar" round :src="avatar" @error="avatar = undefined"/>
    <n-dropdown trigger="click" :show-arrow="true" placement="bottom-end" @select="userMenuSelect"
                :options="userInfo.roleId > 0 ? adminMenuOptions: userMenuOptions">
      <n-button text icon-placement="right" style="padding: 6px 0">
        {{ userInfo.name }}
        <template #icon>
          <n-icon>
            <drop-down-icon/>
          </n-icon>
        </template>
      </n-button>
    </n-dropdown>
  </n-space>
  <n-space v-else size="small">
    <n-button secondary type="primary" size="small" @click="login">
      注册登录
    </n-button>
  </n-space>
</template>

<script lang="tsx">
import {useStore} from "vuex"
import {RouterLink} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {NAvatar, NButton, NDropdown, NIcon, NSpace, useDialog, useMessage} from "naive-ui"
import {
  ArrowDropDownFilled as DropDownIcon,
  DashboardCustomizeRound as DashboardIcon,
  ExitToAppOutlined as LogoutIcon,
  LogInFilled as LoginIcon
} from "@vicons/material"
import {HouseUser as UserHomeIcon} from "@vicons/fa"
import {renderIcon} from "@/utils"
import {AuthApi} from "@/api/request"
import {ErrorMsg, UserInfo} from "@/api/type"
import MutationType from "@/store/mutation-type"

@Options({
  name: "UserMenu",
  components: {
    NSpace,
    NButton,
    NDropdown,
    NAvatar,
    NIcon,
    DropDownIcon,
    UserHomeIcon,
    DashboardIcon,
    LogoutIcon,
    LoginIcon
  }
})
export default class UserMenu extends Vue {
  private store = useStore()
  private message = useMessage()
  private dialog = useDialog()
  private avatar: string = ""

  private userMenuOptions = [
    {
      label: "个人中心",
      key: "account",
      icon: renderIcon(UserHomeIcon)
    },
    {
      label: "退出",
      key: "exit",
      icon: renderIcon(LogoutIcon)
    }
  ]

  private adminMenuOptions = [
    {
      label: () => <RouterLink to={{name: "account"}}>个人中心</RouterLink>,
      key: "account",
      icon: renderIcon(UserHomeIcon)
    },
    {
      label: () => <RouterLink to={{name: "problem_admin"}}>系统管理</RouterLink>,
      key: "admin",
      icon: renderIcon(DashboardIcon)
    },
    {
      label: "退出登录",
      key: "exit",
      icon: renderIcon(LogoutIcon)
    }
  ]

  get isLoggedIn(): boolean {
    return this.store.getters.isLoggedIn
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  userMenuSelect(key: string) {
    if (key === "exit") {
      this.exit()
    }
  }

  beforeMount() {
    if (this.store.state.userInfo != null) {
      this.avatar = `/api/file/image/avatar/${this.userInfo.userId}.png`
    }
  }

  login(event: any) {
    this.store.commit(MutationType.SHOW_AUTH_DIALOG, true)
    event.target.blur()
    event.target.parentNode.blur()
  }

  logoff() {
    AuthApi.logoff(
        this.userInfo
    ).then(() => {
      this.message.success(`${this.userInfo.name} 已退出`)
    }).catch((error: ErrorMsg) => {
      this.message.warning(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.store.commit(MutationType.CLEAR_TOKEN)
    })
  }

  exit() {
    this.dialog.warning({
      title: "警告",
      content: "确定退出吗？",
      positiveText: "确定",
      negativeText: "不要",
      onPositiveClick: () => {
        this.logoff()
      }
    })
  }
}
</script>

<style scoped>
.avatar {
  vertical-align: middle;
}
</style>