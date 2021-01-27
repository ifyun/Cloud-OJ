import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter)
const Page404 = () => import("@/page/404")
const Login = () => import("@/page/Login")
const Problems = () => import("@/page/Problems")
const Contest = () => import("@/page/Contest")
const Profile = () => import("@/page/Profile")
const Ranking = () => import("@/page/Ranking")
const Manage = () => import("@/page/Manage")
const Commit = () => import("@/page/Commit")
const Help = () => import("@/page/Help")

const routes = [
    {path: "*", component: Page404},
    {path: "/page_not_found", component: Page404},
    {path: "/login", component: Login},
    {path: "/", redirect: "problems"},
    {path: "/problems", component: Problems},
    {path: "/contest", component: Contest},
    {path: "/profile", component: Profile},
    {path: "/ranking", component: Ranking},
    {path: "/manage", component: Manage},
    {path: "/commit", component: Commit},
    {path: "/help", component: Help}
]

const router = new VueRouter({
    routes,
    mode: "history"
})

export default router