package com.lanchenjishu.aicreator.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * MySQL数据库配置类
 * 使用HikariCP连接池管理数据库连接
 */
object DatabaseConfig {
    // MySQL数据库连接信息
    private const val HOST = "lanchenjishu-mysql.ns-twk4hi58.svc"
    private const val PORT = 3306
    private const val DATABASE_NAME = "lanchenjishu"
    private const val USERNAME = "root"
    private const val PASSWORD = "shbbz7d"
    
    // 连接池配置
    private const val MAX_POOL_SIZE = 10
    private const val MIN_IDLE = 2
    private const val CONNECTION_TIMEOUT = 30000L
    
    // 数据源
    private val dataSource: HikariDataSource
    
    // 数据库驱动
    const val DRIVER = "com.mysql.jdbc.Driver"
    
    // 数据库连接URL
    const val URL = "jdbc:mysql://localhost:3306/ai_creator?useUnicode=true&characterEncoding=utf8&useSSL=false"
    
    // 数据库用户名
    const val USER = "root"
    
    // 数据库密码
    const val PASSWORD = "password"
    
    // 数据库表名
    object Tables {
        const val USER = "user"
        const val CREATION_HISTORY = "creation_history"
        const val CREATION_TEMPLATE = "creation_template"
        const val CREDITS_RECORD = "credits_record"
    }
    
    init {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$HOST:$PORT/$DATABASE_NAME?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
            username = USERNAME
            password = PASSWORD
            maximumPoolSize = MAX_POOL_SIZE
            minimumIdle = MIN_IDLE
            connectionTimeout = CONNECTION_TIMEOUT
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }
        
        dataSource = HikariDataSource(config)
    }
    
    /**
     * 获取数据库连接
     */
    fun getConnection(): Connection {
        try {
            return dataSource.connection
        } catch (e: SQLException) {
            throw RuntimeException("获取数据库连接失败", e)
        }
    }
    
    /**
     * 关闭连接池
     */
    fun closePool() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }
} 