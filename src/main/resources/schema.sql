-- Housekeeping 系统数据库表结构
-- 数据库: home_service
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci

-- ----------------------------------------------------
-- 用户表
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'member' COMMENT '角色：admin/manager/member',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/disabled',
    `is_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否实名认证',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `open_id` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `session_key` VARCHAR(100) DEFAULT NULL COMMENT '微信会话密钥',
    `union_id` VARCHAR(100) DEFAULT NULL COMMENT '微信UnionID',
    `is_admin` BOOLEAN DEFAULT FALSE COMMENT '是否管理员（已废弃）',
    `login_count` INT NOT NULL DEFAULT 0 COMMENT '登录次数',
    `last_login` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '软删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------------------------------
-- 任务表
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(100) NOT NULL COMMENT '任务标题',
    `description` TEXT NOT NULL COMMENT '任务描述',
    `type` VARCHAR(50) NOT NULL DEFAULT 'daily_task' COMMENT '任务类型：daily_task/special_task/maintenance',
    `status` VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '任务状态：pending/processing/completed/cancelled',
    `priority` VARCHAR(50) NOT NULL DEFAULT 'medium' COMMENT '优先级：low/medium/high/urgent',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '任务分类',
    `tags` JSON DEFAULT NULL COMMENT '标签（JSON数组）',
    `assignee_id` BIGINT DEFAULT NULL COMMENT '指派人ID',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `due_date` DATE DEFAULT NULL COMMENT '截止日期',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成日期',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '软删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_assignee_id` (`assignee_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`),
    KEY `idx_priority` (`priority`),
    KEY `idx_due_date` (`due_date`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ----------------------------------------------------
-- 订单表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `service_id` BIGINT NOT NULL COMMENT '服务ID',
    `status` VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '订单状态',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_service_id` (`service_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ----------------------------------------------------
-- 服务表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `service` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
    `description` TEXT COMMENT '服务描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '服务价格',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '服务图片',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务表';

-- ----------------------------------------------------
-- 评价表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `evaluate` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `rating` INT NOT NULL COMMENT '评分',
    `comment` TEXT COMMENT '评论内容',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ----------------------------------------------------
-- 会员表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `level` VARCHAR(50) NOT NULL DEFAULT 'normal' COMMENT '会员等级',
    `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- ----------------------------------------------------
-- 文件表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `filename` VARCHAR(255) NOT NULL COMMENT '文件名',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型',
    `upload_user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '文件分类',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_upload_user_id` (`upload_user_id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- ----------------------------------------------------
-- 菜单表（已有）
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
    `path` VARCHAR(100) DEFAULT NULL COMMENT '路由路径',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父菜单ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ----------------------------------------------------
-- 系统配置表
-- ----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_type` VARCHAR(50) DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------------------------------
-- 初始化数据
-- ----------------------------------------------------

-- 插入默认管理员用户
INSERT INTO `user` (`user_name`, `password`, `nickname`, `role`, `status`, `is_verified`, `login_count`, `created_at`)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    '超级管理员',
    'admin',
    'active',
    TRUE,
    1,
    NOW()
)
ON DUPLICATE KEY UPDATE `user_name` = `user_name`;

-- 插入测试用户
INSERT INTO `user` (`user_name`, `password`, `nickname`, `role`, `status`, `phone`, `created_at`)
VALUES (
    'testuser',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    '测试用户',
    'member',
    'active',
    '13800138000',
    NOW()
)
ON DUPLICATE KEY UPDATE `user_name` = `user_name`;

-- 插入示例任务数据
INSERT INTO `task` (`title`, `description`, `type`, `status`, `priority`, `category`, `assignee_id`, `creator_id`, `due_date`, `created_at`)
SELECT '系统维护任务', '对系统进行例行维护，包括数据库备份、日志清理等', 'maintenance', 'pending', 'high', '系统维护', 1, 1, DATE_ADD(NOW(), INTERVAL 5 DAY), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `task` WHERE `title` = '系统维护任务');

INSERT INTO `task` (`title`, `description`, `type`, `status`, `priority`, `category`, `assignee_id`, `creator_id`, `due_date`, `created_at`)
SELECT '更新项目文档', '根据最新要求更新项目相关文档，包括接口文档、用户手册等', 'daily_task', 'processing', 'high', '文档管理', 1, 1, DATE_ADD(NOW(), INTERVAL 10 DAY), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `task` WHERE `title` = '更新项目文档');

-- 插入系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`)
VALUES 
('site.name', 'Housekeeping管理系统', 'string', '站点名称'),
('site.version', '1.0.0', 'string', '系统版本'),
('maintenance', 'false', 'boolean', '维护模式'),
('registration.enabled', 'true', 'boolean', '是否允许注册'),
('features.sms-login', 'true', 'boolean', '短信登录功能'),
('features.wechat-login', 'true', 'boolean', '微信登录功能'),
('features.email-notification', 'true', 'boolean', '邮件通知功能')
ON DUPLICATE KEY UPDATE `config_key` = `config_key`;
