create database cloud_oj character set utf8mb4;
use cloud_oj;
set character set utf8mb4;

# 题目
create table problem
(
    problem_id   int unsigned auto_increment primary key,
    title        varchar(64)                          not null,
    description  text                                 not null,
    timeout      bigint     default 100               null comment 'CPU 时间限制(ms)',
    memory_limit int        default 16                not null comment '内存限制(MiB)',
    output_limit int        default 16                not null comment '输出限制(MiB)',
    score        int        default 0                 not null comment '分数',
    enable       tinyint(1) default 0                 null comment '是否开放',
    category     varchar(128)                         null comment '分类, 逗号分隔',
    create_at    datetime   default CURRENT_TIMESTAMP not null,
    deleted      tinyint    default 0                 not null
);

create index idx_title on problem (title);
create index idx_time on problem (create_at);
create index idx_enable on problem (enable);

# 竞赛
create table contest
(
    contest_id   int unsigned auto_increment primary key,
    contest_name varchar(64)                     null,
    start_at     bigint                          not null comment '开始时间',
    end_at       bigint                          not null comment '结束时间',
    languages    int        default 0            not null comment '允许的语言',
    create_at    datetime   default CURRENT_TIME not null,
    deleted      tinyint(1) default 0            not null
);

create table `contest-problem`
(
    contest_id int unsigned not null,
    problem_id int unsigned null,
    primary key (contest_id, problem_id)
) engine = Aria;

create index idx_cid on `contest-problem` (contest_id);
create index idx_pid on `contest-problem` (problem_id);

create table role
(
    role_id   int unsigned not null primary key,
    role_name varchar(32)  not null
);

create table user
(
    user_id    varchar(32)                            not null primary key,
    name       varchar(16)                            not null,
    password   varchar(100)                           not null,
    secret     char(36)                               not null,
    email      varchar(32)                            null,
    section    varchar(16)                            null,
    has_avatar tinyint(1)   default 0                 not null,
    role_id    int unsigned default 1                 not null,
    create_at  timestamp    default CURRENT_TIMESTAMP not null,
    deleted    tinyint(1)   default 0                 not null
);

create index idx_name on user (name);
create index idx_time on user (create_at);

# 提交
create table solution
(
    solution_id char(36)                                                       not null primary key,
    user_id     varchar(32)                                                    not null,
    title       varchar(64)                                                    not null,
    problem_id  int unsigned                                                   not null,
    contest_id  int unsigned                                                   null,
    language    int unsigned                                                   not null,
    state       enum ('JUDGED', 'IN_QUEUE')                                    not null,
    result      enum ('AC', 'TLE', 'MLE', 'PA', 'WA', 'CE', 'RE', 'IE', 'OLE') null,
    pass_rate   double     default 0                                           not null,
    score       double     default 0                                           not null,
    submit_time datetime   default CURRENT_TIMESTAMP                           not null,
    deleted     tinyint(1) default 0                                           not null
);

create index idx_uid on solution (user_id);
create index idx_pid on solution (problem_id);
create index idx_cid on solution (contest_id);
create index idx_time on solution (submit_time desc);

# 排名
create table leaderboard
(
    user_id     varchar(32)                          not null primary key,
    committed   int        default 0                 not null,
    passed      int        default 0                 not null,
    score       double     default 0                 not null,
    update_time timestamp  default CURRENT_TIMESTAMP not null,
    deleted     tinyint(1) default 0                 not null
);

create index idx_score_time on leaderboard (score desc, update_time asc);

# 竞赛排名
create table leaderboard_contest
(
    user_id     varchar(32)                          not null primary key,
    contest_id  int unsigned                         not null,
    committed   int        default 0                 not null,
    passed      int        default 0                 not null,
    score       int        default 0                 not null,
    update_time timestamp  default CURRENT_TIMESTAMP not null,
    deleted     tinyint(1) default 0                 not null
);

create index idx_cid_score_time on leaderboard_contest (contest_id, score desc, update_time asc);

# 编译信息
create table if not exists compile
(
    id          int unsigned auto_increment primary key,
    solution_id char(36) not null,
    state       int      not null comment '0: 编译成功, -1: 编译出错',
    info        text     null
);

create index idx_sid on compile (solution_id);

# 运行时信息
create table runtime
(
    id          int unsigned auto_increment primary key,
    solution_id char(36) not null,
    total       int      null comment '总测试点数量',
    passed      int      null comment '通过测试点数量',
    time        bigint   null comment 'CPU 时间(μs)',
    memory      bigint   null comment '内存占用(KiB)',
    info        text     null
);

create index idx_sid on runtime (solution_id);

# 代码
create table source_code
(
    id          int unsigned auto_increment primary key,
    solution_id char(36) not null,
    code        text     not null
);

create index idx_sid on source_code (solution_id);

create table settings
(
    id                  int unsigned         not null primary key,
    always_show_ranking tinyint(1) default 0 not null,
    show_all_contest    tinyint(1) default 0 not null,
    show_passed_points  tinyint(1) default 0 not null
) engine = Aria;

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
from ((`cloud_oj`.`contest-problem` `cp` join `cloud_oj`.`problem` `p` on (`cp`.`problem_id` = `p`.`problem_id`))
    join `cloud_oj`.`contest` `c` on (`c`.`deleted` = 0 and `cp`.`contest_id` = `c`.`contest_id`));

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
       `r`.`time`                                             AS `time`,
       `r`.`memory`                                           AS `memory`,
       `r`.`passed`                                           AS `passed`,
       `r`.`total`                                            AS `total`,
       concat(ifnull(`c`.`info`, ''), ifnull(`r`.`info`, '')) AS `error_info`
from ((`cloud_oj`.`solution` `s` left join `cloud_oj`.`compile` `c`
       on (`s`.`solution_id` = `c`.`solution_id`)) left join `cloud_oj`.`runtime` `r`
      on (`s`.`solution_id` = `r`.`solution_id`))
where `s`.`deleted` = 0
order by `s`.`submit_time` desc;

ALTER TABLE problem
    AUTO_INCREMENT = 1000;

# 初始化角色/权限表
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (0, 'ROLE_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (1, 'ROLE_USER');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (2, 'ROLE_USER_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (3, 'ROLE_PROBLEM_ADMIN');

# 初始 ADMIN 用户
INSERT INTO cloud_oj.user (user_id, name, password, secret, role_id)
VALUES ('admin', '管理员', '$2a$10$i8D62CjX7/.z8juoUACG9ecqatDl9JkizB5XoA9UswPtb8WmnCAG6', '', 0);

INSERT INTO settings (id)
VALUES (0);
