let roles = ['普通用户', '用户管理员', '题目管理员', 'ROOT'];

let iconMap = new Map([
    ['C/C++', 'icon-cpp'],
    ['Java', 'icon-java'],
    ['Python', 'icon-python'],
    ['Shell', 'icon-shell']
]);

let langColorMap = new Map([
    ['C/C++', 'cpp-color'],
    ['Java', 'java-color'],
    ['Python', 'py-color'],
    ['Shell', 'shell-color']
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