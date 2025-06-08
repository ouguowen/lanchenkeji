package com.lanchenjishu.aicreator.database

import android.util.Log
import java.sql.Connection
import java.sql.SQLException

/**
 * 数据库初始化工具类
 * 用于创建数据库表结构
 */
object DatabaseInitializer {
    private const val TAG = "DatabaseInitializer"
    
    /**
     * 初始化数据库表结构
     */
    fun initialize() {
        try {
            createUserTable()
            createCreationHistoryTable()
            createCreationTemplateTable()
            createCreditsRecordTable()
            Log.i(TAG, "数据库初始化完成")
        } catch (e: SQLException) {
            Log.e(TAG, "数据库初始化失败", e)
            throw RuntimeException("数据库初始化失败", e)
        }
    }
    
    /**
     * 创建用户表
     */
    private fun createUserTable() {
        val connection = DatabaseConfig.getConnection()
        
        try {
            val sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    nickname VARCHAR(50) NOT NULL,
                    avatar VARCHAR(255),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    vip_level INT NOT NULL DEFAULT 0,
                    credits INT NOT NULL DEFAULT 0,
                    create_time TIMESTAMP NOT NULL,
                    update_time TIMESTAMP NOT NULL,
                    last_login_time TIMESTAMP,
                    status INT NOT NULL DEFAULT 1,
                    INDEX idx_username (username),
                    INDEX idx_email (email),
                    INDEX idx_phone (phone)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """.trimIndent()
            
            executeStatement(connection, sql)
        } finally {
            connection.close()
        }
    }
    
    /**
     * 创建创作历史表
     */
    private fun createCreationHistoryTable() {
        val connection = DatabaseConfig.getConnection()
        
        try {
            val sql = """
                CREATE TABLE IF NOT EXISTS creation_history (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    creation_type VARCHAR(20) NOT NULL COMMENT '创作类型: TEXT_TO_IMAGE, IMAGE_TO_VIDEO, VOICE_CLONE, COPYWRITING, DIGITAL_HUMAN',
                    title VARCHAR(100) NOT NULL,
                    prompt TEXT NOT NULL,
                    result_url TEXT,
                    thumbnail_url VARCHAR(255),
                    create_time TIMESTAMP NOT NULL,
                    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING, PROCESSING, COMPLETED, FAILED',
                    credits_cost INT NOT NULL DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    INDEX idx_user_id (user_id),
                    INDEX idx_creation_type (creation_type),
                    INDEX idx_create_time (create_time)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """.trimIndent()
            
            executeStatement(connection, sql)
        } finally {
            connection.close()
        }
    }
    
    /**
     * 创建创作模板表
     */
    private fun createCreationTemplateTable() {
        val connection = DatabaseConfig.getConnection()
        
        try {
            val sql = """
                CREATE TABLE IF NOT EXISTS creation_template (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    template_name VARCHAR(100) NOT NULL,
                    template_type INT NOT NULL COMMENT '创作类型: 1-文生图, 2-图生视频, 3-声音克隆, 4-文案创作, 5-数字人',
                    prompt_template TEXT NOT NULL,
                    description TEXT,
                    thumbnail_url VARCHAR(255),
                    is_system BOOLEAN NOT NULL DEFAULT FALSE,
                    create_time TIMESTAMP NOT NULL,
                    update_time TIMESTAMP,
                    INDEX idx_template_type (template_type),
                    INDEX idx_is_system (is_system)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """.trimIndent()
            
            executeStatement(connection, sql)
        } finally {
            connection.close()
        }
    }
    
    /**
     * 创建积分记录表
     */
    private fun createCreditsRecordTable() {
        val connection = DatabaseConfig.getConnection()
        
        try {
            val sql = """
                CREATE TABLE IF NOT EXISTS credits_records (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    amount INT NOT NULL COMMENT '积分变动数量, 正数表示增加, 负数表示减少',
                    balance INT NOT NULL COMMENT '变动后的积分余额',
                    type VARCHAR(20) NOT NULL COMMENT '类型: RECHARGE, CONSUMPTION, REWARD, REFUND',
                    description VARCHAR(255) NOT NULL,
                    related_id BIGINT COMMENT '关联ID, 如创作ID, 订单ID等',
                    create_time TIMESTAMP NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    INDEX idx_user_id (user_id),
                    INDEX idx_create_time (create_time),
                    INDEX idx_type (type)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """.trimIndent()
            
            executeStatement(connection, sql)
        } finally {
            connection.close()
        }
    }
    
    /**
     * 执行SQL语句
     */
    private fun executeStatement(connection: Connection, sql: String) {
        val statement = connection.createStatement()
        try {
            statement.execute(sql)
        } finally {
            statement.close()
        }
    }
} 