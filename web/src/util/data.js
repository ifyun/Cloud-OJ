const languages = ["C", "C++", "Java", "Python", "Bash", "C#", "JavaScript", "Kotlin"]

const stateTags = {
    1: {text: "等待判题", type: "primary", icon: "el-icon-loading"},
    2: {text: "等待写入", type: "primary", icon: "el-icon-loading"}
}

const resultTags = {
    0: {text: "完全正确", type: "success", icon: "el-icon-success"},
    1: {text: "时间超限", type: "warning", icon: "el-icon-warning"},
    2: {text: "内存超限", type: "warning", icon: "el-icon-warning"},
    3: {text: "部分通过", type: "warning", icon: "el-icon-warning"},
    4: {text: "答案错误", type: "danger", icon: "el-icon-error"},
    5: {text: "编译错误", type: "info", icon: "el-icon-info"},
    6: {text: "运行错误", type: "info", icon: "el-icon-info"},
    7: {text: "内部错误", type: "primary", icon: "el-icon-error"},
    8: {text: "输出超限", type: "warning", icon: "el-icon-warning"}
}

export {
    languages,
    stateTags,
    resultTags
}