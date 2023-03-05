create database cloud_oj character set utf8mb4;
use cloud_oj;
set character set utf8mb4;

create table contest
(
    contest_id   int auto_increment
        primary key,
    contest_name varchar(64)   null,
    start_at     bigint        not null comment '开始时间',
    end_at       bigint        not null comment '结束时间',
    languages    int default 0 not null comment '支持的语言范围'
);

create table problem
(
    problem_id   int auto_increment
        primary key,
    title        varchar(64)                          not null,
    description  text                                 not null comment '题目描述',
    timeout      bigint     default 1000              null comment '时间限制',
    memory_limit int        default 64                not null comment '内存限制',
    output_limit int        default 128               not null comment '输出限制',
    score        int        default 0                 not null comment '分数',
    enable       tinyint(1) default 0                 null comment '是否开放',
    category     varchar(64)                          null comment '分类，多个用逗号分隔',
    create_at    datetime   default CURRENT_TIMESTAMP not null comment '创建时间'
);

create table `contest-problem`
(
    contest_id int null,
    problem_id int null,
    constraint `contest-problem_contest_contest_id_fk`
        foreign key (contest_id) references contest (contest_id)
            on update cascade,
    constraint `contest-problem_problem_problem_id_fk`
        foreign key (problem_id) references problem (problem_id)
            on update cascade
);

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
    user_id   varchar(32)                         not null
        primary key,
    name      varchar(16)                         not null,
    password  varchar(100)                        not null,
    secret    char(36)                            not null comment '秘钥，用于生成 jwt',
    email     varchar(32)                         null,
    section   varchar(16)                         null,
    role_id   int       default 1                 not null,
    create_at timestamp default CURRENT_TIMESTAMP null,
    constraint user_role_role_id_fk
        foreign key (role_id) references role (role_id)
            on update cascade
);

create index user_name_index
    on user (name);

create index user_create_at_index
    on user (create_at);

create table solution
(
    solution_id char(36)                                                                not null
        primary key,
    problem_id  int                                                                     null,
    contest_id  int                                                                     null,
    title       varchar(64)                                                             not null,
    language    int                                                                     not null,
    state       enum ('JUDGED', 'IN_JUDGE_QUEUE', 'ACCEPTED') default 'ACCEPTED'        not null,
    result      enum ('AC', 'TLE', 'MLE', 'PA', 'WA', 'CE', 'RE', 'IE', 'OLE')          null,
    pass_rate   double                                        default 0                 not null comment '通过率',
    score       double                                        default 0                 not null,
    user_id     varchar(32)                                                             null,
    submit_time datetime                                      default CURRENT_TIMESTAMP not null comment '提交时间',
    constraint solution_contest_contest_id_fk
        foreign key (contest_id) references contest (contest_id)
            on update cascade,
    constraint solution_problem_problem_id_fk
        foreign key (problem_id) references problem (problem_id)
            on update cascade on delete cascade,
    constraint solution_user_user_id_fk
        foreign key (user_id) references user (user_id)
            on update cascade
);

create index solution_score_index
    on solution (score);

-- 排名
create table leaderboard
(
    user_id     varchar(32)                         not null
        primary key,
    committed   int       default 0                 not null,
    passed      int       default 0                 not null,
    score       double    default 0                 not null,
    update_time timestamp default CURRENT_TIMESTAMP not null
);

create index leaderboard_index
    on leaderboard (score desc, update_time asc);

-- 竞赛排名
create table leaderboard_contest
(
    user_id     varchar(32)                         not null
        primary key,
    contest_id  int                                 not null,
    committed   int       default 0                 not null,
    passed      int       default 0                 not null,
    score       int       default 0                 not null,
    update_time timestamp default CURRENT_TIMESTAMP null
);

create index leaderboard_contest_index
    on leaderboard_contest (contest_id, score desc, update_time asc);

create table if not exists compile
(
    id          int auto_increment
        primary key,
    solution_id char(36) not null,
    state       int      not null comment '编译状态(0->编译成功,-1->编译出错)',
    info        text     null,
    constraint compile_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
);

create table runtime
(
    id          int auto_increment
        primary key,
    solution_id char(36) not null,
    total       int      null comment '总测试点数量',
    passed      int      null comment '通过的测试点数量',
    time        bigint   null comment '耗时(ms)',
    memory      bigint   null comment '内存占用(KB)',
    info        text     null,
    constraint runtime_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
);

create table source_code
(
    id          int auto_increment
        primary key,
    solution_id char(36) not null,
    code        text     not null,
    constraint source_code_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
);

create view contest_problem as
select `c`.`contest_id`   AS `contest_id`,
       `c`.`contest_name` AS `contest_name`,
       `c`.`start_at`     AS `start_at`,
       `c`.`end_at`       AS `end_at`,
       `c`.`languages`    AS `languages`,
       `p`.`problem_id`   AS `problem_id`,
       `p`.`title`        AS `title`,
       `p`.`description`  AS `description`,
       `p`.`timeout`      AS `timeout`,
       `p`.`memory_limit` AS `memory_limit`,
       `p`.`output_limit` AS `output_limit`,
       `p`.`score`        AS `score`,
       `p`.`category`     AS `category`,
       `p`.`create_at`    AS `create_at`
from ((`cloud_oj`.`contest-problem` `cp` join `cloud_oj`.`problem` `p` on ((`cp`.`problem_id` = `p`.`problem_id`)))
    join `cloud_oj`.`contest` `c` on ((`cp`.`contest_id` = `c`.`contest_id`)));

create view judge_result as
select `s`.`solution_id`                                      AS `solution_id`,
       `s`.`problem_id`                                       AS `problem_id`,
       `s`.`contest_id`                                       AS `contest_id`,
       `s`.`title`                                            AS `title`,
       `s`.`language`                                         AS `language`,
       `s`.`state`                                            AS `state`,
       `s`.`result`                                           AS `result`,
       truncate(`s`.`pass_rate`, 2)                           AS `pass_rate`,
       `s`.`user_id`                                          AS `user_id`,
       `s`.`submit_time`                                      AS `submit_time`,
       truncate(`s`.`score`, 2)                               AS `score`,
       `sc`.`code`                                            AS `code`,
       `r`.`time`                                             AS `time`,
       `r`.`memory`                                           AS `memory`,
       concat(ifnull(`c`.`info`, ''), ifnull(`r`.`info`, '')) AS `error_info`
from ((((`cloud_oj`.`solution` `s` left join `cloud_oj`.`compile` `c`
         on ((`s`.`solution_id` = `c`.`solution_id`))) left join `cloud_oj`.`runtime` `r`
        on ((`s`.`solution_id` = `r`.`solution_id`)))
    left join `cloud_oj`.`source_code` `sc` on ((`s`.`solution_id` = `sc`.`solution_id`))))
order by `s`.`submit_time`;

create table settings
(
    id                       int                    not null
        primary key,
    icp                      varchar(64) default '' not null,
    icp_url                  varchar(64) default '' not null,
    site_name                varchar(16) default '' not null,
    show_ranking_after_ended tinyint(1)  default 0  null,
    show_not_started_contest tinyint(1)  default 0  null
);

ALTER TABLE problem
    AUTO_INCREMENT = 1000;

-- 初始化角色/权限表
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (0, 'ROLE_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (1, 'ROLE_USER');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (2, 'ROLE_USER_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (3, 'ROLE_PROBLEM_ADMIN');

-- 初始 ADMIN 用户
INSERT INTO cloud_oj.user (user_id, name, password, secret, role_id)
VALUES ('admin', '管理员', '$2a$10$i8D62CjX7/.z8juoUACG9ecqatDl9JkizB5XoA9UswPtb8WmnCAG6', UUID(), 0);

INSERT INTO settings (id)
VALUES (0);
