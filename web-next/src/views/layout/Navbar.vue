<template>
  <div class="top-nav">
    <div class="app-name">
      <logo :size="40"/>
    </div>
    <div style="margin-left: 50px; font-weight: bold">
      <n-menu mode="horizontal" v-model:value="$route.name" :options="navMenuOptions">
      </n-menu>
    </div>
    <div style="margin-left: auto">
      <n-space size="large" align="center">
        <n-switch v-model:value="isDarkTheme">
          <template #checked>
            <n-icon color="#FCE100">
              <dark-icon/>
            </n-icon>
          </template>
          <template #unchecked>
            <n-icon>
              <dark-icon/>
            </n-icon>
          </template>
        </n-switch>
        <n-space v-if="isLoggedIn" align="center" size="small">
          <n-avatar class="avatar" round :src="avatar" @error="avatar = undefined"/>
          <n-dropdown :show-arrow="true" placement="bottom-end" @select="userMenuSelect"
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
      </n-space>
    </div>
  </div>
</template>

<script lang="ts">
import {h} from "vue"
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {RouterLink} from "vue-router"
import {NAvatar, NButton, NDropdown, NIcon, NMenu, NSpace, NSwitch, useDialog, useMessage} from "naive-ui"
import {HouseUser as UserHomeIcon} from "@vicons/fa"
import {
  ArrowDropDownFilled as DropDownIcon,
  DarkModeRound as DarkIcon,
  DashboardCustomizeRound as DashboardIcon,
  ExitToAppOutlined as LogoutIcon,
  LogInFilled as LoginIcon
} from "@vicons/material"
import Logo from "@/components/Logo.vue"
import {AuthApi} from "@/api/request"
import {ErrorMsg, UserInfo} from "@/api/type"
import {renderIcon} from "@/utils"
import MutationType from "@/store/mutation-type"

const renderLabel = (routerName: string, label: string) => {
  return () => h(RouterLink, {to: {name: routerName}}, {default: () => label})
}

@Options({
  components: {
    NSpace,
    NMenu,
    NDropdown,
    NButton,
    NAvatar,
    NIcon,
    NSwitch,
    LoginIcon,
    DarkIcon,
    UserHomeIcon,
    DashboardIcon,
    LogoutIcon,
    DropDownIcon,
    Logo
  }
})
export default class Navbar extends Vue {
  private store = useStore()
  private message = useMessage()
  private dialog = useDialog()
  private avatar: string = ""

  private navMenuOptions = [
    {
      label: renderLabel("problems", "练习"),
      key: "problems"
    },
    {
      label: renderLabel("contests", "竞赛 & 作业"),
      key: "contests"
    },
    {
      label: renderLabel("leaderboard", "排名"),
      key: "leaderboard"
    },
    {
      label: renderLabel("help", "帮助"),
      key: "help"
    }
  ]

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
      label: renderLabel("account", "个人中心"),
      key: "account",
      icon: renderIcon(UserHomeIcon)
    },
    {
      label: renderLabel("problem_admin", "系统管理"),
      key: "admin",
      icon: renderIcon(DashboardIcon)
    },
    {
      label: "退出登录",
      key: "exit",
      icon: renderIcon(LogoutIcon)
    }
  ]

  get isDarkTheme(): boolean {
    return this.store.state.theme != null
  }

  set isDarkTheme(value: boolean) {
    this.store.commit(MutationType.CHANGE_THEME, value ? "dark" : "light")
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  get isLoggedIn(): boolean {
    return this.store.getters.isLoggedIn
  }

  beforeMount() {
    if (this.store.state.userInfo != null) {
      this.avatar = `/api/file/image/avatar/${this.userInfo.userId}.png`
    }
  }

  userMenuSelect(key: string) {
    if (key === "exit") {
      this.exit()
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

<style scoped lang="scss">
.top-nav {
  height: 60px;
  padding: 0 25px;
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .app-name {
    display: flex;
    align-items: center;
    font-size: 22px;
    font-weight: 500;

    .logo {
      margin-right: 12px;
    }
  }

  .avatar {
    vertical-align: middle;
  }
}
</style>
