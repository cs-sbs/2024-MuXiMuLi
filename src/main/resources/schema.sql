CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- 删除所有现有表
DROP TABLE IF EXISTS backup_records;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    is_admin TINYINT(1) DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建图书表
CREATE TABLE books (
    id BIGINT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    
    -- 计算机类
    programming_language VARCHAR(50) NULL,
    framework VARCHAR(50) NULL,
    difficulty VARCHAR(20) NULL,
    
    -- 文学类
    genre VARCHAR(50) NULL,
    era VARCHAR(50) NULL,
    language VARCHAR(50) NULL,
    
    -- 科学类
    subject_area VARCHAR(50) NULL,
    research_field VARCHAR(50) NULL,
    academic_level VARCHAR(20) NULL,
    
    -- 艺术类
    art_form VARCHAR(50) NULL,
    medium VARCHAR(50) NULL,
    style VARCHAR(50) NULL,
    
    -- 历史类
    time_period VARCHAR(50) NULL,
    region VARCHAR(50) NULL,
    historical_figures VARCHAR(100) NULL,
    
    -- 哲学类
    philosophical_school VARCHAR(50) NULL,
    key_concepts VARCHAR(100) NULL,
    thinkers VARCHAR(100) NULL,
    
    -- 经济类
    economic_school VARCHAR(50) NULL,
    market_type VARCHAR(50) NULL,
    application_field VARCHAR(50) NULL,
    
    -- 医学类
    medical_specialty VARCHAR(50) NULL,
    clinical_focus VARCHAR(50) NULL,
    practice_area VARCHAR(50) NULL,
    
    -- 教育类
    education_level VARCHAR(50) NULL,
    subject VARCHAR(50) NULL,
    teaching_method VARCHAR(50) NULL,
    
    -- 法律类
    legal_system VARCHAR(50) NULL,
    jurisdiction VARCHAR(50) NULL,
    legal_field VARCHAR(50) NULL,
    
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn),
    KEY idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建备份记录表
CREATE TABLE backup_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    backup_time TIMESTAMP NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    record_count INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_backup_time (backup_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;