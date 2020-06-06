let form;
let resultIndex;

$(document).ready(() => {
    let contestId = getQueryVariable('contestId');

    let language = 0;
    let codeLang = ['text/x-csrc', 'text/x-c++src', 'text/x-java', 'text/x-python', 'text/x-sh', 'text/x-csharp'];

    let editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: codeLang[0],
        theme: 'monokai',
        indentUnit: 4,
        lineNumbers: true,
        matchBrackets: true,
        autoCloseBrackets: true,
        lineWrapping: true,
        showHint: true
    });

    editor.setSize('auto', '650px');

    layui.use(['form', 'element'], function () {
        form = layui.form;
        let element = layui.element;
        form.render();
        element.init();
    });

    resize();

    let contestName;

    if (contestId != null) {
        $.ajax({
            url: baseUrl + '/api/manager/contest/lang/' + contestId,
            method: 'get',
            success: (data) => {
                let languages = data.languages;
                contestName = data.contestName;
                $('.lang').each((index, item) => {
                    let t = 1 << index;
                    if ((languages & t) !== t)
                        $(item).remove();
                });
                form.render('select');
                $('#problem-list').attr('href', './problems?contestId=' + contestId).text(contestName);
                if (Date.parse(data.endAt) >= new Date())
                    $('#end-time').text('结束时间：' + data.endAt);
                else {
                    $('#end-time').css('color', '#FF5722').text('当前竞赛/作业已结束');
                    $('#commit-code').addClass('layui-btn-disabled');
                }
                $('#breadcrumb').show();
            }
        });
    }

    form.on('select(language)', (data) => {
        language = data.value;
        editor.setOption('mode', codeLang[language]);
    });

    // 提交代码
    form.on('submit(commit-code)', (data) => {
        if (user.token == null) {
            console.log('未登录');
            layer.open({
                content: '你还没有登录！',
                btn: ['去登录'],
                yes: (index) => {
                    window.location.href = './index?login=true';
                    layer.close(index);
                }
            });
        } else {
            let commitData = {
                userId: user.userId,
                problemId: $('#problemId').text(),
                language: parseInt(data.field.language),
                sourceCode: editor.getValue()
            };

            if (commitData.sourceCode.length === 0) {
                return;
            }

            if (contestId !== null) commitData.contestId = contestId;
            let loadIndex;
            $.ajax({
                url: baseUrl + '/api/judge/commit?userId=' + user.userId,
                method: 'post',
                headers: {
                    "Content-Type": "application/json",
                    "token": user.token
                },
                data: JSON.stringify(commitData),
                beforeSend: () => {
                    loadIndex = layer.load(1);
                },
                success: (data) => {
                    layer.close(loadIndex);
                    resultIndex = layer.open({
                        btn: [],
                        content: '已提交，正在等待结果...',
                        closeBtn: false
                    });
                    getResult(data, 1);
                },
                error: (xhr) => {
                    layer.close(loadIndex);
                    layer.open({
                        content: errorText.get(xhr.status)
                    });
                }
            });
        }
    });
});

$(window).resize(resize);

function getResult(solutionId, count) {
    $.ajax({
        url: baseUrl + '/api/judge/commit?solutionId=' + solutionId + '&userId=' + user.userId,
        headers: {
            "token": user.token
        },
        method: 'get',
        success: (data) => {
            if (data === '' && count <= 10) {
                setTimeout(() => {
                    getResult(solutionId, count + 1);
                }, 1000);
            } else if (count >= 10) {
                layer.open({
                    content: '等待时间过长，你可以稍后去提交记录中查看！'
                });
            } else {
                let result;
                switch (data.result) {
                    case 4:
                        result = "编译错误！";
                        break;
                    case 3:
                        result = "答案错误！";
                        break;
                    case 2:
                        result = "部分通过（" + data.passRate * 100 + ")";
                        break;
                    case 1:
                        result = "时间超限";
                        break;
                    case 0:
                        result = "完全正确（100）";
                        break;
                }
                layer.close(resultIndex);
                layer.open({
                    content: result === undefined ? data : result
                });
            }
        },
        error: (xhr) => {
            layer.close(resultIndex);
            layer.open({
                content: errorText.get(xhr.status)
            });
        }
    });
}

function resize() {
    let width = $(window).width();
    let problemPane = $('#problem-pane');
    let codePane = $('#code-pane');

    if (width >= 1500) {
        let paneHeight = Math.max(problemPane.height(), codePane.height());
        $('.layui-main').css('width', width <= 1800 ? (width - 30) + 'px' : '1770px');
        $('.layui-container').css('width', width <= 1800 ? width + 'px' : '1800px');
        $('.layui-card')
            .css('width', 'calc(50% - 10px)')
            .css('height', paneHeight)
            .css('float', 'left');
        problemPane.css('margin', '0 10px 0 0');
        codePane.css('margin', '0 0 0 10px');
    } else {
        $('.layui-main').css('width', '1140px');
        $('.layui-container').css('width', '1170px');
        $('.layui-card')
            .css('width', '')
            .css('height', '')
            .css('float', '');
        problemPane.css('margin', '');
        codePane.css('margin', '10px 0 0 0');
    }
}