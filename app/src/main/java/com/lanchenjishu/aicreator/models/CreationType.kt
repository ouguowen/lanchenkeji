package com.lanchenjishu.aicreator.models

/**
 * 创作类型枚举
 */
enum class CreationType(val value: Int, val displayName: String) {
    TEXT_TO_IMAGE(1, "文生图"),
    IMAGE_TO_VIDEO(2, "图生视频"),
    VOICE_CLONE(3, "声音克隆"),
    COPYWRITING(4, "文案创作"),
    DIGITAL_HUMAN(5, "数字人");
    
    companion object {
        fun fromValue(value: Int): CreationType {
            return values().find { it.value == value } ?: TEXT_TO_IMAGE
        }
    }
} 