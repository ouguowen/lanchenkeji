package com.lanchenjishu.aicreator.database

import java.sql.Connection
import java.sql.SQLException

/**
 * 数据库管理类
 */
object DatabaseManager {
    
    private var dataSource: com.zaxxer.hikari.HikariDataSource? = null
    
    /**
     * 初始化数据库连接池
     */
    fun init() {
        if (dataSource == null) {
            try {
                // 加载数据库驱动
                Class.forName(DatabaseConfig.DRIVER)
                
                // 初始化连接池
                val config = com.zaxxer.hikari.HikariConfig()
                config.jdbcUrl = DatabaseConfig.URL
                config.username = DatabaseConfig.USER
                config.password = DatabaseConfig.PASSWORD
                config.maximumPoolSize = 10
                config.minimumIdle = 2
                config.idleTimeout = 600000 // 10分钟
                config.connectionTimeout = 30000 // 30秒
                config.maxLifetime = 1800000 // 30分钟
                
                dataSource = com.zaxxer.hikari.HikariDataSource(config)
                
                // 初始化数据库
                DatabaseInitializer.initialize()
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException("数据库初始化失败: " + e.message)
            }
        }
    }
    
    /**
     * 获取数据库连接
     */
    fun getConnection(): Connection {
        if (dataSource == null) {
            init()
        }
        
        try {
            return dataSource!!.connection
        } catch (e: SQLException) {
            e.printStackTrace()
            throw RuntimeException("获取数据库连接失败: " + e.message)
        }
    }
    
    /**
     * 关闭连接池
     */
    fun close() {
        dataSource?.close()
        dataSource = null
    }
} 