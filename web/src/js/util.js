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

function userInfo() {
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

function copyObject(src) {
    return JSON.parse(JSON.stringify(src))
}

let apiPath = {
    problemManage: '/api/manager/problem/pro',
    contestManage: '/api/manager/contest/pro',
    userManage: '/api/manager/user/pro',
    testDataManage: '/api/file/test_data',
    user: '/api/manager/user',
    problem: `/api/manager/problem`,
    contest: '/api/manager/contest'
}

export {
    getTagColor,
    userInfo,
    searchParams,
    handle401,
    clearToken,
    copyObject,
    apiPath
}