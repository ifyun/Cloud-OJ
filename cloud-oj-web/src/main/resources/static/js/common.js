let roles = ['普通用户', '用户管理员', '题目管理员', 'ROOT'];
let result = ['完全正确', '时间超限', '部分通过', '答案错误', '编译错误']
let resultColor = ['bg-green', 'bg-yellow', 'bg-yellow', 'bg-red', 'bg-grey'];
let language = ['C', 'C++', 'Java', 'Python', 'Bash', 'C#'];

let langColorMap = new Map([
    [0, 'cpp-color'],
    [1, 'cpp-color'],
    [2, 'java-color'],
    [3, 'py-color'],
    [4, 'shell-color'],
    [5, 'csharp-color']
]);

let roleColor = ['user-color', 'user-manager-color', 'problem-manager-color', 'root-color'];

let errorText = new Map([
    [400, '错误请求.'],
    [401, '你的登录可能已过期.'],
    [403, '没有权限.'],
    [404, '请求的URL不存在.'],
    [410, '目标不存在'],
    [500, '服务器错误.'],
    [502, '网关无法转发请求，请稍后再试.'],
    [504, '网关超时，请稍后再试.']
]);

let color = 0;
let colors = ['tag-color-1', 'tag-color-2', 'tag-color-3', 'tag-color-4', 'tag-color-5', 'tag-color-6', 'tag-color-7'];
let colorMap = new Map();

let poster = "TALK IS CHEAP, SHOW ME YOUR CODE!";
let poster_login = "THINK TWICE, CODE ONCE!";

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