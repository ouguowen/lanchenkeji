package com.lanchenjishu.aicreator.models

import java.util.Date

/**
 * 用户数据模型
 */
data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val nickname: String,
    val avatar: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val vipLevel: Int = 0,
    val credits: Int = 0,
    val createTime: Date = Date(),
    val updateTime: Date = Date(),
    val lastLoginTime: Date? = null,
    val status: Int = 1 // 1: 正常, 0: 禁用
) 