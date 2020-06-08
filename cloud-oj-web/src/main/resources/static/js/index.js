let $startBtn = $('#start-btn');
let form;

layui.use(['form', 'element', 'carousel'], function () {
    form = layui.form;
    form.verify({
        id: [
            /^[A-Za-z0-9]{8,16}$/,
            'ID必须是8到16位字母或数字！'
        ],
        name: [
            /^[\S]{2,16}$/
            , '用户名必须2到16个字符，且不能出现空格！'
        ],
        pass: [
            /^[\S]{6,16}$/
            , '密码必须6到16位，且不能出现空格！'
        ]
    });
    let element = layui.element;
    element.init();
});

$(document).ready(() => {
    $startBtn.text(user.token === null ? '登录去练习' : '去练习');
    form.render();
    if (getQueryVariable('login') === 'true') {
        setTimeout(() => {
            startCodding()
        }, 500);
    }

    let loadIndex;

    // Login
    form.on('submit(login)', (data) => {
        let login = data.field;

        $.ajax({
            url: baseUrl + '/login',
            method: 'post',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            data: {
                username: login.userId,
                password: $.md5(login.password)
            },
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: (data) => {
                layer.close(loadIndex);
                user = data;
                $('#user-name').text(user.name);
                $.cookie('cloudOjToken', JSON.stringify(user));
                window.location.href = './problems';
                $('#poster').text(poster_login);
            },
            error: (xhr) => {
                layer.close(loadIndex);
                layer.open({
                    content: xhr.status === 400 ? '用户名或密码错误！' : '出现了错误！'
                });
            }
        });
    });

    // Register
    form.on('submit(register)', (data) => {
        let register = data.field;
        register.password = $.md5(register.password);
        $.ajax({
            url: baseUrl + '/api/manager/user',
            method: "post",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify(register),
            beforeSend: () => {
                loadIndex = layer.load(1);
            },
            success: () => {
                layer.close(loadIndex);
                layer.open({
                    content: '注册成功'
                });
            },
            error: (xhr) => {
                layer.close(loadIndex);
                layer.open({
                    content: xhr.status === 400 ? '用户已存在！' : xhr.status + ': 发生了错误！'
                });
            }
        });
    });
});

function startCodding() {
    if (user.token !== null) {
        window.location.href = './problems';
    } else {
        $startBtn.hide();
        $('#poster-div').css('margin-top', '5%');
        $('#login-pane').show(500);
        $('')
    }
}

$('#password').keydown((e) => {
    if (e.keyCode === 13)
        $('#login').trigger('click');
});