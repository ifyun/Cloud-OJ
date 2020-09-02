import qs from 'qs'

let tagColor = 1,
    colorMap = new Map(),
    tokenKey = 'cloud_oj_token'

function getTagColor(tag) {
    if (colorMap[tag] === undefined) {
        colorMap[tag] = `tag-color-${(++tagColor % 7) + 1}`
    }
    return colorMap[tag]
}

function getUserInfo() {
    return JSON.parse(sessionStorage.getItem(tokenKey))
}

function searchParams() {
    return qs.parse(location.search.replace('?', ''))
}

function handle401() {
    clearToken()
    window.location.href = '/login'
}

function clearToken() {
    sessionStorage.removeItem(tokenKey)
}

let apiPath = {
    problemManage: 'api/manager/problem/pro',
    contestManage: 'api/manager/contest/pro',
    problem: `api/manager/problem`,
    contest: 'api/manager/contest'
}

export {
    getTagColor,
    getUserInfo,
    searchParams,
    handle401,
    clearToken,
    apiPath
}