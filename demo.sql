CREATE DATABASE IF NOT EXISTS demo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE demo;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username`   VARCHAR(50)  NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 部门表
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`
(
    `id`         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(50) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 员工表
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`
(
    `id`            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username`      VARCHAR(50)      NOT NULL,
    `password`      VARCHAR(255)     NOT NULL DEFAULT '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K',
    `real_name`     VARCHAR(50)      NOT NULL,
    `gender`        TINYINT UNSIGNED NOT NULL COMMENT '0: 未知, 1: 男, 2: 女',
    `avatar`        VARCHAR(255)     NOT NULL,
    `job`           TINYINT UNSIGNED NOT NULL COMMENT '1: 班主任, 2: 讲师, 3: 学工主管, 4: 教研主管, 5: 咨询师',
    `department_id` INT UNSIGNED     NOT NULL,
    `entry_date`    DATE             NOT NULL,
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 班级表
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`
(
    `id`         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(50) NOT NULL,
    `grade`      VARCHAR(50) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 学生表
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`
(
    `id`         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(50)  NOT NULL,
    `gender`     TINYINT      NOT NULL COMMENT '0: 未知, 1: 男, 2: 女',
    `birth_date` DATE         NOT NULL,
    `class_id`   INT UNSIGNED NOT NULL,
    `join_date`  DATE         NOT NULL,
    `status`     TINYINT      NOT NULL COMMENT '1: 在读, 0: 毕业',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `user` (`username`, `password`)
VALUES ('admin', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K');

INSERT INTO `department` (`name`)
VALUES ('语文组'),
       ('数学组'),
       ('英语组'),
       ('信息技术组'),
       ('物理组'),
       ('化学组'),
       ('政治组'),
       ('历史组');

INSERT INTO `employee`
(`username`, `password`, `real_name`, `gender`, `avatar`, `job`, `department_id`, `entry_date`)
VALUES ('lihua', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '李华', 1,
        'https://github.com/lihua.png', 1, 1, '2015-08-20'),
       ('zhangyan', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '张燕', 2,
        'https://github.com/zhangyan.png', 2, 2, '2016-09-01'),
       ('wangming', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '王明', 1,
        'https://github.com/wangming.png', 3, 3, '2014-03-15'),
       ('zhaolei', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '赵磊', 1,
        'https://github.com/zhaolei.png', 4, 4, '2019-11-30'),
       ('sunli', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '孙丽', 2,
        'https://github.com/sunli.png', 5, 5, '2020-03-10'),
       ('hewei', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '何伟', 1,
        'https://github.com/hewei.png', 4, 6, '2018-05-25'),
       ('yangjing', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '杨静', 2,
        'https://github.com/yangjing.png', 3, 7, '2017-10-12'),
       ('liujie', '$2a$10$zYlOFfXC1XuCY1kt7Oc2m.QYAhQtvCasg8Bnq/MJ9KXhYEFoCs28K', '刘杰', 1,
        'https://github.com/liujie.png', 2, 8, '2019-09-01');

INSERT INTO `class` (`name`, `grade`)
VALUES ('高一(1)班', '高一'),
       ('高一(2)班', '高一'),
       ('高二(1)班', '高二'),
       ('高二(2)班', '高二'),
       ('高三(1)班', '高三'),
       ('高三(2)班', '高三');

INSERT INTO `student`
    (`name`, `gender`, `birth_date`, `class_id`, `join_date`, `status`)
VALUES ('陈晨', 1, '2007-05-21', 1, '2023-09-01', 1),
       ('刘颖', 2, '2007-08-15', 1, '2023-09-01', 1),
       ('孙浩', 1, '2006-11-30', 2, '2023-09-01', 1),
       ('周倩', 2, '2006-02-12', 2, '2023-09-01', 1),
       ('何杰', 1, '2005-06-18', 3, '2022-09-01', 1),
       ('吴婷', 2, '2005-07-10', 3, '2022-09-01', 1),
       ('唐磊', 1, '2004-09-25', 4, '2022-09-01', 1),
       ('林芳', 2, '2004-10-03', 4, '2022-09-01', 1),
       ('罗强', 1, '2003-12-01', 5, '2021-09-01', 0),
       ('杨雪', 2, '2003-11-08', 5, '2021-09-01', 0),
       ('张亮', 1, '2004-01-17', 6, '2021-09-01', 0),
       ('高慧', 2, '2004-02-21', 6, '2021-09-01', 0),
       ('赵宁', 1, '2007-03-10', 1, '2023-09-01', 1),
       ('丁芳', 2, '2007-04-18', 1, '2023-09-01', 1),
       ('魏涛', 1, '2006-06-09', 2, '2023-09-01', 1),
       ('许琴', 2, '2006-07-23', 2, '2023-09-01', 1),
       ('潘凯', 1, '2005-10-01', 3, '2022-09-01', 1),
       ('马丽', 2, '2005-11-14', 3, '2022-09-01', 1),
       ('郑刚', 1, '2004-08-08', 4, '2022-09-01', 1),
       ('钱蕾', 2, '2004-12-12', 4, '2022-09-01', 1);

-- 公告表
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`
(
    `id`           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `title`        VARCHAR(200)     NOT NULL COMMENT '公告标题',
    `content`      TEXT             NOT NULL COMMENT '公告内容',
    `type`         TINYINT UNSIGNED NOT NULL COMMENT '公告类型：1-系统公告, 2-活动公告, 3-通知公告',
    `status`       TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-草稿, 1-已发布, 2-已下线',
    `priority`     TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '优先级：1-普通, 2-重要, 3-紧急',
    `publish_time` DATETIME         NULL COMMENT '发布时间',
    `expire_time`  DATETIME         NULL COMMENT '过期时间',
    `creator_id`   INT UNSIGNED     NOT NULL COMMENT '创建人ID',
    `created_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `announcement` (`title`, `content`, `type`, `status`, `priority`, `publish_time`, `expire_time`,
                            `creator_id`)
VALUES ('系统维护通知', '系统将于本周六晚上10点进行维护，预计维护时间2小时，请提前保存工作。', 1, 1, 2,
        '2024-01-15 09:00:00', null, 1),
       ('新学期开学通知', '新学期将于2月20日正式开学，请各位同学做好开学准备。', 3, 1, 3, '2024-02-01 08:00:00', null, 1),
       ('春节放假安排', '根据国家规定，春节放假时间为2月10日至2月17日，共8天。', 2, 1, 1, '2024-01-25 10:00:00', null, 1);

-- 请求日志表
DROP TABLE IF EXISTS `request_log`;
CREATE TABLE `request_log`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id`         INT UNSIGNED    NULL COMMENT '用户ID',
    `username`        VARCHAR(50)     NULL COMMENT '用户名',
    `request_method`  VARCHAR(10)     NOT NULL COMMENT '请求方法',
    `request_url`     VARCHAR(500)    NOT NULL COMMENT '请求URL',
    `request_params`  TEXT            NULL COMMENT '请求参数',
    `request_body`    TEXT            NULL COMMENT '请求体',
    `response_status` INT             NOT NULL COMMENT '响应状态码',
    `response_time`   BIGINT UNSIGNED NOT NULL COMMENT '响应时间(毫秒)',
    `ip_address`      VARCHAR(50)     NOT NULL COMMENT 'IP地址',
    `user_agent`      VARCHAR(500)    NULL COMMENT '用户代理',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 网盘文件表
DROP TABLE IF EXISTS `netdisk_file`;
CREATE TABLE `netdisk_file`
(
    `id`          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id`     INT UNSIGNED NOT NULL COMMENT '用户ID',
    `file_name`   VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_size`   BIGINT       NOT NULL COMMENT '文件大小(字节)',
    `file_type`   VARCHAR(50)  NOT NULL COMMENT '文件类型',
    `file_url`    VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;