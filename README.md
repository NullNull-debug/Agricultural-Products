# Agricultural-Products（农产品电商平台）

基于 Spring Boot + Vue3 的前后端分离B2C农产品电商系统

## 项目简介

Agricultural-Products 是一个面向农产品销售的专业电商平台，致力于连接农产品生产者与消费者，提供便捷的线上购物体验。平台采用前后端分离架构，后端基于 Spring Boot 构建稳定可靠的服务端，前端使用 Vue3 + Element Plus 打造美观易用的用户界面。

项目核心亮点是实现了基于 RabbitMQ 的实时消息通知系统，支持用户与管理员之间的一对一私信聊天、系统公告推送、订单状态通知等功能，为用户提供及时的消息服务。

## 功能模块

### 用户端功能

- **用户管理**：用户注册、登录、个人信息管理、收货地址管理
- **商品浏览**：农产品分类浏览、筛选搜索、商品详情查看
- **购物车**：商品添加、数量修改、批量操作
- **订单管理**：订单提交、支付、状态跟踪、售后申请
- **消息系统**：实时私信聊天、系统通知接收、未读消息提醒
- **评价系统**：商品评价与查看

### 管理员端功能

- **用户管理**：用户列表查看、状态管理
- **商品管理**：商品上架、编辑、分类管理
- **订单管理**：订单列表、订单处理、发货管理
- **售后处理**：售后申请审核、退款处理
- **消息管理**：系统公告发布、消息推送、在线客服
- **数据统计**：订单统计、销售数据展示

## 技术架构

### 架构设计

采用经典的前后端分离架构，前端负责界面展示和用户交互，后端提供 RESTful API 服务，通过 JSON 格式进行数据交互。

### 核心技术选型

| 分类      | 技术           | 作用            |
| :------ | :----------- | :------------ |
| 前端框架    | Vue3 + Vite  | 构建响应式用户界面     |
| 前端组件库   | Element Plus | 提供丰富的UI组件     |
| HTTP客户端 | Axios        | 发送异步请求        |
| 后端框架    | Spring Boot  | 构建 RESTful 服务 |
| ORM框架   | MyBatis-Plus | 数据库访问         |
| 数据库     | MySQL 8.0    | 数据持久化存储       |
| 缓存      | Redis 6.0    | 缓存热点数据、会话管理   |
| 消息队列    | RabbitMQ 3.9 | 异步消息处理、实时消息推送 |
| 实时通信    | WebSocket    | 前端实时消息接收      |
| 认证方式    | JWT          | 无状态身份认证       |

### 消息系统架构

- **RabbitMQ**：负责消息路由和分发，支持点对点和广播模式
- **WebSocket**：维持前端与后端的长连接，实现实时消息推送
- **MessageService**：封装消息的发送、接收和存储逻辑

## 快速开始

### 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.9+

### 后端启动步骤

1. **克隆项目**

```bash
git clone https://github.com/your-username/Agricultural-Products.git
cd Agricultural-Products
```

1. **创建数据库**

```sql
CREATE DATABASE agricultural_products CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

1. **导入数据库脚本**

```bash
mysql -u your-username -p agricultural_products < backend/src/main/resources/farm_product.sql
mysql -u your-username -p agricultural_products < backend/src/main/resources/message.sql
```

1. **修改配置文件**

编辑 `backend/src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.username=your-username
spring.datasource.password=your-password

# Redis配置（如使用默认配置可跳过）
spring.redis.host=localhost
spring.redis.port=6379

# RabbitMQ配置（如使用默认配置可跳过）
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

1. **构建并启动后端**

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 前端启动步骤

1. **进入前端目录**

```bash
cd frontend
```

1. **安装依赖**

```bash
npm install
```

1. **启动前端服务**

```bash
npm run dev
```

前端服务默认运行在 `http://localhost:5173`

1. **访问项目**

打开浏览器访问 `http://localhost:5173`

## 项目结构

```
Agricultural-Products/
├── backend/                                    # 后端服务
│   ├── src/main/java/com/farmproduct/
│   │   ├── controller/                         # REST API控制层
│   │   │   ├── MessageController.java          # 消息相关接口
│   │   │   ├── OrderController.java            # 订单相关接口
│   │   │   ├── ProductController.java          # 商品相关接口
│   │   │   ├── UserController.java             # 用户相关接口
│   │   │   └── ...
│   │   ├── service/                            # 业务逻辑层
│   │   │   ├── MessageService.java             # 消息服务
│   │   │   └── UserService.java                # 用户服务
│   │   ├── mapper/                             # 数据访问层
│   │   ├── entity/                             # 实体类
│   │   ├── config/                             # 配置类
│   │   │   ├── RabbitMQConfig.java             # RabbitMQ配置
│   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   └── WebSocketConfig.java            # WebSocket配置
│   │   ├── mq/                                 # 消息队列监听
│   │   │   └── MessageListener.java            # RabbitMQ消息监听
│   │   ├── websocket/                          # WebSocket处理
│   │   │   └── MessageWebSocketHandler.java    # WebSocket处理器
│   │   └── Application.java                    # 启动类
│   ├── src/main/resources/
│   │   ├── mapper/                             # MyBatis映射文件
│   │   ├── application.properties              # 应用配置
│   │   ├── farm_product.sql                    # 业务数据库脚本
│   │   └── message.sql                         # 消息数据库脚本
│   └── pom.xml                                 # Maven依赖管理
├── frontend/                                   # 前端应用
│   ├── src/
│   │   ├── api/                                # API接口封装
│   │   │   └── message.js                      # 消息API
│   │   ├── components/                         # 公共组件
│   │   │   └── message/                        # 消息相关组件
│   │   ├── router/                             # 路由配置
│   │   ├── views/                              # 页面视图
│   │   │   ├── Admin/                          # 管理员页面
│   │   │   │   ├── AdminMessages.vue           # 管理员消息中心
│   │   │   │   └── ...
│   │   │   ├── user/                           # 用户页面
│   │   │   ├── Message.vue                     # 用户消息中心
│   │   │   └── ...
│   │   ├── App.vue                             # 根组件
│   │   ├── main.js                             # 入口文件
│   │   └── style.css                           # 全局样式
│   ├── index.html                              # HTML模板
│   ├── package.json                            # 前端依赖
│   └── vite.config.js                          # Vite配置
├── LICENSE                                     # 许可证文件
└── README.md                                   # 项目说明文档
```

## 核心功能说明

### 实时消息系统

基于 RabbitMQ 和 WebSocket 实现的实时消息系统，支持：

- **一对一私信**：用户与管理员之间的实时聊天
- **消息推送**：订单状态变更、售后处理结果等通知
- **系统公告**：管理员发布的全局通知消息
- **未读消息计数**：实时统计并展示未读消息数量

### 消息通知类型

| 类型        | 说明   |
| :-------- | :--- |
| system    | 系统公告 |
| private   | 私信消息 |
| order     | 订单通知 |
| service   | 客服消息 |
| promotion | 优惠活动 |

### 未读消息计数

- 支持分类统计：全部消息、系统通知、互动消息等
- 实时更新：收到新消息立即更新计数
- 持久化存储：刷新页面后计数保持正确

## 注意事项

1. **中间件启动**：确保 MySQL、Redis、RabbitMQ 都已正确安装并启动
2. **数据库配置**：`application.properties` 中的数据库用户名和密码要与本地配置一致
3. **RabbitMQ配置**：确保已开启 WebSocket 插件

```bash
rabbitmq-plugins enable rabbitmq_web_stomp
```

1. **路径要求**：项目必须放在纯英文路径下，避免中文路径导致的编码问题
2. **端口占用**：确保 8080（后端）和 5173（前端）端口未被占用

## 许可证

MIT License

***

**项目维护者**：Jiang\
**联系方式**：[162018147@qq.com](mailto:your.email@example.com)
