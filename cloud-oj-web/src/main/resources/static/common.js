let roles = ['普通用户', '用户管理员', '题目管理员', 'ROOT'];

let iconMap = new Map([
    ['C/C++', 'icon-cpp'],
    ['Java', 'icon-java'],
    ['Python', 'icon-python'],
    ['Shell', 'icon-shell'],
    ['C Sharp', 'icon-csharp']
]);

let langColorMap = new Map([
    ['C/C++', 'cpp-color'],
    ['Java', 'java-color'],
    ['Python', 'py-color'],
    ['Shell', 'shell-color'],
    ['C Sharp', 'csharp-color']
]);

let errorText = new Map([
    [400, '错误请求.'],
    [401, '你的登录可能已过期.'],
    [404, '请求的URL不存在.'],
    [500, '服务器错误.'],
    [502, '网关无法转发请求，请稍后再试.'],
    [504, '网关超时，请稍后再试.']
]);

let color = 0;
let colors = ['tag-color-1', 'tag-color-2', 'tag-color-3', 'tag-color-4', 'tag-color-5', 'tag-color-6', 'tag-color-7'];
let colorMap = new Map();

function getColor(tag) {
    if (colorMap[tag] === undefined)
        colorMap[tag] = colors[color++ % colors.length];
    return colorMap[tag];
}

function getQueryVariable(variable) {
    let query = window.location.search.substring(1);
    let vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        let pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return null;
}