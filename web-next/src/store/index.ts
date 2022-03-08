import {VNode} from "vue"
import {createStore} from "vuex"
import {darkTheme} from "naive-ui"
import {UserInfo} from "@/api/type"
import Mutations from "@/store/mutations"

const THEME = "theme"
const TOKEN = "userToken"

const theme = localStorage.getItem(THEME)
const token = localStorage.getItem(TOKEN)

const store = createStore({
    state: {
        theme: theme === "dark" ? darkTheme : null,
        userInfo: token == null ? null : JSON.parse(token) as UserInfo,
        showAuthDialog: false,
        reload: false,
        breadcrumb: null
    },
    mutations: {
        [Mutations.CHANGE_THEME](state: any, value: string) {
            if (value === "dark") {
                state.theme = darkTheme
                localStorage.setItem(THEME, "dark")
            } else {
                state.theme = null
                localStorage.removeItem(THEME)
            }
        },
        [Mutations.SAVE_TOKEN](state: any, userInfo: UserInfo) {
            localStorage.setItem(TOKEN, JSON.stringify(userInfo))
            state.userInfo = userInfo
        },
        [Mutations.CLEAR_TOKEN](state: any) {
            localStorage.removeItem(TOKEN)
            state.userInfo = null
        },
        [Mutations.SHOW_AUTH_DIALOG](state: any, value: boolean) {
            state.showAuthDialog = value
        },
        [Mutations.SET_RELOAD](state: any, value: boolean) {
            state.reload = value
        },
        [Mutations.SET_BREADCRUMB](state: any, value: VNode) {
            state.breadcrumb = value
        }
    },
    getters: {
        isLoggedIn: (state) => {
            return state.userInfo != null
        }
    }
})

export default store
export {
    Mutations
}
