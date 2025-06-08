package com.lanchenjishu.aicreator.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lanchenjishu.aicreator.models.User
import com.lanchenjishu.aicreator.repository.UserRepository
import com.lanchenjishu.aicreator.utils.UserPreferences
import kotlinx.coroutines.launch

/**
 * 用户ViewModel
 * 处理用户相关业务逻辑
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository = UserRepository()
    private val userPreferences = UserPreferences(application)
    
    // 登录结果LiveData
    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult
    
    // 注册结果LiveData
    private val _registerResult = MutableLiveData<Long>()
    val registerResult: LiveData<Long> = _registerResult
    
    // 当前用户
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    init {
        // 从本地获取用户信息
        _currentUser.value = userPreferences.getUser()
    }
    
    /**
     * 登录
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.login(username, password)
                
                if (user != null) {
                    // 保存用户信息
                    userPreferences.saveUser(user)
                    _currentUser.value = user
                }
                
                _loginResult.value = user
            } catch (e: Exception) {
                Log.e("UserViewModel", "登录失败: ${e.message}")
                _loginResult.value = null
            }
        }
    }
    
    /**
     * 注册
     */
    fun register(username: String, password: String, nickname: String) {
        viewModelScope.launch {
            try {
                val userId = userRepository.register(username, password, nickname)
                _registerResult.value = userId
            } catch (e: Exception) {
                Log.e("UserViewModel", "注册失败: ${e.message}")
                _registerResult.value = -2
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        userPreferences.clearUser()
        _currentUser.value = null
    }
    
    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        viewModelScope.launch {
            try {
                val userId = userPreferences.getUserId()
                if (userId > 0) {
                    val user = userRepository.getUserById(userId)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "获取用户信息失败: ${e.message}")
            }
        }
    }
    
    /**
     * 更新用户信息
     */
    fun updateUserInfo(user: User) {
        viewModelScope.launch {
            try {
                val success = userRepository.updateUser(user)
                if (success) {
                    userPreferences.saveUser(user)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "更新用户信息失败: ${e.message}")
            }
        }
    }
} 