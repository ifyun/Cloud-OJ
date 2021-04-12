create database cloud_oj character set utf8mb4;
use cloud_oj;
set character set utf8;

create table contest
(
    contest_id   int auto_increment
        primary key,
    contest_name varchar(64)   null,
    start_at     timestamp     not null comment '开始时间',
    end_at       timestamp     not null comment '结束时间',
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
    score        int        default 0                 not null comment '分数',
    type         int        default 0                 not null comment '1 -> SQL',
    `sql`        text                                 null comment '正确的查询语句',
    enable       tinyint(1) default 0                 null comment '是否开放',
    category     varchar(32)                          null comment '分类，多个用逗号分隔',
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
    secret    char(8)                             not null comment '秘钥，用于生成 jwt',
    email     varchar(32)                         null,
    section   varchar(16)                         null,
    role_id   int       default 0                 not null,
    create_at timestamp default CURRENT_TIMESTAMP null,
    constraint user_role_role_id_fk
        foreign key (role_id) references role (role_id)
            on update cascade
);

create table solution
(
    solution_id char(36)                                                                not null
        primary key,
    problem_id  int                                                                     null,
    contest_id  int                                                                     null,
    language    int                                                                     not null,
    type        int                                           default 0                 not null,
    state       enum ('JUDGED', 'IN_JUDGE_QUEUE', 'ACCEPTED') default 'ACCEPTED'        not null,
    result      enum ('AC', 'TLE', 'MLE', 'PA', 'WA', 'CE', 'RE', 'IE', 'OLE')          null,
    pass_rate   double                                        default 0                 not null,
    user_id     varchar(32)                                                             null,
    submit_time datetime                                      default CURRENT_TIMESTAMP not null,
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

create table compile
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
    code_id     int auto_increment
        primary key,
    solution_id char(36) not null,
    code        text     not null,
    constraint source_code_solution_solution_id_fk
        foreign key (solution_id) references solution (solution_id)
            on update cascade on delete cascade
);

create index user_name_index
    on user (name);

create index user_section_index
    on user (section);

DELIMITER //
create trigger tr_user_insert
    before insert
    on user
    for each row
begin
    set NEW.secret = LEFT(UUID(), 8);
end //

create trigger tr_user_update
    before update
    on user
    for each row
begin
    if OLD.role_id <> NEW.role_id or OLD.password <> NEW.password
    then
        set NEW.secret = LEFT(UUID(), 8);
    end if;
end //
DELIMITER ;

create view contest_problem as
select `c`.`contest_id`   AS `contest_id`,
       `c`.`contest_name` AS `contest_name`,
       `c`.`start_at`     AS `start_at`,
       `c`.`end_at`       AS `end_at`,
       `c`.`languages`    AS `languages`,
       `p`.`problem_id`   AS `problem_id`,
       `p`.`title`        AS `title`,
       `p`.`type`         AS `type`,
       `p`.`description`  AS `description`,
       `p`.`timeout`      AS `timeout`,
       `p`.`memory_limit` AS `memory_limit`,
       `p`.`score`        AS `score`,
       `p`.`category`     AS `category`,
       `p`.`create_at`    AS `create_at`
from ((`cloud_oj`.`contest-problem` `cp` join `cloud_oj`.`problem` `p` on ((`cp`.`problem_id` = `p`.`problem_id`)))
         join `cloud_oj`.`contest` `c` on ((`cp`.`contest_id` = `c`.`contest_id`)));

create view contest_ranking_list as
select `problem_score`.`user_id`                                    AS `user_id`,
       `problem_score`.`name`                                       AS `name`,
       sum(`problem_score`.`committed`)                             AS `committed`,
       count((case when (`problem_score`.`result` = 1) then 1 end)) AS `passed`,
       truncate(sum(`problem_score`.`score`), 2)                    AS `total_score`,
       `problem_score`.`contest_id`                                 AS `contest_id`,
       `problem_score`.`contest_name`                               AS `contest_name`
from (select `s`.`user_id`                        AS `user_id`,
             `u`.`name`                           AS `name`,
             max(`s`.`pass_rate`)                 AS `result`,
             count(`p`.`problem_id`)              AS `committed`,
             (max(`s`.`pass_rate`) * `p`.`score`) AS `score`,
             `s`.`contest_id`                     AS `contest_id`,
             `c`.`contest_name`                   AS `contest_name`
      from (((`cloud_oj`.`solution` `s` join `cloud_oj`.`user` `u` on (((`s`.`user_id` = `u`.`user_id`) and (`u`.`role_id` = 0)))) join `cloud_oj`.`problem` `p` on ((`s`.`problem_id` = `p`.`problem_id`)))
               join `cloud_oj`.`contest` `c` on ((`s`.`contest_id` = `c`.`contest_id`)))
      group by `s`.`user_id`, `p`.`problem_id`, `s`.`contest_id`) `problem_score`
group by `problem_score`.`user_id`, `problem_score`.`contest_id`;

create view ranking_list as
select `problem_score`.`user_id`                                    AS `user_id`,
       `problem_score`.`name`                                       AS `name`,
       sum(`problem_score`.`committed`)                             AS `committed`,
       count((case when (`problem_score`.`result` = 1) then 1 end)) AS `passed`,
       truncate(sum(`problem_score`.`score`), 2)                    AS `total_score`
from (select `s`.`user_id`                        AS `user_id`,
             `u`.`name`                           AS `name`,
             max(`s`.`pass_rate`)                 AS `result`,
             count(`p`.`problem_id`)              AS `committed`,
             (max(`s`.`pass_rate`) * `p`.`score`) AS `score`
      from ((`cloud_oj`.`solution` `s` join `cloud_oj`.`user` `u` on (((`s`.`user_id` = `u`.`user_id`) and (`u`.`role_id` = 0))))
               join `cloud_oj`.`problem` `p` on ((`s`.`problem_id` = `p`.`problem_id`)))
      where (`s`.`contest_id` is null)
      group by `s`.`user_id`, `p`.`problem_id`) `problem_score`
group by `problem_score`.`user_id`;

create view judge_result as
select `s`.`solution_id`                                      AS `solution_id`,
       `s`.`problem_id`                                       AS `problem_id`,
       `s`.`contest_id`                                       AS `contest_id`,
       `p`.`title`                                            AS `title`,
       `s`.`type`                                             AS `type`,
       `s`.`language`                                         AS `language`,
       `s`.`state`                                            AS `state`,
       `s`.`result`                                           AS `result`,
       truncate(`s`.`pass_rate`, 2)                           AS `pass_rate`,
       `s`.`user_id`                                          AS `user_id`,
       `s`.`submit_time`                                      AS `submit_time`,
       truncate((`s`.`pass_rate` * `p`.`score`), 1)           AS `score`,
       `sc`.`code`                                            AS `code`,
       `r`.`time`                                             AS `time`,
       `r`.`memory`                                           AS `memory`,
       concat(ifnull(`c`.`info`, ''), ifnull(`r`.`info`, '')) AS `error_info`
from ((((`cloud_oj`.`solution` `s` join `cloud_oj`.`problem` `p` on ((`s`.`problem_id` = `p`.`problem_id`))) join `cloud_oj`.`compile` `c` on ((`s`.`solution_id` = `c`.`solution_id`))) left join `cloud_oj`.`runtime` `r` on ((`s`.`solution_id` = `r`.`solution_id`)))
         join `cloud_oj`.`source_code` `sc` on ((`s`.`solution_id` = `sc`.`solution_id`)))
order by `s`.`submit_time`;

-- 初始化角色/权限表
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (0, 'ROLE_USER');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (1, 'ROLE_USER_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (2, 'ROLE_PROBLEM_ADMIN');
INSERT INTO cloud_oj.role (role_id, role_name)
VALUES (3, 'ROLE_ROOT');

-- 初始 ROOT 用户
INSERT INTO cloud_oj.user (user_id, name, password, secret, role_id)
VALUES ('root', '初始管理员', '$2a$10$79exZxOfiSAtHcyCXSfjMeH5GYgMwUhexc.3ZXqbuxLaHVhp05LTi', LEFT(UUID(), 8), 3);

ALTER TABLE problem
    AUTO_INCREMENT = 1000