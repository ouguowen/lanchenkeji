package com.lanchenjishu.aicreator.database.dao

import com.lanchenjishu.aicreator.database.BaseDao
import com.lanchenjishu.aicreator.models.User
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

/**
 * 用户数据访问对象
 */
class UserDao : BaseDao() {
    
    /**
     * 根据ID查询用户
     */
    fun findById(id: Long): User? {
        val sql = "SELECT * FROM users WHERE id = ?"
        return executeQuery(sql, arrayOf(id)) { resultSet ->
            mapResultSetToUser(resultSet)
        }
    }
    
    /**
     * 根据用户名查询用户
     */
    fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        return executeQuery(sql, arrayOf(username)) { resultSet ->
            mapResultSetToUser(resultSet)
        }
    }
    
    /**
     * 查询所有用户
     */
    fun findAll(): List<User> {
        val sql = "SELECT * FROM users"
        return executeQueryList(sql) { resultSet ->
            mapResultSetToUser(resultSet)
        }
    }
    
    /**
     * 查询分页用户列表
     */
    fun findByPage(page: Int, pageSize: Int): List<User> {
        val offset = (page - 1) * pageSize
        val sql = "SELECT * FROM users LIMIT ?, ?"
        return executeQueryList(sql, arrayOf(offset, pageSize)) { resultSet ->
            mapResultSetToUser(resultSet)
        }
    }
    
    /**
     * 保存用户
     */
    fun save(user: User): Long {
        val sql = """
            INSERT INTO users (
                username, password, nickname, avatar, email, phone, 
                vip_level, credits, create_time, update_time, last_login_time, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        
        val now = Timestamp(System.currentTimeMillis())
        val lastLoginTime = user.lastLoginTime?.let { Timestamp(it.time) }
        
        return executeInsertWithGeneratedKey(
            sql, arrayOf(
                user.username, user.password, user.nickname, user.avatar, 
                user.email, user.phone, user.vipLevel, user.credits,
                now, now, lastLoginTime, user.status
            )
        )
    }
    
    /**
     * 更新用户
     */
    fun update(user: User): Int {
        val sql = """
            UPDATE users SET 
                nickname = ?, avatar = ?, email = ?, phone = ?, 
                vip_level = ?, credits = ?, update_time = ?, last_login_time = ?, status = ?
            WHERE id = ?
        """.trimIndent()
        
        val now = Timestamp(System.currentTimeMillis())
        val lastLoginTime = user.lastLoginTime?.let { Timestamp(it.time) }
        
        return executeUpdate(
            sql, arrayOf(
                user.nickname, user.avatar, user.email, user.phone,
                user.vipLevel, user.credits, now, lastLoginTime, user.status, user.id
            )
        )
    }
    
    /**
     * 更新用户密码
     */
    fun updatePassword(id: Long, password: String): Int {
        val sql = "UPDATE users SET password = ?, update_time = ? WHERE id = ?"
        val now = Timestamp(System.currentTimeMillis())
        return executeUpdate(sql, arrayOf(password, now, id))
    }
    
    /**
     * 更新用户最后登录时间
     */
    fun updateLastLoginTime(id: Long): Int {
        val sql = "UPDATE users SET last_login_time = ? WHERE id = ?"
        val now = Timestamp(System.currentTimeMillis())
        return executeUpdate(sql, arrayOf(now, id))
    }
    
    /**
     * 删除用户
     */
    fun delete(id: Long): Int {
        val sql = "DELETE FROM users WHERE id = ?"
        return executeUpdate(sql, arrayOf(id))
    }
    
    /**
     * 将ResultSet映射为User对象
     */
    private fun mapResultSetToUser(resultSet: ResultSet): User {
        return User(
            id = resultSet.getLong("id"),
            username = resultSet.getString("username"),
            password = resultSet.getString("password"),
            nickname = resultSet.getString("nickname"),
            avatar = resultSet.getString("avatar"),
            email = resultSet.getString("email"),
            phone = resultSet.getString("phone"),
            vipLevel = resultSet.getInt("vip_level"),
            credits = resultSet.getInt("credits"),
            createTime = Date(resultSet.getTimestamp("create_time").time),
            updateTime = Date(resultSet.getTimestamp("update_time").time),
            lastLoginTime = resultSet.getTimestamp("last_login_time")?.let { Date(it.time) },
            status = resultSet.getInt("status")
        )
    }
} 