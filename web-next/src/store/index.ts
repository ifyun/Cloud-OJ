import {createStore} from "vuex"
import {darkTheme} from "naive-ui"
import {UserInfo} from "@/api/type"
import MutationType from "@/store/mutation-type"

const THEME = "theme"
const TOKEN = "userToken"

const theme = localStorage.getItem(THEME)
const token = localStorage.getItem(TOKEN)

const store = createStore({
    state: {
        theme: theme === "dark" ? darkTheme : null,
        userInfo: token == null ? null : JSON.parse(token) as UserInfo,
        showAuthDialog: false
    },
    mutations: {
        [MutationType.CHANGE_THEME](state: any, value: string) {
            if (value === "dark") {
                state.theme = darkTheme
                localStorage.setItem(THEME, "dark")
            } else {
                state.theme = null
                localStorage.removeItem(THEME)
            }
        },
        [MutationType.SAVE_TOKEN](state: any, userInfo: UserInfo) {
            localStorage.setItem(TOKEN, JSON.stringify(userInfo))
            state.userInfo = userInfo
        },
        [MutationType.CLEAR_TOKEN](state: any) {
            localStorage.removeItem(TOKEN)
            state.userInfo = null
        },
        [MutationType.SHOW_AUTH_DIALOG](state: any, value: boolean) {
            state.showAuthDialog = value
        }
    },
    getters: {
        isLoggedIn: (state) => {
            return state.userInfo != null
        }
    }
})

export default store