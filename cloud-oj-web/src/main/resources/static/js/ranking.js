let table;

layui.use(['table', 'element'], () => {
    table = layui.table;
    let element = layui.element;
    element.init();
});

$(document).ready(() => {
    let apiUrl = baseUrl + '/api/manager/ranking';
    let contestId = getQueryVariable('contestId');

    if (contestId != null) {
        apiUrl += '/contest/' + contestId;
    }

    table.render({
        elem: '#ranking',
        url: apiUrl,
        parseData: (res) => {
            if (res !== undefined && res.count > 0 && res.data[0].contestName !== undefined) {
                $('#contest_name').text(res.data[0].contestName + '-排行榜');
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
        limit: 25,
        limits: [15, 20, 25, 30, 40],
        toolbar: '#top-bar',
        defaultToolbar: ['print', 'exports'],
        autoSort: false,
        cols: [
            [
                {field: 'rank', title: '排名', width: '10%', align: 'center', templet: '#rankingTpl'},
                {field: 'name', title: '用户名', width: '21%', align: 'center', templet: '#nameTpl'},
                {field: 'userId', title: 'ID', width: '15%'},
                {
                    field: 'committed',
                    title: '总提交次数',
                    width: '18%',
                    align: 'right',
                    style: 'color: #1E9FFF; font-weight: bold'
                },
                {
                    field: 'passed',
                    title: '已通过题目',
                    width: '18%',
                    align: 'right',
                    style: 'color: #5FB878; font-weight: bold'
                },
                {
                    field: 'totalScore',
                    title: '总分数',
                    width: '18%',
                    align: 'right',
                    style: 'color: black; font-weight: bold; font-size: 13pt'
                }
            ]
        ]
    });
});

$('#nav-ranking').addClass('layui-this');