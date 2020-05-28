let table;

layui.use(['table', 'element'], () => {
    let element = layui.element;
    table = layui.table;
    element.init();
});

$(document).ready(() => {
    table.render({
        elem: '#results',
        url: baseUrl + '/api/manager/result',
        where: {
            userId: user.userId
        },
        headers: {token: user.token},
        parseData: (res) => {
            return {
                "code": 0,
                "count": res === undefined ? 0 : res.count,
                "data": res === undefined ? null : res.data
            }
        },
        skin: 'nob',
        size: 'lg',
        page: true,
        limit: 15,
        limits: [10, 15, 20, 25, 30],
        cols: [
            [
                {field: 'problemId', title: '题目编号', width: '10%', align: 'center'},
                {field: 'title', title: '题目名称', width: '25%', templet: '#titleTpl'},
                {title: '操作', width: '15%', align: 'center', toolbar: '#opt-toolbar'},
                {field: 'language', title: '编程语言', width: '15%', templet: '#languageTpl'},
                {field: 'result', title: '结果', width: '15%', align: 'center', templet: '#resultTpl'},
                {field: 'time', title: '运行时间', width: '10%', templet: '#timeTpl'},
                {field: 'submitTime', title: '提交时间', width: '10%'}
            ]
        ]
    });

    table.on('tool(results)', (obj) => {
        if (obj.event === 'see-code') {
            layer.open({
                type: 1,
                title: '代码',
                area: ['600px', '700px'],
                closeBtn: 2,
                content: '<textarea readonly="readonly" class="layui-textarea"' +
                    ' style="position: absolute; top: 0; bottom: 0;" id="code">'
                    + obj.data.code + '</textarea>'
            });
        }
    });
});

$('#nav-commit').addClass('layui-this');