import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const Login = () => import('@/page/Login')
const Problems = () => import('@/page/Problems')
const Contest = () => import('@/page/Contest')
const History = () => import('@/page/CommitHistory')
const Ranking = () => import('@/page/Ranking')
const Manage = () => import('@/page/Manage')
const Commit = () => import('@/page/Commit')

const routes = [
    {path: '/login', component: Login},
    {path: '/', redirect: 'problems'},
    {path: '/problems', component: Problems},
    {path: '/contest', component: Contest},
    {path: '/history', component: History},
    {path: '/ranking', component: Ranking},
    {path: '/manage', component: Manage},
    {path: '/commit', component: Commit}
]

const router = new VueRouter({
    routes,
    mode: 'history'
})

export default router