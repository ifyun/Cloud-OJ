let editorIndex, isAddMode;
let problemId, problemsTable;
let form, table, upload;

layui.use(['element', 'table', 'form', 'upload'], () => {
    let element = layui.element;
    element.init();
    table = layui.table;
    upload = layui.upload;
    form = layui.form;
    form.render();
});

$(document).ready(() => {
    $('#nav-manager').addClass("layui-this");

    // 题目列表
    problemsTable = table.render({
        elem: '#problems',
        url: baseUrl + '/api/manager/problem/pro',
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
        page: true,
        limit: 25,
        limits: [15, 25, 35, 50, 100],
        defaultToolbar: [],
        toolbar: '#top-bar',
        cols: [
            [
                {field: 'problemId', title: '编号', width: '7.5%', align: 'center'},
                {field: 'title', title: '题目名称', width: '22.5%', templet: '#titleTpl'},
                {field: 'category', title: '标签', width: '25%', align: 'center', templet: '#categoryTpl'},
                {field: 'enable', title: '是否开放', width: '10%', align: 'center', templet: '#enableTpl'},
                {title: '操作', width: '25%', align: 'center', toolbar: '#optToolbar'},
                {field: 'createAt', title: '创建时间', width: '10%', align: 'center', sort: true}
            ]
        ]
    });

    table.on('tool(problems)', (obj) => {
        problemId = obj.data.problemId;
        if (obj.event === 'edit') {
            $('#reset').hide();
            $.ajax({
                url: baseUrl + '/api/manager/problem/pro/' + problemId + '?userId=' + user.userId,
                method: 'get',
                headers: {token: user.token},
                success: (data) => {
                    $('#save').text('保 存');
                    openEditor(data)
                },
                error: (xhr) => {
                    layer.msg(errorText.get(xhr.status), {icon: 5});
                }
            });
        } else if (obj.event === 'edit-test-data') {
            $("#upload-file-table tr:not(:first)").empty();
            obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');

            table.render({
                elem: '#test-data',
                url: baseUrl + '/api/file/test_data/' + obj.data.problemId,
                headers: {
                    "token": user.token
                },
                where: {
                    userId: user.userId
                },
                parseData: (res) => {
                    return {
                        "code": 0,
                        "data": res
                    }
                },
                skin: 'row',
                cols: [
                    [
                        {field: 'fileName', title: '文件名', width: '35%'},
                        {field: 'size', title: '文件长度', width: '35%', templet: '#file-length'},
                        {title: '操作', width: '30%', align: 'center', toolbar: '#test-data-toolbar'},
                    ]
                ]
            });

            table.on('tool(test-data)', (obj) => {
                if (obj.event === 'edit-content') {
                    layer.open({
                        type: 1,
                        title: obj.data.fileName,
                        area: ['500px', '450px'],
                        closeBtn: 2,
                        content: '<textarea readonly="readonly" class="layui-textarea"' +
                            ' style="position: absolute; top: 0; bottom: 0;">'
                            + obj.data.content + '</textarea>'
                    });
                } else if (obj.event === 'del-file') {
                    layer.confirm('确定要删除文件吗？', {icon: 3, title: '提示'}, (index) => {
                            $.ajax({
                                url: baseUrl + '/api/file/test_data/' + problemId + '?name=' + obj.data.fileName
                                    + '&userId=' + user.userId,
                                method: 'delete',
                                headers: {
                                    "Content-Type": "application/json",
                                    "token": user.token
                                },
                                success: () => {
                                    layer.msg("删除成功", {icon: 6});
                                    table.reload('test-data');
                                },
                                error: (xhr) => {
                                    layer.msg("删除失败: " + xhr.responseText, {icon: 5});
                                }
                            });
                            layer.close(index);
                        }
                    )
                } else if (obj.event === 'download-file') {
                    window.open('/api/file/test_data/file/' + problemId + '/' + obj.data.fileName
                        + '?userId=' + user.userId + '&token=' + user.token, '_blank')
                }
            });
            openTestDataEditor();
        } else if (obj.event === 'del') {
            layer.confirm('确定要删除吗？', {icon: 3, title: '提示'}, (index) => {
                $.ajax({
                    url: baseUrl + '/api/manager/problem/pro/' + obj.data.problemId + '?userId=' + user.userId,
                    method: 'delete',
                    headers: {
                        token: user.token
                    },
                    success: () => {
                        layer.msg('删除成功');
                        refresh('problems');
                    },
                    error: (xhr) => {
                        layer.open({
                            content: xhr.status + ' 删除失败！'
                        });
                    }
                });
                layer.close(index);
            });
        }
    });

    // 切换开放/关闭
    form.on('switch(enable)', (obj) => {
        let problemId = obj.value, enable = obj.elem.checked;
        $.ajax({
            url: baseUrl + '/api/manager/problem/pro/' + problemId + '?enable=' + enable + '&userId=' + user.userId,
            method: 'put',
            headers: {
                "Content-Type": "application/json",
                "token": user.token
            },
            success: () => {
                let msg = enable ? '已开放' : '已关闭';
                layer.msg(msg, {icon: 6});
            },
            error: (xhr) => {
                layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
                obj.elem.checked = !enable;
                form.render('checkbox');
            }
        });
    });

    // 添加/保存题目
    form.on('submit(save)', (data) => {
        let problem = data.field;

        problem.enable = (problem.enable === 'on');

        let loadIndex;
        let method = isAddMode ? 'post' : 'put';
        $.ajax({
            url: baseUrl + '/api/manager/problem/pro?userId=' + user.userId,
            method: method,
            headers: {
                "Content-Type": "application/json",
                "token": user.token
            },
            data: JSON.stringify(problem),
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: () => {
                layer.close(loadIndex);
                layer.msg('成功！', {icon: 6});
                resetForm();
                $('#editor').hide();
                layer.close(editorIndex);
                refresh('problems');
            },
            error: (xhr) => {
                layer.close(loadIndex);
                layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
            }
        });

        return false;
    });

    form.verify({
        timeout: (value, item) => {
            if (value > 3000 || value < 500)
                return '时限范围为：[500, 3000]';
        }
    });

    let fileListView = $('#file-list');
    let uploadListIns = upload.render({
        elem: '#select-files',
        url: baseUrl + '/api/file/test_data?userId=' + user.userId,
        headers: {
            token: user.token
        },
        data: {
            problemId: () => {
                return problemId
            }
        },
        accept: 'file',
        exts: 'in|out',
        multiple: true,
        auto: false,
        bindAction: '#upload-files',
        choose: function (obj) {
            $('#upload-file-table').show();
            $('#upload-files').show();
            let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                let tr = $(['<tr id="upload-' + index + '">'
                    , '<td>' + file.name + '</td>'
                    , '<td>' + file.size + '字节</td>'
                    , '<td>等待上传</td>'
                    , '<td>'
                    , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    , '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    , '</td>'
                    , '</tr>'].join(''));

                //单个重传
                tr.find('.demo-reload').on('click', function () {
                    obj.upload(index, file);
                });

                //删除
                tr.find('.demo-delete').on('click', function () {
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });

                fileListView.append(tr);
            });
        }
        , done: function (res, index, upload) {
            //上传成功
            table.reload('test-data');
            let tr = fileListView.find('tr#upload-' + index)
                , tds = tr.children();
            tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
            tds.eq(3).html(''); //清空操作
            return delete this.files[index]; //删除文件队列已经上传成功的文件
        }
        , error: function (index, upload) {
            let tr = fileListView.find('tr#upload-' + index)
                , tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });
});

function openTestDataEditor() {
    layer.open({
        type: 1,
        title: '编辑测试数据',
        content: $('#test-data-editor'),
        area: ['1000px', '650px'],
        offset: '150px',
        closeBtn: 2,
        cancel: () => {
            $('#test-data-editor').hide();
        }
    });
}

/**
 * 打开编辑窗口
 */
function openEditor(problem) {
    isAddMode = (problem == null);
    editorIndex = layer.open({
        type: 1,
        title: isAddMode ? '添加题目' : '编辑题目',
        content: $('#editor'),
        area: ['1140px', '800px'],
        offset: '150px',
        maxmin: true,
        cancel: () => {
            resetForm();
            $('#editor').hide();
        }
    });
    form.val('edit-problem', problem);
}

/**
 * 重置表单
 */
function resetForm() {
    form.val('edit-problem', {
        title: null,
        enable: null,
        category: null,
        timeout: null,
        score: null,
        description: null,
        input: null,
        output: null,
        sampleInput: null,
        sampleOutput: null,
    });
}

function refresh(tableId) {
    if (table.cache[tableId].length === 0)
        table.reload(tableId);
    else
        $(".layui-laypage-btn").click();
}

function onSearch(e) {
    if (e.keyCode === 13) {
        addSearchTag($('#keyword').val().trim());
    }
}

function search(keyword) {
    problemsTable.reload({
        where: {
            keyword: keyword.trim()
        }
    });
}

function addSearchTag(text) {
    if (text.length === 0) return;
    search(text);
    let searchTag = $('<span class="tag bg-green">' + text + '</span>')
        .append($('<span style="cursor: pointer">&nbsp;&nbsp;X</span>').on('click', clear));
    $('#search-tag').text('关键字：')
        .append(searchTag);
}

function addTag(text) {
    if (text.length === 0) return;
    search(text);
    let tag = $('<span class="tag">' + text + '</span>')
        .addClass(getColor(text))
        .append($('<span style="cursor: pointer">&nbsp;&nbsp;X</span>').on('click', clear));
    $('#tag').text('分类：')
        .append(tag);
}

function clear() {
    problemsTable.reload({
        where: {
            keyword: null
        }
    });
}