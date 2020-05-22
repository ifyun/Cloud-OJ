let table, form, laydate;
let languages = 0;

$(document).ready(() => {
    $('#nav-manager').addClass("layui-this");

    layui.use(['element', 'table', 'form', 'laydate'], () => {
        let element = layui.element;
        table = layui.table;
        form = layui.form;
        laydate = layui.laydate;

        element.init();
    });

    // 竞赛/作业列表
    table.render({
        elem: '#contests',
        url: '../api/manager/contest/pro',
        where: {
            userId: user.userId
        },
        headers: {token: user.token},
        parseData: (res) => {
            return {
                "code": 0,
                "count": res === undefined ? 0 : res.count,
                "data": res === undefined ? null : res.data,
            }
        },
        skin: 'nob',
        size: 'lg',
        page: true,
        limit: 15,
        limits: [10, 15, 20, 25, 30],
        defaultToolbar: ['print', 'exports'],
        toolbar: '#top-bar',
        cols: [
            [
                {field: 'contestId', title: '竞赛/作业编号', width: '15%', align: 'center'},
                {field: 'contestName', title: '竞赛/作业名称', width: '30%', edit: 'text'},
                {field: 'startAt', title: '开始时间', width: '15%', edit: 'text'},
                {field: 'endAt', title: '结束时间', width: '15%', edit: 'text'},
                {title: '操作', width: '25%', align: 'center', toolbar: '#optToolbar'}
            ]
        ]
    });

    table.on('edit(contests)', update);

    let createContestIndex, setLanguagesIndex;

    table.on('toolbar(contests)', (obj) => {
        if (obj.event === 'create-contest') {
            languages = 0;
            laydate.render({
                elem: '#startAt',
                type: 'datetime'
            });

            laydate.render({
                elem: '#endAt',
                type: 'datetime'
            });

            form.render();

            createContestIndex = layer.open({
                type: 1,
                title: '创建竞赛/作业',
                content: $('#contest-form'),
                area: ['500px', '370px'],
                resize: false,
                closeBtn: 2,
                cancel: () => {
                    $('#contest-form').hide();
                }
            });
        }
    });

    table.on('tool(contests)', (obj) => {
        if (obj.event === 'edit') {
            showContestProblems(obj.data.contestId, obj.data.contestName);
        }

        if (obj.event === 'del') {
            delContest(obj.data);
        }

        if (obj.event === 'set-languages') {
            languages = obj.data.languages;
            $('#edit-languages input[type=checkbox]').attr("checked", false);
            form.render();

            // 列出语言
            for (let i = 0; i <= 5; i++) {
                let t = 1 << i;
                let lang = {};
                lang['lang[' + i + ']'] = (languages & t) === t;
                console.log(lang);
                form.val('set-languages', lang);
            }

            setLanguagesIndex = layer.open({
                type: 1,
                content: $('#set-languages-form'),
                area: ['500px', '220px'],
                resize: false,
                closeBtn: 2,
                cancel: () => {
                    $('#set-languages-form').hide();
                    if (obj.data.languages !== languages) {
                        obj.data.languages = languages;
                        update(obj);
                    }
                }
            });
        }
    });

    form.on('submit(create)', (data) => {
        let contest = data.field;
        contest.languages = languages;
        if (Date.parse(contest.startAt) >= Date.parse(contest.endAt)) {
            layer.msg('开始时间不能大于结束时间!', {icon: 5});
        } else {
            let loadIndex;
            $.ajax({
                url: baseUrl + '/api/manager/contest/pro?userId=' + user.userId,
                method: 'post',
                headers: {
                    "Content-Type": "application/json",
                    "token": user.token
                },
                data: JSON.stringify(contest),
                beforeSend: () => {
                    loadIndex = layer.load(1);
                },
                success: () => {
                    layer.close(loadIndex);
                    layer.msg('已创建！', {icon: 6});
                    $('#contest-form').hide();
                    layer.close(createContestIndex);
                    refresh('contests');
                },
                error: (xhr) => {
                    layer.close(loadIndex);
                    layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
                }
            });
        }
    });

    form.on('checkbox(language)', function (data) {
        if (data.elem.checked) {
            languages = languages | 1 << data.value;
            console.log(languages);
        } else {
            languages = languages ^ 1 << data.value;
            console.log(languages);
        }
    });
});

function update(obj) {
    let contest = obj.data;

    if (Date.parse(contest.startAt) >= Date.parse(contest.endAt)) {
        layer.msg('开始时间不能大于结束时间!', {icon: 5});
        refresh('contests');
        return;
    }

    let loadIndex;

    $.ajax({
        url: baseUrl + '/api/manager/contest/pro?userId=' + user.userId,
        method: 'put',
        headers: {
            "Content-Type": "application/json",
            "token": user.token
        },
        data: JSON.stringify(contest),
        beforeSend: () => {
            loadIndex = layer.load(1);
        },
        success: () => {
            layer.close(loadIndex);
            layer.msg('已保存！', {icon: 6});
            refresh('contests');
        },
        error: (xhr) => {
            layer.close(loadIndex);
            layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
            refresh('contests');
        }
    });
}

// 显示竞赛/作业的题目
function showContestProblems(contestId, contestName) {
    layer.open({
        type: 1,
        title: contestName,
        resize: false,
        content: $('#contest-problem-table'),
        area: ['650px', '600px'],
        closeBtn: 2,
        cancel: () => {
            $('#contest-problem-table').hide();
        }
    });

    table.render({
        elem: '#contest-problems',
        url: baseUrl + '/api/manager/contest/' + contestId,
        parseData: (res) => {
            return {
                "code": 0,
                "count": res === undefined ? 0 : res.count,
                "data": res === undefined ? null : res.data
            }
        },
        skin: 'nob',
        size: 'lg',
        height: '500px',
        page: true,
        limit: 15,
        limits: [10, 15, 20, 25, 30],
        toolbar: '#contest-problem-toolbar',
        defaultToolbar: [],
        cols: [
            [
                {field: 'problemId', title: '编号', width: '15%', align: 'center'},
                {field: 'title', title: '题目名称', width: '65%'},
                {title: '操作', width: '20%', align: 'center', toolbar: '#contestProblemToolbar'}
            ]
        ]
    });

    table.on('tool(contest-problems)', (obj) => {
        if (obj.event === 'remove') {
            removeProblems(contestId, obj.data.problemId);
        }
    });

    table.on('toolbar(contest-problems)', (obj) => {
        if (obj.event === 'add-problem') {
            showProblems(contestId);
        }
    });
}

function showProblems(contestId) {
    layer.open({
        type: 1,
        title: '题目列表',
        resize: false,
        content: $('#problems-table'),
        area: ['700px', '600px'],
        closeBtn: 2,
        cancel: () => {
            $('#problems-table').hide();
        }
    });

    table.render({
        elem: '#problems',
        url: '../api/manager/contest/pro/' + contestId,
        headers: {token: user.token},
        where: {
            userId: user.userId
        },
        parseData: (res) => {
            return {
                "code": 0,
                "count": res === undefined ? 0 : res.count,
                "data": res === undefined ? null : res.data
            }
        },
        skin: 'nob',
        size: 'lg',
        height: '500px',
        page: true,
        limit: 10,
        limits: [10, 15, 20, 25],
        cols: [
            [
                {field: 'problemId', title: '编号', width: '15%'},
                {field: 'title', title: '题目名称', width: '35%'},
                {title: '操作', width: '25%', align: 'center', toolbar: '#addProblemToolbar'},
                {field: 'createAt', title: '创建时间', width: '25%', align: 'center'}
            ]
        ]
    });

    table.on('tool(problems)', (obj) => {
        if (obj.event === 'add') {
            addProblem(contestId, obj.data.problemId);
        }
    });
}

function addProblem(contestId, problemId) {
    let loadIndex;
    $.ajax({
        url: baseUrl + '/api/manager/contest/pro/problem/' + contestId + '/' + problemId + '?userId=' + user.userId,
        method: 'post',
        headers: {
            token: user.token
        },
        beforeSend: () => {
            loadIndex = layer.load(1);
        },
        success: () => {
            layer.close(loadIndex);
            layer.msg('添加成功！', {icon: 6});
            refresh('problems');
            refresh('contest-problems');
        },
        error: (xhr) => {
            layer.close(loadIndex);
            layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
        }
    });
}

// 从竞赛/作业中移除题目
function removeProblems(contestId, problemId) {
    let loadIndex;
    layer.confirm('确定要从竞赛中移除此题吗？（题目不会被删除）', {icon: 3, title: '提示'}, (index) => {
        $.ajax({
            url: baseUrl + '/api/manager/contest/pro/problem/' + contestId + '/' + problemId + '?userId=' + user.userId,
            method: 'delete',
            headers: {
                token: user.token
            },
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: () => {
                layer.close(loadIndex);
                layer.msg('已移除！', {icon: 6});
                refresh('contest-problems');
            },
            error: (xhr) => {
                layer.close(loadIndex);
                layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
            }
        });
        layer.close(index);
    });
}

// 删除竞赛/作业
function delContest(data) {
    let loadIndex;
    layer.confirm('确定要删除 [' + data.contestName + '] 吗？', {icon: 3, title: '提示'}, (index) => {
        $.ajax({
            url: baseUrl + '/api/manager/contest/pro/' + data.contestId + '?userId=' + user.userId,
            method: 'delete',
            headers: {
                token: user.token
            },
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: () => {
                layer.close(loadIndex);
                layer.msg('已删除！', {icon: 6});
                refresh('contests');
            },
            error: (xhr) => {
                layer.close(loadIndex);
                if (xhr.status === 400)
                    layer.msg(xhr.responseText, {icon: 5});
                else if (xhr.status === 410)
                    layer.msg(errorText.get(410), {icon: 5});
                else
                    layer.msg('请求失败！', {icon: 5});
                refresh('contests');
            }
        });
        layer.close(index);
    });
}

function refresh(tableId) {
    if (table.cache[tableId].length === 0)
        table.reload(tableId);
    else
        $(".layui-laypage-btn").click();
}