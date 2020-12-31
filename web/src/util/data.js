const languages = ["C", "C++", "Java", "Python", "Bash", "C#", "JavaScript", "Kotlin"]

const stateTags = [
    {text: "等待判题", type: "primary", icon: "el-icon-loading"},
    {text: "等待写入", type: "primary", icon: "el-icon-loading"}
]

const resultTags = [
    {text: "完全正确", type: "success", icon: "el-icon-success"},
    {text: "时间超限", type: "warning", icon: "el-icon-warning"},
    {text: "内存超限", type: "warning", icon: "el-icon-warning"},
    {text: "部分通过", type: "warning", icon: "el-icon-warning"},
    {text: "答案错误", type: "danger", icon: "el-icon-error"},
    {text: "编译错误", type: "info", icon: "el-icon-info"},
    {text: "运行错误", type: "info", icon: "el-icon-info"},
    {text: "判题异常", type: "primary", icon: "el-icon-error"}
]

export {
    languages,
    stateTags,
    resultTags
}