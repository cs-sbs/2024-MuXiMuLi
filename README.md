# 图书管理系统

一个基于 Java 的图书管理系统，支持多种类型图书的管理和自动备份功能。

## 功能特性

- 支持多种类型图书管理（计算机、文学、科学等）
- 库存管理
- 用户管理（管理员/普通用户）
- 自动备份功能
- 事务管理
- 连接池支持

## 技术栈

- Java 17
- MySQL 8.0
- HikariCP
- Log4j2
- JUnit 5
- Jackson
- Lombok

## 项目结构

```
src/
├── main/
│   ├── java/com/library/
│   │   ├── domain/         # 领域模型
│   │   ├── repository/     # 数据访问层
│   │   ├── service/        # 业务逻辑层
│   │   └── util/          # 工具类
│   └── resources/
│       ├── application.properties  # 配置文件
│       └── sample_data.sql        # 示例数据
└── test/
    └── java/com/library/
        └── service/        # 测试类
```

## 快速开始

1. 克隆项目
2. 配置数据库连接（application.properties）
3. 执行 sample_data.sql 创建表结构和示例数据
4. 运行测试确保环境正常

## 配置说明

主要配置文件：`src/main/resources/application.properties`

```properties
db.url=jdbc:mysql://localhost:3306/library_management
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

## 测试

运行所有测试：
```bash
mvn test
```

## 备份功能

系统每24小时自动备份一次数据到 JSON 文件：
- 备份路径：./backup/
- 文件格式：books_backup_YYYYMMDD_HHMMSS.json

## 构建和运行

1. 构建项目：
```bash
mvn clean package
```

2. 运行项目：
```bash
java -jar target/library-management-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 开发环境设置

1. 安装必要软件：
   - JDK 17
   - MySQL 8.0
   - Maven 3.8+

2. 配置数据库：
   - 创建数据库：library_management
   - 执行 src/main/resources/sample_data.sql

3. 配置应用：
   - 复制 src/main/resources/application.properties.template 到 application.properties
   - 修改数据库连接信息

4. 运行测试：
```bash
mvn test
```

## 日志配置

系统使用 log4j 1.x 进行日志管理：
- 控制台输出：INFO 级别
- 文件输出：DEBUG 级别（logs/library.log）
- HikariCP 日志：INFO 级别

配置文件：`src/main/resources/log4j.properties`

## SQL 脚本说明

项目使用 MySQL 数据库，`sample_data.sql` 使用 MySQL 语法。如果您的 IDE 显示 SQL 语法错误，这可能是因为 IDE 使用了 SQL Server 的语法检查器。这些错误可以安全忽略，因为这些是有效的 MySQL 语句。

要正确执行 SQL 脚本：
1. 确保使用 MySQL 8.0 或更高版本
2. 使用 MySQL 命令行或 MySQL Workbench 执行脚本
3. 或者在 IDE 中将 SQL 方言设置为 MySQL