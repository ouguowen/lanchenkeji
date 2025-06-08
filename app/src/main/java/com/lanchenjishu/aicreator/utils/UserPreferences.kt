package com.lanchenjishu.aicreator.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.lanchenjishu.aicreator.models.User

/**
 * 用户偏好设置工具类
 * 用于存储用户登录状态和用户信息
 */
class UserPreferences(context: Context) {
    
    companion object {
        private const val PREF_NAME = "user_preferences"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER = "user"
        private const val TAG = "UserPreferences"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    /**
     * 保存登录状态
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    /**
     * 获取登录状态
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * 保存用户ID
     */
    fun setUserId(userId: Long) {
        sharedPreferences.edit().putLong(KEY_USER_ID, userId).apply()
    }
    
    /**
     * 获取用户ID
     */
    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1)
    }
    
    /**
     * 保存用户信息
     */
    fun saveUser(user: User) {
        try {
            val userJson = gson.toJson(user)
            sharedPreferences.edit().putString(KEY_USER, userJson).apply()
            setUserId(user.id)
            setLoggedIn(true)
        } catch (e: Exception) {
            Log.e(TAG, "保存用户信息失败: ${e.message}")
        }
    }
    
    /**
     * 获取用户信息
     */
    fun getUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "获取用户信息失败: ${e.message}")
                null
            }
        } else {
            null
        }
    }
    
    /**
     * 清除用户信息
     */
    fun clearUser() {
        sharedPreferences.edit()
            .remove(KEY_USER)
            .remove(KEY_USER_ID)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
} 