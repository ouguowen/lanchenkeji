package com.lanchenjishu.aicreator.models

/**
 * 创作状态枚举
 */
enum class CreationStatus(val value: Int, val displayName: String) {
    PENDING(0, "处理中"),
    COMPLETED(1, "已完成"),
    FAILED(2, "失败");
    
    companion object {
        fun fromValue(value: Int): CreationStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
} 