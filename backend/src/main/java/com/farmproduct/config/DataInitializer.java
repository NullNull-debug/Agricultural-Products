package com.farmproduct.config;

import com.farmproduct.entity.Order;
import com.farmproduct.entity.OrderItem;
import com.farmproduct.entity.Product;
import com.farmproduct.entity.User;
import com.farmproduct.mapper.OrderItemMapper;
import com.farmproduct.mapper.OrderMapper;
import com.farmproduct.mapper.ProductMapper;
import com.farmproduct.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            createTables();
            initUsers();
            initProducts();
            initOrders();
            initMessages();
        } catch (Exception e) {
            System.out.println("数据初始化失败，可能是数据库连接问题：" + e.getMessage());
        }
    }

    private void createTables() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "username VARCHAR(50) NOT NULL UNIQUE, " +
            "password VARCHAR(100) NOT NULL, " +
            "name VARCHAR(50) NOT NULL, " +
            "phone VARCHAR(20) NOT NULL, " +
            "address VARCHAR(200) NOT NULL, " +
            "role INT NOT NULL DEFAULT 0, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "name VARCHAR(100) NOT NULL, " +
            "description TEXT NOT NULL, " +
            "price DOUBLE NOT NULL, " +
            "stock INT NOT NULL, " +
            "image VARCHAR(200) NOT NULL, " +
            "category VARCHAR(50) NOT NULL, " +
            "status INT NOT NULL DEFAULT 1, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "order_no VARCHAR(50) NOT NULL UNIQUE, " +
            "user_id BIGINT NOT NULL, " +
            "total_amount DOUBLE NOT NULL, " +
            "status INT NOT NULL DEFAULT 0, " +
            "create_time DATETIME NOT NULL, " +
            "update_time DATETIME NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS order_items (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "order_id BIGINT NOT NULL, " +
            "product_id BIGINT NOT NULL, " +
            "quantity INT NOT NULL, " +
            "price DOUBLE NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS announcement (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "title VARCHAR(200) NOT NULL, " +
            "content TEXT NOT NULL, " +
            "create_time DATETIME NOT NULL, " +
            "status INT NOT NULL DEFAULT 1, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS review (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "product_id BIGINT NOT NULL, " +
            "user_id BIGINT NOT NULL, " +
            "content TEXT NOT NULL, " +
            "rating INT NOT NULL, " +
            "create_time DATETIME NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS message (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "user_id BIGINT NOT NULL, " +
            "type VARCHAR(20) NOT NULL, " +
            "name VARCHAR(100) NOT NULL, " +
            "content TEXT NOT NULL, " +
            "preview VARCHAR(200), " +
            "time VARCHAR(50), " +
            "unread_count INT NOT NULL DEFAULT 0, " +
            "status INT NOT NULL DEFAULT 1, " +
            "create_time DATETIME NOT NULL, " +
            "update_time DATETIME NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS message_session (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "user_id BIGINT NOT NULL, " +
            "session_type VARCHAR(20) NOT NULL, " +
            "target_name VARCHAR(100), " +
            "target_id BIGINT, " +
            "last_message TEXT, " +
            "last_time DATETIME, " +
            "unread_count INT NOT NULL DEFAULT 0, " +
            "status INT NOT NULL DEFAULT 1, " +
            "create_time DATETIME NOT NULL, " +
            "update_time DATETIME NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS message_content (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "session_id BIGINT NOT NULL, " +
            "user_id BIGINT NOT NULL, " +
            "content TEXT NOT NULL, " +
            "is_self BOOLEAN NOT NULL DEFAULT FALSE, " +
            "message_time DATETIME, " +
            "status INT NOT NULL DEFAULT 1, " +
            "create_time DATETIME NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");

        try {
            jdbcTemplate.execute("CREATE INDEX idx_username ON users (username)");
        } catch (Exception e) {
            System.out.println("创建索引失败: " + e.getMessage());
        }
        System.out.println("数据库表结构创建成功");
    }

    private void initUsers() {
        if (userMapper.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("管理员");
            admin.setPhone("13800138000");
            admin.setAddress("管理员地址");
            admin.setRole(1);
            userMapper.insert(admin);
            System.out.println("管理员账号已创建: admin/admin123");
        }

        if (userMapper.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setName("普通用户");
            user.setPhone("13800138001");
            user.setAddress("普通用户地址");
            user.setRole(0);
            userMapper.insert(user);
            System.out.println("普通用户账号已创建: user/user123");
        }
    }

    private void initProducts() {
        if (productMapper.selectCount(null) == 0) {
            Product[] products = {
                createProduct("有机蔬菜礼盒", "包含多种新鲜有机蔬菜", 99.9, 100, "vegetable.jpg", "蔬菜", 1),
                createProduct("农家散养土鸡蛋", "纯天然散养土鸡蛋", 68.0, 200, "egg.jpg", "禽蛋", 1),
                createProduct("有机大米", "东北有机大米", 88.0, 150, "rice.jpg", "粮食", 1),
                createProduct("新鲜水果礼盒", "时令新鲜水果", 128.0, 80, "fruit.jpg", "水果", 1),
                createProduct("农家蜂蜜", "纯天然农家蜂蜜", 158.0, 50, "honey.jpg", "调味品", 1),
                createProduct("有机茶叶", "高山有机茶叶", 298.0, 30, "tea.jpg", "饮品", 1),
                createProduct("农家腊肉", "传统工艺制作腊肉", 198.0, 40, "bacon.jpg", "肉类", 1),
                createProduct("有机核桃", "新疆有机核桃", 78.0, 120, "walnut.jpg", "坚果", 1)
            };

            for (Product product : products) {
                productMapper.insert(product);
            }
            System.out.println("商品数据已初始化，共创建8个商品");
        }
    }

    private Product createProduct(String name, String description, double price, int stock, String image, String category, int status) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(image);
        product.setCategory(category);
        product.setStatus(status);
        return product;
    }

    private void initOrders() {
        if (orderMapper.selectCount(null) == 0) {
            User user = userMapper.findByUsername("user");
            List<Product> products = productMapper.selectList(null);

            if (user != null && !products.isEmpty()) {
                Order order = new Order();
                order.setOrderNo("ORD" + System.currentTimeMillis());
                order.setUserId(user.getId());
                order.setTotalAmount(products.get(0).getPrice() + products.get(1).getPrice());
                order.setStatus(3);
                order.setCreateTime(new Date());
                order.setUpdateTime(new Date());
                orderMapper.insert(order);

                OrderItem item1 = new OrderItem();
                item1.setOrderId(order.getId());
                item1.setProductId(products.get(0).getId());
                item1.setQuantity(1);
                item1.setPrice(products.get(0).getPrice());
                orderItemMapper.insert(item1);

                OrderItem item2 = new OrderItem();
                item2.setOrderId(order.getId());
                item2.setProductId(products.get(1).getId());
                item2.setQuantity(1);
                item2.setPrice(products.get(1).getPrice());
                orderItemMapper.insert(item2);

                System.out.println("订单数据已初始化");
            }
        }
    }

    private void initMessages() {
        try {
            jdbcTemplate.execute("DELETE FROM message_content");
            jdbcTemplate.execute("DELETE FROM message_session");
            jdbcTemplate.execute("DELETE FROM message");
        } catch (Exception e) {
            System.out.println("清空旧消息数据失败: " + e.getMessage());
        }

        User user1 = userMapper.findByUsername("user");
        User admin = userMapper.findByUsername("admin");
        Long userId1 = user1 != null ? user1.getId() : 1L;
        Long adminId = admin != null ? admin.getId() : 2L;

        System.out.println("初始化消息 - user ID: " + userId1 + ", admin ID: " + adminId);

        insertUserMessages(userId1, adminId);
        insertUserMessages(adminId, userId1);

        System.out.println("消息数据已初始化");
    }

    private void insertUserMessages(Long userId, Long targetId) {
        jdbcTemplate.update(
            "INSERT INTO message (user_id, type, name, content, preview, time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            userId, "announcement", "系统公告",
            "【系统维护通知】\n\n尊敬的用户：\n\n为了提升平台服务质量，我们将进行系统维护升级。\n\n维护时间：今晚22:00-24:00\n维护范围：全部功能\n\n请提前做好安排，给您带来不便敬请谅解！\n\n特色农产品销售平台\n2026年5月11日",
            "平台将于今晚22:00-24:00进行系统维护", "5分钟前", 0, 1
        );

        jdbcTemplate.update(
            "INSERT INTO message (user_id, type, name, content, preview, time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            userId, "system", "账号安全",
            "【账号安全提醒】\n\n检测到您的账号在新设备登录：\n\n登录地点：北京市\n登录时间：2026年5月11日 10:30\n设备信息：Chrome浏览器\n\n如果不是您本人操作，请立即修改密码或联系客服。",
            "检测到新设备登录，登录地点：北京市", "1小时前", 1, 1
        );

        jdbcTemplate.update(
            "INSERT INTO message (user_id, type, name, content, preview, time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            userId, "order", "售后结果",
            "【退款通知】\n\n您好！\n\n您的退款申请已审核通过：\n\n订单编号：DD20260510001\n退款金额：￥158.00\n退款方式：原路返回\n预计到账：1-3个工作日\n\n感谢您的理解与支持！",
            "您的退款申请已通过，预计1-3个工作日到账", "3小时前", 2, 1
        );

        jdbcTemplate.update(
            "INSERT INTO message (user_id, type, name, content, preview, time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            userId, "promotion", "优惠活动",
            "【新用户专享】\n\n亲爱的用户：\n\n为感谢您对我们的支持，现推出新用户专享优惠：\n\n新用户首单满100减20\n优惠码：NEWUSER20\n有效期至：2026年5月31日\n\n快来选购吧！",
            "新用户首单满100减20，快来选购吧！", "昨天", 0, 1
        );

        jdbcTemplate.update(
            "INSERT INTO message_session (user_id, session_type, target_name, target_id, last_message, last_time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, NOW(), NOW())",
            userId, "private", "私信", null, "暂无聊天消息", 2, 1
        );

        jdbcTemplate.update(
            "INSERT INTO message_session (user_id, session_type, target_name, target_id, last_message, last_time, unread_count, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, NOW(), NOW())",
            userId, "service", "客服中心", targetId, "您好！请问有什么可以帮助您的？", 0, 1
        );

        Long sessionId = null;
        try {
            sessionId = jdbcTemplate.queryForObject("SELECT id FROM message_session WHERE user_id = ? AND session_type = 'service' AND status = 1", Long.class, userId);
        } catch (Exception e) {
            System.out.println("获取会话ID失败: " + e.getMessage());
        }

        if (sessionId != null) {
            jdbcTemplate.update(
                "INSERT INTO message_content (session_id, user_id, content, is_self, message_time, status, create_time) VALUES (?, ?, ?, ?, NOW(), ?, NOW())",
                sessionId, targetId, "您好！请问有什么可以帮助您的？", 0, 1
            );
        }
    }
}
