create table problem
(
    problem_id    int auto_increment
        primary key,
    title         varchar(64)                          not null,
    description   text                                 not null comment '题目描述',
    input         text                                 not null comment '输入说明',
    output        text                                 not null comment '输出说明',
    sample_input  text                                 null comment '示例输入',
    sample_output text                                 null comment '示例输出',
    score         int        default 0                 not null comment '分数',
    enable        tinyint(1) default 0                 null comment '是否可用',
    create_at     datetime   default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '题目表';

create index problem_title_index
    on problem (title);

create table role
(
    role_id   int         not null
        primary key,
    role_name varchar(16) not null
);

create table user
(
    user_id  varchar(16) not null
        primary key,
    name     varchar(16) not null comment '用户名',
    password char(32)    not null,
    email    varchar(32) null,
    section  varchar(16) null comment '班级',
    role_id  int         null,
    constraint user_role_role_id_fk
        foreign key (role_id) references role (role_id)
            on update cascade
);

create table solution
(
    solution_id int auto_increment
        primary key,
    user_id     varchar(16)                        null,
    problem_id  int                                null,
    language    int                                not null,
    state       int      default 2                 not null comment '状态(2->已提交,1->在判题队列,0->已判题)',
    result      int                                null comment '结果(3->编译错误,2->答案错误,1->部分通过,0->完全正确)',
    pass_rate   double   default 0                 not null comment '通过率',
    submit_time datetime default CURRENT_TIMESTAMP not null comment '提交时间',
    constraint solution_problem_problem_id_fk
        foreign key (problem_id) references problem (problem_id)
            on update cascade on delete cascade,
    constraint solution_user_user_id_fk
        foreign key (user_id) references user (user_id)
            on update cascade
);

create table compile
(
    id          int auto_increment
        primary key,
    solution_id int  null,
    state       int  not null comment '编译状态(0->编译成功,-1->编译出错)',
    info        text null comment '编译信息',
    constraint compile_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
)
    comment '存储编译信息';

create table runtime
(
    id          int auto_increment
        primary key,
    solution_id int  null,
    state       int  null comment '状态，0表示正常退出',
    info        text null,
    total       int  null comment '总测试点数量',
    passed      int  null comment '通过的测试点数量',
    constraint runtime_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
)
    comment '代码运行信息';

create table source_code
(
    code_id     int auto_increment
        primary key,
    solution_id int  null,
    code        text not null,
    constraint source_code_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
);

create index user_name_index
    on user (name);

create index user_section_index
    on user (section);

