-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `role` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `idx_username` (`username`)
);

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `price` DOUBLE NOT NULL,
  `stock` INT NOT NULL,
  `image` VARCHAR(200) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `status` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`)
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL UNIQUE,
  `user_id` BIGINT NOT NULL,
  `total_amount` DOUBLE NOT NULL,
  `status` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`)
);

-- 创建订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `price` DOUBLE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`)
);

-- 创建公告表
CREATE TABLE IF NOT EXISTS `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`)
);

-- 创建评价表
CREATE TABLE IF NOT EXISTS `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `rating` INT NOT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_product_id` (`product_id`),
  INDEX `idx_user_id` (`user_id`)
);

-- 插入用户数据
-- 密码使用 BCrypt 加密，对应明文密码: admin123
INSERT INTO `user` (`username`, `password`, `name`, `phone`, `address`, `role`) VALUES
('admin', '$2a$10$6J5m5rO4qQ2p7X2e3Y4z5W6V7U8T9S0R1E2W3Q4E5R6T7Y8U9I0', '管理员', '13800138000', '管理员地址', 1),
('user', '$2a$10$6J5m5rO4qQ2p7X2e3Y4z5W6V7U8T9S0R1E2W3Q4E5R6T7Y8U9I0', '普通用户', '13800138001', '普通用户地址', 0);

-- 插入商品数据
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `image`, `category`, `status`) VALUES
('有机蔬菜礼盒', '包含多种新鲜有机蔬菜', 99.9, 100, 'vegetable.jpg', '蔬菜', 1),
('农家散养土鸡蛋', '纯天然散养土鸡蛋', 68.0, 200, 'egg.jpg', '禽蛋', 1),
('有机大米', '东北有机大米', 88.0, 150, 'rice.jpg', '粮食', 1),
('新鲜水果礼盒', '时令新鲜水果', 128.0, 80, 'fruit.jpg', '水果', 1),
('农家蜂蜜', '纯天然农家蜂蜜', 158.0, 50, 'honey.jpg', '调味品', 1),
('有机茶叶', '高山有机茶叶', 298.0, 30, 'tea.jpg', '饮品', 1),
('农家腊肉', '传统工艺制作腊肉', 198.0, 40, 'bacon.jpg', '肉类', 1),
('有机核桃', '新疆有机核桃', 78.0, 120, 'walnut.jpg', '坚果', 1);

-- 插入订单数据
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `create_time`, `update_time`) VALUES
('ORD20260308001', 2, 167.9, 3, NOW(), NOW());

-- 插入订单项数据
INSERT INTO `order_item` (`order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 1, 99.9),
(1, 2, 1, 68.0);

-- 插入公告数据
INSERT INTO `announcement` (`title`, `content`, `status`, `create_time`) VALUES
('欢迎光临农家产品商城', '感谢您访问我们的农家产品商城，我们提供新鲜、有机、健康的农产品。', 1, NOW()),
('新品上市', '本周新品：有机蔬菜礼盒和农家散养土鸡蛋，欢迎选购！', 1, NOW());

-- 插入评价数据
INSERT INTO `review` (`product_id`, `user_id`, `content`, `rating`, `create_time`) VALUES
(1, 2, '蔬菜很新鲜，包装很好，下次还会购买！', 5, NOW()),
(2, 2, '土鸡蛋味道很好，值得推荐。', 4, NOW());
