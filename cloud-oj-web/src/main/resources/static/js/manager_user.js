let table, form;

layui.use(['element', 'table', 'form'], () => {
    let element = layui.element;
    element.init();
    table = layui.table;
    form = layui.form;
});

$(document).ready(() => {
    $('#nav-manager').addClass("layui-this");

    table.render({
        elem: '#users',
        url: baseUrl + '/api/manager/user/pro',
        headers: {token: user.token},
        where: {
            userId: user.userId
        },
        parseData: (res) => {
            return {
                "code": 0,
                "count": res === undefined ? 0 : res.count,
                "data": res === undefined ? null : res.data,
                "msg": res.msg
            }
        },
        skin: 'nob',
        size: 'lg',
        page: true,
        limit: 25,
        limits: [15, 25, 35, 50],
        cols: [
            [
                {field: 'userId', title: '用户ID/学号', width: '20%'},
                {field: 'name', title: '用户名', width: '20%'},
                {field: 'roleId', title: '角色/权限', width: '20%', align: 'center', templet: '#roleTpl'},
                {title: '操作', width: '20%', align: 'center', toolbar: '#optToolbar'},
                {field: 'createAt', title: '创建时间', width: '20%', sort: true}
            ]
        ]
    });

    let editUserIndex;

    table.on('tool(users)', (obj) => {
        if (obj.event === 'edit') {
            form.val('user-info', obj.data);
            editUserIndex = layer.open({
                type: 1,
                title: '编辑用户信息',
                content: $('#user-form'),
                area: ['450px', '400px'],
                resize: false,
                closeBtn: 2,
                cancel: () => {
                    $('#user-form').hide();
                }
            });
        }
    });

    form.on('submit(save)', (data) => {
        let userInfo = data.field;
        if (userInfo.password.toString().trim() === '')
            delete userInfo.password;
        else
            userInfo.password = $.md5(userInfo.password);

        let loadIndex;

        $.ajax({
            url: baseUrl + '/api/manager/user/pro?userId=' + user.userId,
            method: 'put',
            headers: {
                "Content-Type": "application/json",
                "token": user.token
            },
            data: JSON.stringify(userInfo),
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: (data, status, xhr) => {
                console.log(xhr.status);
                if (xhr.status === 304) {
                    layer.msg('未修改任何数据！', {icon: 5});
                } else {
                    layer.msg('保存成功！', {icon: 6});
                    $('#user-form').hide();
                    layer.close(loadIndex);
                    layer.close(editUserIndex);
                }
                $(".layui-laypage-btn").click();
            },
            error: (xhr) => {
                layer.close(loadIndex);
                layer.msg(xhr.status + ': ' + xhr.responseText, {icon: 5});
            }
        });

        return false;
    });
});