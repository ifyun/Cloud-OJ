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
    timeout       bigint     default 1000              null comment '时间限制',
    score         int        default 0                 not null comment '分数',
    enable        tinyint(1) default 0                 null comment '是否可用',
    category      varchar(32)                          null comment '分类',
    create_at     datetime   default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '题目表';

create index problem_title_index
    on problem (title);

create table role
(
    role_id   int         not null
        primary key,
    role_name varchar(32) not null
);

create table user
(
    user_id  varchar(32)   not null
        primary key,
    name     varchar(16)   not null comment '用户名',
    password varchar(100)  not null,
    email    varchar(32)   null,
    section  varchar(16)   null comment '班级',
    role_id  int default 0 not null,
    constraint user_role_role_id_fk
        foreign key (role_id) references role (role_id)
            on update cascade
);

create table solution
(
    solution_id int auto_increment
        primary key,
    problem_id  int                                null,
    language    int                                not null,
    state       int      default 2                 not null comment '状态(2->已提交,1->在判题队列,0->已判题)',
    result      int                                null comment '结果(4->编译错误,3->答案错误,2->部分通过,1->时间超限,0->完全正确)',
    pass_rate   double   default 0                 not null comment '通过率',
    user_id     varchar(32)                        null,
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
    solution_id int    null,
    total       int    null comment '总测试点数量',
    passed      int    null comment '通过的测试点数量',
    time        bigint null comment '耗时（ms）',
    info        text   null,
    output      text   null comment '输出',
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

create definer = root@`%` view judge_result as
select `s`.`solution_id`               AS `solution_id`,
       `s`.`problem_id`                AS `problem_id`,
       `p`.`title`                     AS `title`,
       `s`.`language`                  AS `language`,
       `s`.`state`                     AS `state`,
       `s`.`result`                    AS `result`,
       `s`.`pass_rate`                 AS `pass_rate`,
       `s`.`user_id`                   AS `user_id`,
       `s`.`submit_time`               AS `submit_time`,
       (`s`.`pass_rate` * `p`.`score`) AS `score`,
       `sc`.`code`                     AS `code`,
       `c`.`state`                     AS `compile_state`,
       `c`.`info`                      AS `compile_info`,
       `r`.`time`                      AS `time`
from ((((`cloud_oj`.`solution` `s` join `cloud_oj`.`problem` `p` on ((`s`.`problem_id` = `p`.`problem_id`))) join `cloud_oj`.`compile` `c` on ((`s`.`solution_id` = `c`.`solution_id`))) join `cloud_oj`.`runtime` `r` on ((`s`.`solution_id` = `r`.`solution_id`)))
         join `cloud_oj`.`source_code` `sc` on ((`s`.`solution_id` = `sc`.`solution_id`)));

-- comment on column judge_result.state not supported: 状态(2->已提交,1->在判题队列,0->已判题)

-- comment on column judge_result.result not supported: 结果(4->编译错误,3->答案错误,2->部分通过,1->时间超限,0->完全正确)

-- comment on column judge_result.pass_rate not supported: 通过率

-- comment on column judge_result.submit_time not supported: 提交时间

-- comment on column judge_result.compile_state not supported: 编译状态(0->编译成功,-1->编译出错)

-- comment on column judge_result.compile_info not supported: 编译信息

-- comment on column judge_result.time not supported: 耗时（ms）

create definer = root@`%` view ranking_list as
select `problem_score`.`user_id`                                    AS `user_id`,
       `problem_score`.`name`                                       AS `name`,
       sum(`problem_score`.`committed`)                             AS `committed`,
       count((case when (`problem_score`.`result` = 0) then 1 end)) AS `passed`,
       sum(`problem_score`.`score`)                                 AS `total_score`
from (select `s`.`user_id`                        AS `user_id`,
             `u`.`name`                           AS `name`,
             count(`p`.`problem_id`)              AS `committed`,
             `s`.`result`                         AS `result`,
             max((`s`.`pass_rate` * `p`.`score`)) AS `score`
      from ((`cloud_oj`.`solution` `s` join `cloud_oj`.`user` `u` on ((`s`.`user_id` = `u`.`user_id`)))
               join `cloud_oj`.`problem` `p` on ((`s`.`problem_id` = `p`.`problem_id`)))
      group by `s`.`user_id`, `u`.`name`, `p`.`problem_id`, `s`.`result`) `problem_score`
group by `problem_score`.`user_id`
order by `total_score` desc;

-- comment on column ranking_list.name not supported: 用户名

