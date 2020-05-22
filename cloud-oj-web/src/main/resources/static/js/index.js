let $startBtn = $('#start-btn');

$(document).ready(() => {
    $startBtn.text(user.token === null ? '登录去练习' : '去练习');
    if (getQueryVariable('login') === 'true') {
        setTimeout(() => {
            startCodding()
        }, 500);
    }
});

layui.use(['form', 'element', 'carousel'], function () {
    let form = layui.form;
    form.render();

    let element = layui.element;
    element.init();

    let carousel = layui.carousel;

    carousel.render({
        elem: '#pic',
        width: '100%',
        height: '100%',
        anim: 'fade'
    });

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

    form.verify({
        pass: [
            /^[\S]{6,12}$/
            , '密码必须6到12位，且不能出现空格'
        ]
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