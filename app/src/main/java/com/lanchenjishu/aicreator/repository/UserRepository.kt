package com.lanchenjishu.aicreator.repository

import com.lanchenjishu.aicreator.database.DatabaseManager
import com.lanchenjishu.aicreator.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 用户仓库
 * 处理用户相关业务逻辑
 */
class UserRepository {
    private val userDao = DatabaseManager.getUserDao()
    
    /**
     * 根据ID获取用户
     */
    suspend fun getUserById(id: Long): User? = withContext(Dispatchers.IO) {
        userDao.findById(id)
    }
    
    /**
     * 根据用户名获取用户
     */
    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.findByUsername(username)
    }
    
    /**
     * 获取所有用户
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.findAll()
    }
    
    /**
     * 分页获取用户
     */
    suspend fun getUsersByPage(page: Int, pageSize: Int): List<User> = withContext(Dispatchers.IO) {
        userDao.findByPage(page, pageSize)
    }
    
    /**
     * 创建用户
     */
    suspend fun createUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.save(user)
    }
    
    /**
     * 更新用户
     */
    suspend fun updateUser(user: User): Boolean = withContext(Dispatchers.IO) {
        userDao.update(user) > 0
    }
    
    /**
     * 更新用户密码
     */
    suspend fun updatePassword(id: Long, password: String): Boolean = withContext(Dispatchers.IO) {
        userDao.updatePassword(id, password) > 0
    }
    
    /**
     * 更新用户最后登录时间
     */
    suspend fun updateLastLoginTime(id: Long): Boolean = withContext(Dispatchers.IO) {
        userDao.updateLastLoginTime(id) > 0
    }
    
    /**
     * 删除用户
     */
    suspend fun deleteUser(id: Long): Boolean = withContext(Dispatchers.IO) {
        userDao.delete(id) > 0
    }
    
    /**
     * 用户登录
     */
    suspend fun login(username: String, password: String): User? = withContext(Dispatchers.IO) {
        val user = userDao.findByUsername(username)
        
        if (user != null && user.password == password) {
            // 更新最后登录时间
            userDao.updateLastLoginTime(user.id)
            return@withContext user
        }
        
        return@withContext null
    }
    
    /**
     * 用户注册
     */
    suspend fun register(username: String, password: String, nickname: String): Long = withContext(Dispatchers.IO) {
        // 检查用户名是否已存在
        val existingUser = userDao.findByUsername(username)
        if (existingUser != null) {
            return@withContext -1L
        }
        
        // 创建新用户
        val user = User(
            username = username,
            password = password,
            nickname = nickname,
            createTime = Date(),
            updateTime = Date()
        )
        
        return@withContext userDao.save(user)
    }
} 