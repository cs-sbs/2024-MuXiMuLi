-- 重置数据库脚本
DROP DATABASE IF EXISTS library_management;
CREATE DATABASE library_management;
USE library_management;

-- 设置字符集
ALTER DATABASE library_management CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci; 