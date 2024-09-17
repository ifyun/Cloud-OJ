create database cloud_oj character set utf8mb4;
set character set utf8mb4;
use cloud_oj;

# 题目
create table problem
(
    problem_id   int auto_increment primary key      not null,
    title        char(64)                            not null,
    description  text                                not null comment '题目描述',
    timeout      bigint     default 100              null comment 'CPU 时间限制(ms)',
    memory_limit int        default 16               not null comment '内存限制(MiB)',
    output_limit int        default 16               not null comment '输出限制(MiB)',
    score        int        default 0                not null comment '分数',
    enable       tinyint(1) default 0                null comment '是否开放',
    category     char(128)                           null comment '分类, 逗号分隔',
    create_at    bigint     default unix_timestamp() not null comment '秒级',
    deleted      tinyint(1) default 0                not null
);

create index idx_title on problem (title);
create index idx_time on problem (create_at);
create index idx_enable on problem (enable);
create index idx_category on problem (category);

# 测试数据配置
create table data_conf
(
    problem_id int primary key not null,
    conf       json            null comment '每个测试点的分数配置'
);

# 竞赛
create table contest
(
    contest_id   int auto_increment primary key      not null,
    contest_name char(64)                            not null,
    invite_key   char(8)                             not null comment '邀请码',
    start_at     bigint                              not null comment '开始时间，10 位 UNIX',
    end_at       bigint                              not null comment '结束时间，10 位 UNIX',
    languages    int        default 0                not null comment '允许的语言',
    create_at    bigint     default unix_timestamp() not null comment '秒级',
    deleted      tinyint(1) default 0                not null
);

# 竞赛 <-> 题目
create table `contest-problem`
(
    contest_id int not null,
    problem_id int null,
    `order`    int not null default 0,
    primary key (contest_id, problem_id)
);

create index idx_cid on `contest-problem` (contest_id);
create index idx_pid on `contest-problem` (problem_id);
create index idx_order on `contest-problem` (`order`);

# 竞赛 <-> 用户
create table invitee
(
    contest_id int not null,
    uid        int not null,
    primary key (contest_id, uid)
);

# 用户
create table user
(
    uid        int auto_increment primary key      not null,
    username   char(32)                            not null,
    nickname   char(16)                            not null,
    password   char(100)                           not null,
    secret     char(36)                            not null comment '用于加密 JWT',
    email      char(32)                            null,
    section    char(16)                            null,
    has_avatar tinyint(1) default 0                not null,
    star       tinyint(1) default 0                not null,
    role       int        default 1                not null,
    create_at  bigint     default unix_timestamp() not null comment '秒级',
    deleted    tinyint(1) default 0                not null,
    constraint idx_username
        unique (username)
);

create index idx_nickname on user (nickname);
create index idx_time on user (create_at);

# 提交
create table solution
(
    solution_id bigint unsigned primary key auto_increment                     not null,
    uid         int                                                            not null,
    problem_id  int                                                            not null,
    contest_id  int                                                            null,
    title       char(64)                                                       not null,
    language    int                                                            not null,
    state       enum ('JUDGED', 'RUNNING', 'COMPILING', 'WAITING')             not null,
    result      enum ('AC', 'TLE', 'MLE', 'PA', 'WA', 'CE', 'RE', 'IE', 'OLE') null,
    total       int        default 0                                           not null comment '总测试点数量',
    passed      int        default 0                                           not null comment '通过测试点数量',
    pass_rate   double     default 0                                           not null comment '通过率',
    score       double     default 0                                           not null comment '得分',
    time        bigint     default 0                                           not null comment 'CPU 时间(μs)',
    memory      bigint     default 0                                           not null comment '内存占用(KiB)',
    error_info  text                                                           null comment '错误信息(RE,IE)',
    submit_time bigint                                                         not null comment '毫秒级',
    deleted     tinyint(1) default 0                                           not null
);

create index idx_uid on solution (uid);
create index idx_pid on solution (problem_id);
create index idx_cid on solution (contest_id);
create index idx_time on solution (submit_time desc);

# 代码
create table source_code
(
    solution_id bigint unsigned primary key not null,
    code        text                        not null
);

# 排名
create table scoreboard
(
    uid         int primary key      not null,
    committed   int        default 0 not null comment '提交次数',
    passed      int        default 0 not null comment 'AC题目数量',
    score       double     default 0 not null comment '总分',
    update_time bigint               not null comment '毫秒级',
    deleted     tinyint(1) default 0 not null
);

create index idx_score_time on scoreboard (score desc, update_time asc);

# 竞赛排名
create table scoreboard_contest
(
    uid         int                  not null,
    contest_id  int                  not null,
    committed   int        default 0 not null,
    passed      int        default 0 not null,
    score       double     default 0 not null,
    update_time bigint               not null comment '毫秒级',
    deleted     tinyint(1) default 0 not null,
    primary key (uid, contest_id)
);

create index idx_cid_score_time on scoreboard_contest (contest_id, score desc, update_time asc);

create table settings
(
    id                  int primary key      not null,
    always_show_ranking tinyint(1) default 0 not null,
    show_all_contest    tinyint(1) default 0 not null,
    show_passed_points  tinyint(1) default 0 not null,
    auto_del_solutions  tinyint(1) default 0 not null
);

create table log
(
    id          bigint unsigned primary key auto_increment,
    service     char(16) comment '服务名称',
    instance_id char(36) comment '实例 ID',
    level       char(8)  not null comment '日志级别',
    thread      char(36) not null comment '线程名称',
    className   tinytext not null comment '类名',
    message     text     not null,
    time        bigint   not null
) engine = Aria;

create index idx_level on log(level);
create index idx_time on log (time);

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
       `p`.`create_at`    AS `create_at`,
       `cp`.`order`       AS `order`
from ((`cloud_oj`.`contest-problem` `cp` join `cloud_oj`.`problem` `p` on (`cp`.`problem_id` = `p`.`problem_id`))
    join `cloud_oj`.`contest` `c` on (`c`.`deleted` = 0 and `cp`.`contest_id` = `c`.`contest_id`));

ALTER TABLE problem
    AUTO_INCREMENT = 1000;

ALTER TABLE solution
    AUTO_INCREMENT = 10000;

ALTER TABLE user
    AUTO_INCREMENT = 100000;

# 初始 ADMIN 用户
INSERT INTO cloud_oj.user (uid, username, nickname, password, secret, role)
VALUES (1, 'admin', '管理员', '$2a$10$i8D62CjX7/.z8juoUACG9ecqatDl9JkizB5XoA9UswPtb8WmnCAG6', '', 0);

INSERT INTO settings (id)
VALUES (0);
