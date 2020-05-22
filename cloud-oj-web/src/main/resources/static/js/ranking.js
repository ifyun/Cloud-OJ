let table;

$(document).ready(() => {
    let apiUrl = baseUrl + '/api/manager/ranking';
    let contestId = getQueryVariable('contestId');

    if (contestId != null) {
        apiUrl += '/contest/' + contestId;
    }

    layui.use(['table', 'element'], () => {
        table = layui.table;
        let element = layui.element;
        element.init();
    });

    table.render({
        elem: '#ranking',
        url: apiUrl,
        parseData: (res) => {
            if (res !== undefined && res.count > 0 && res.data[0].contestName !== undefined)
                $('#contest_name').text(res.data[0].contestName + '-排行榜');
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
        toolbar: '#top-bar',
        defaultToolbar: ['print', 'exports'],
        autoSort: false,
        cols: [
            [
                {title: '排名', type: 'numbers', width: '10%'},
                {field: 'userId', title: '用户ID', width: '15%'},
                {field: 'name', title: '用户名', width: '15%'},
                {field: 'committed', title: '总提交次数', width: '20%', style: 'color: #1E9FFF'},
                {field: 'passed', title: '已通过题目', width: '20%', style: 'color: #5FB878'},
                {field: 'totalScore', title: '总分数', width: '20%'}
            ]
        ]
    });
});

$('#nav-ranking').addClass('layui-this');