import qs from 'qs'

let colorIndex = 1,
    colorMap = new Map()
const TOKEN = 'token'

function tagColor(tag) {
    if (colorMap[tag] === undefined) {
        let i = colorIndex++ % 8
        colorMap[tag] = `tag-color-${i === 0 ? 1 : i}`
    }
    return colorMap[tag]
}

function userInfo() {
    return JSON.parse(localStorage.getItem(TOKEN))
}

function searchParams() {
    return qs.parse(location.search.replace('?', ''))
}

function toLoginPage() {
    clearToken()
    window.location.href = '/login'
}

function saveToken(value) {
    localStorage.setItem(TOKEN, value)
}

function clearToken() {
    localStorage.removeItem(TOKEN)
}

function copyObject(src) {
    return JSON.parse(JSON.stringify(src))
}

export {
    tagColor,
    userInfo,
    searchParams,
    toLoginPage,
    saveToken,
    clearToken,
    copyObject
}