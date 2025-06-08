package com.lanchenjishu.aicreator.models

import java.util.Date

/**
 * 积分记录类型枚举
 */
enum class CreditsRecordType(val value: String, val displayName: String) {
    RECHARGE("RECHARGE", "充值"),
    CONSUMPTION("CONSUMPTION", "消费"),
    REWARD("REWARD", "奖励"),
    REFUND("REFUND", "退款");
    
    companion object {
        fun fromValue(value: String): CreditsRecordType {
            return values().find { it.value == value } ?: CONSUMPTION
        }
    }
}

/**
 * 积分记录模型
 */
data class CreditsRecord(
    val id: Long = 0,
    val userId: Long,
    val amount: Int,         // 积分变动数量，正数表示增加，负数表示减少
    val balance: Int,        // 变动后的积分余额
    val type: CreditsRecordType,
    val description: String,
    val relatedId: Long? = null,  // 关联ID，如创作ID、订单ID等
    val createTime: Date = Date()
) 