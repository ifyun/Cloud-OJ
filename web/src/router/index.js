import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter)
const Page404 = () => import("@/page/404")
const Login = () => import("@/page/Login")
const Problems = () => import("@/page/Problems")
const Contest = () => import("@/page/Contest")
const Profile = () => import("@/page/Profile")
const Ranking = () => import("@/page/Ranking")
const Commit = () => import("@/page/Commit")
const Help = () => import("@/page/Help")
const Manage = () => import("@/page/Manage")
const ManageProblems = () => import("@/components/manage/problem/Index")
const ManageContest = () => import("@/components/manage/contest/Index")
const ManageUser = () => import("@/components/manage/user/Index")
const Settings = () => import("@/components/manage/Settings")

const routes = [
    {path: "/page_not_found", component: Page404},
    {path: "/login", component: Login},
    {path: "/", redirect: "problems"},
    {path: "/problems", component: Problems},
    {path: "/contest", component: Contest},
    {path: "/profile", component: Profile},
    {path: "/ranking", component: Ranking},
    {path: "/commit", component: Commit},
    {path: "/help", component: Help},
    {
        path: "/manage",
        component: Manage,
        children: [
            {
                path: "problems",
                component: ManageProblems
            },
            {
                path: "contest",
                component: ManageContest
            },
            {
                path: "user",
                component: ManageUser
            },
            {
                path: "settings",
                component: Settings
            },
            {
                path: "",
                redirect: "problems"
            }
        ]
    },
    {path: "*", component: Page404},
]

const router = new VueRouter({
    routes,
    mode: "history"
})

export default router