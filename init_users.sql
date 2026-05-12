-- ==============================================
-- 用户数据初始化脚本（明文密码版本）
-- 用户名: admin, 密码: admin123
-- 用户名: user, 密码: user123
-- ==============================================

USE farm;

-- 如果用户表不存在则创建
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    role INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 删除现有用户数据（可选，谨慎使用）
-- TRUNCATE TABLE users;

-- 插入管理员用户 (admin/admin123) - 明文密码
INSERT INTO users (username, password, name, phone, address, role) VALUES (
    'admin',
    'admin123',
    '管理员',
    '13800138000',
    '管理员地址',
    1
) ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    name = VALUES(name),
    phone = VALUES(phone),
    address = VALUES(address),
    role = VALUES(role);

-- 插入普通用户 (user/user123) - 明文密码
INSERT INTO users (username, password, name, phone, address, role) VALUES (
    'user',
    'user123',
    '普通用户',
    '13800138001',
    '普通用户地址',
    0
) ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    name = VALUES(name),
    phone = VALUES(phone),
    address = VALUES(address),
    role = VALUES(role);

SELECT '用户数据初始化完成' AS result;
SELECT username, name, role FROM users;