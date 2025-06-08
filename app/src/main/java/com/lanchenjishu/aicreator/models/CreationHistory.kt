package com.lanchenjishu.aicreator.models

import java.util.Date

/**
 * 创作历史记录模型
 */
data class CreationHistory(
    val id: Long = 0,
    val userId: Long,
    val creationType: CreationType,
    val title: String,
    val prompt: String,
    val thumbnailUrl: String? = null,
    val resultUrl: String? = null,
    val createTime: Date,
    val updateTime: Date? = null,
    val status: CreationStatus,
    val creditsCost: Int
)

/**
 * 创作类型枚举
 */
enum class CreationType {
    TEXT_TO_IMAGE,    // 文生图
    IMAGE_TO_VIDEO,   // 图生视频
    VOICE_CLONE,      // 声音克隆
    COPYWRITING,      // 文案创作
    DIGITAL_HUMAN     // 数字人生成
}

/**
 * 创作状态枚举
 */
enum class CreationStatus {
    PENDING,       // 等待中
    PROCESSING,    // 处理中
    COMPLETED,     // 已完成
    FAILED         // 失败
} 