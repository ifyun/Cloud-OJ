const languages = {
    0: {name: "C", icon: "/icons/lang/c.svg"},
    1: {name: "C++", icon: "/icons/lang/cpp.svg"},
    2: {name: "Java", icon: "/icons/lang/java.svg"},
    3: {name: "Python", icon: "/icons/lang/python.svg"},
    4: {name: "Bash Shell", icon: "/icons/lang/bash.svg"},
    5: {name: "C#", icon: "/icons/lang/csharp.svg"},
    6: {name: "JavaScript", icon: "/icons/lang/js.svg"},
    7: {name: "Kotlin", icon: "/icons/lang/kotlin.svg"},
    8: {name: "Go", icon: "/icons/lang/go.svg"}
}

const sqlTypes = {
    0: {name: "SQLite", icon: "/icons/lang/sqlite.svg"}
}

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
    sqlTypes,
    stateTags,
    resultTags
}