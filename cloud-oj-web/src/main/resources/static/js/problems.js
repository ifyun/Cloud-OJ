let problemsTable;
let contestId, contestName;

$(document).ready(() => {
    contestId = getQueryVariable('contestId');

    if (contestId != null) {
        $('#search-box').hide();
        $('#nav-contests').addClass("layui-this");
    } else {
        $('#nav-problems').addClass("layui-this");
    }

    let apiUrl = contestId == null ?
        baseUrl + '/api/manager/problem' :
        baseUrl + '/api/manager/contest/' + contestId;

    if (user.userId != null) apiUrl += '?userId=' + user.userId;

    layui.use(['table', 'element'], () => {
        let element = layui.element;
        element.init();
        let table = layui.table;

        problemsTable = table.render({
            elem: '#problems',
            url: apiUrl,
            parseData: (res) => {
                if (res !== undefined && res.count > 0 && res.data[0].contestName !== undefined) {
                    contestName = res.data[0].contestName;
                    $('#current').text(contestName);
                    $('#contest_name').text(contestName + '-题目列表');
                    $('#breadcrumb').show();
                }
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
            defaultToolbar: [],
            toolbar: '#top-bar',
            cols: [
                [
                    {field: 'problemId', title: '编号', width: '10%', align: 'center'},
                    {field: 'title', title: '题目名称', width: '25%', templet: '#titleTpl'},
                    {field: 'result', title: '状态', width: '15%', align: 'center', templet: '#resultTpl'},
                    {field: 'category', title: '标签/分类', width: '40%', align: 'center', templet: '#categoryTpl'},
                    {field: 'createAt', title: '创建时间', width: '10%'}
                ]
            ]
        });
    });
});

function onSearch(e) {
    if (e.keyCode === 13) {
        addSearchTag($('#keyword').val().trim());
    }
}

function search(keyword) {
    problemsTable.reload({
        where: {
            keyword: keyword
        }
    });
}

function addSearchTag(text) {
    if (text.length === 0) return;
    search(text);
    let searchTag = $('<span class="tag bg-green">' + text + '</span>')
        .append($('<span style="cursor: pointer">&nbsp;&nbsp;X</span>').on('click', clear));
    $('#search-tag').text('当前关键字：')
        .append(searchTag);
}

function addTag(text) {
    if (text.length === 0) return;
    search(text);
    let tag = $('<span class="tag">' + text + '</span>')
        .addClass(getColor(text))
        .append($('<span style="cursor: pointer">&nbsp;&nbsp;X</span>').on('click', clear));
    $('#tag').text('当前分类：')
        .append(tag);
}

function clear() {
    problemsTable.reload({
        where: {
            keyword: null
        }
    });
}