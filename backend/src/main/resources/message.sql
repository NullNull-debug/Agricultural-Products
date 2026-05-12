-- 消息系统数据库表结构
-- 创建消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '消息类型: system-系统通知, announcement-系统公告, order-订单物流, private-私信, service-客服消息, promotion-优惠活动',
  `name` VARCHAR(100) NOT NULL COMMENT '消息名称',
  `content` TEXT COMMENT '消息内容（文本类消息）',
  `preview` VARCHAR(200) COMMENT '消息预览',
  `time` VARCHAR(50) COMMENT '消息时间描述',
  `unread_count` INT NOT NULL DEFAULT 0 COMMENT '未读数量',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 0-已删除, 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

-- 创建消息会话表（用于私信和客服消息）
CREATE TABLE IF NOT EXISTS `message_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `session_type` VARCHAR(20) NOT NULL COMMENT '会话类型: private-私信, service-客服',
  `target_name` VARCHAR(100) NOT NULL COMMENT '会话对象名称',
  `target_id` BIGINT COMMENT '会话对象ID（客服ID等）',
  `last_message` VARCHAR(200) COMMENT '最后一条消息预览',
  `last_time` DATETIME COMMENT '最后消息时间',
  `unread_count` INT NOT NULL DEFAULT 0 COMMENT '未读消息数',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 0-已删除, 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_session` (`user_id`, `session_type`, `target_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_target_id` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息会话表';

-- 创建聊天消息内容表
CREATE TABLE IF NOT EXISTS `message_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL COMMENT '会话ID',
  `user_id` BIGINT NOT NULL COMMENT '发送用户ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_self` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否自己发送: 0-否, 1-是',
  `message_time` DATETIME NOT NULL COMMENT '消息时间',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 0-已删除, 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_session_id` (`session_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_message_time` (`message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='聊天消息内容表';

-- 插入示例数据

-- 插入消息数据（给userId=1）
INSERT INTO `message` (`user_id`, `type`, `name`, `content`, `preview`, `time`, `unread_count`) VALUES
(1, 'announcement', '系统公告', '【系统维护通知】\n\n尊敬的用户：\n\n为了提升平台服务质量，我们将进行系统维护升级。\n\n维护时间：今晚22:00-24:00\n维护范围：全部功能\n\n请提前做好安排，给您带来不便敬请谅解！\n\n特色农产品销售平台\n2026年5月11日', '平台将于今晚22:00-24:00进行系统维护', '5分钟前', 0),
(1, 'system', '账号安全', '【账号安全提醒】\n\n检测到您的账号在新设备登录：\n\n登录地点：北京市\n登录时间：2026年5月11日 10:30\n设备信息：Chrome浏览器\n\n如果不是您本人操作，请立即修改密码或联系客服。', '检测到新设备登录，登录地点：北京市', '1小时前', 1),
(1, 'order', '售后结果', '【退款通知】\n\n您好！\n\n您的退款申请已审核通过：\n\n订单编号：DD20260510001\n退款金额：￥158.00\n退款方式：原路返回\n预计到账：1-3个工作日\n\n感谢您的理解与支持！', '您的退款申请已通过，预计1-3个工作日到账', '3小时前', 2),
(1, 'promotion', '优惠活动', '【新用户专享优惠】\n\n新用户首单福利来啦！\n\n活动内容：\n- 首单满100元减20元\n- 精选商品5折起\n- 免费包邮到家\n\n活动时间：即日起至5月31日\n适用人群：新注册用户\n\n点击立即领取优惠券 >>', '新用户首单满100减20，快来选购吧！', '昨天', 0);

-- 插入私信会话数据（给userId=1）
INSERT INTO `message_session` (`user_id`, `session_type`, `target_name`, `target_id`, `last_message`, `last_time`, `unread_count`) VALUES
(1, 'private', '私信', NULL, '暂无聊天消息', NOW(), 2),
(1, 'service', '客服中心', 1, '您好！请问有什么可以帮助您的？', NOW(), 0);

-- 插入消息数据（给userId=2）
INSERT INTO `message` (`user_id`, `type`, `name`, `content`, `preview`, `time`, `unread_count`) VALUES
(2, 'announcement', '系统公告', '【系统维护通知】\n\n尊敬的用户：\n\n为了提升平台服务质量，我们将进行系统维护升级。\n\n维护时间：今晚22:00-24:00\n维护范围：全部功能\n\n请提前做好安排，给您带来不便敬请谅解！\n\n特色农产品销售平台\n2026年5月11日', '平台将于今晚22:00-24:00进行系统维护', '5分钟前', 0),
(2, 'system', '账号安全', '【账号安全提醒】\n\n检测到您的账号在新设备登录：\n\n登录地点：北京市\n登录时间：2026年5月11日 10:30\n设备信息：Chrome浏览器\n\n如果不是您本人操作，请立即修改密码或联系客服。', '检测到新设备登录，登录地点：北京市', '1小时前', 1),
(2, 'order', '售后结果', '【退款通知】\n\n您好！\n\n您的退款申请已审核通过：\n\n订单编号：DD20260510001\n退款金额：￥158.00\n退款方式：原路返回\n预计到账：1-3个工作日\n\n感谢您的理解与支持！', '您的退款申请已通过，预计1-3个工作日到账', '3小时前', 2),
(2, 'promotion', '优惠活动', '【新用户专享优惠】\n\n新用户首单福利来啦！\n\n活动内容：\n- 首单满100元减20元\n- 精选商品5折起\n- 免费包邮到家\n\n活动时间：即日起至5月31日\n适用人群：新注册用户\n\n点击立即领取优惠券 >>', '新用户首单满100减20，快来选购吧！', '昨天', 0);

-- 插入私信会话数据（给userId=2）
INSERT INTO `message_session` (`user_id`, `session_type`, `target_name`, `target_id`, `last_message`, `last_time`, `unread_count`) VALUES
(2, 'private', '私信', NULL, '暂无聊天消息', NOW(), 2),
(2, 'service', '客服中心', 1, '您好！请问有什么可以帮助您的？', NOW(), 0);

-- 插入聊天消息内容
INSERT INTO `message_content` (`session_id`, `user_id`, `content`, `is_self`, `message_time`) VALUES
(1, 1, '您好！请问有什么可以帮助您的？', 0, NOW()),
(2, 1, '您好！请问有什么可以帮助您的？', 0, NOW()),
(2, 2, '我想咨询一下订单物流信息', 1, NOW()),
(2, 1, '好的，请您提供一下订单号，我帮您查询', 0, NOW()),
(3, 1, '您好！请问有什么可以帮助您的？', 0, NOW()),
(4, 1, '您好！请问有什么可以帮助您的？', 0, NOW()),
(4, 1, '我想咨询一下订单物流信息', 1, NOW()),
(4, 1, '好的，请您提供一下订单号，我帮您查询', 0, NOW());
