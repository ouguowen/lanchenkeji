package com.lanchenjishu.aicreator.models

import java.util.Date

/**
 * 创作模板模型
 */
data class CreationTemplate(
    val id: Long = 0,
    val templateName: String,
    val templateType: CreationType,
    val promptTemplate: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val isSystem: Boolean = false,
    val createTime: Date = Date(),
    val updateTime: Date? = null
) 