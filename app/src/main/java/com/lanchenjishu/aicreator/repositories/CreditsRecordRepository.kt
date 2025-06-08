package com.lanchenjishu.aicreator.repositories

import com.lanchenjishu.aicreator.dao.CreditsRecordDao
import com.lanchenjishu.aicreator.dao.UserDao
import com.lanchenjishu.aicreator.models.CreditsRecord
import com.lanchenjishu.aicreator.models.CreditsRecordType
import java.util.Date

/**
 * 积分记录仓库
 */
class CreditsRecordRepository {
    
    private val creditsRecordDao = CreditsRecordDao()
    private val userDao = UserDao()
    
    /**
     * 获取用户的积分记录
     */
    fun getUserCreditsRecords(userId: Long, limit: Int = 50, offset: Int = 0): List<CreditsRecord> {
        return creditsRecordDao.getUserCreditsRecords(userId, limit, offset)
    }
    
    /**
     * 获取用户的积分记录数量
     */
    fun getUserCreditsRecordCount(userId: Long): Int {
        return creditsRecordDao.getUserCreditsRecordCount(userId)
    }
    
    /**
     * 按类型获取用户的积分记录
     */
    fun getUserCreditsRecordsByType(userId: Long, type: CreditsRecordType, limit: Int = 50, offset: Int = 0): List<CreditsRecord> {
        return creditsRecordDao.getUserCreditsRecordsByType(userId, type, limit, offset)
    }
    
    /**
     * 创建一条积分变动记录
     * @param userId 用户ID
     * @param amount 变动金额（正数表示增加，负数表示减少）
     * @param type 变动类型
     * @param description 变动描述
     * @param relatedId 关联ID
     * @return 创建成功返回记录ID，失败返回-1
     */
    fun createCreditsRecord(userId: Long, amount: Int, type: CreditsRecordType, description: String, relatedId: Long? = null): Long {
        // 获取用户当前积分
        val user = userDao.getUserById(userId) ?: return -1
        
        // 计算变动后的积分
        val newBalance = user.credits + amount
        
        // 更新用户积分
        val updateResult = userDao.updateUserCredits(userId, newBalance)
        if (updateResult <= 0) {
            return -1
        }
        
        // 创建积分记录
        val record = CreditsRecord(
            userId = userId,
            amount = amount,
            balance = newBalance,
            type = type,
            description = description,
            relatedId = relatedId,
            createTime = Date()
        )
        
        return creditsRecordDao.insertCreditsRecord(record)
    }
    
    /**
     * 充值积分
     */
    fun rechargeCredits(userId: Long, amount: Int, description: String, orderId: Long? = null): Long {
        if (amount <= 0) {
            return -1
        }
        
        return createCreditsRecord(
            userId = userId,
            amount = amount,
            type = CreditsRecordType.RECHARGE,
            description = description,
            relatedId = orderId
        )
    }
    
    /**
     * 消费积分
     */
    fun consumeCredits(userId: Long, amount: Int, description: String, creationId: Long? = null): Long {
        if (amount <= 0) {
            return -1
        }
        
        return createCreditsRecord(
            userId = userId,
            amount = -amount, // 消费是减少积分，所以用负数
            type = CreditsRecordType.CONSUMPTION,
            description = description,
            relatedId = creationId
        )
    }
    
    /**
     * 奖励积分
     */
    fun rewardCredits(userId: Long, amount: Int, description: String, relatedId: Long? = null): Long {
        if (amount <= 0) {
            return -1
        }
        
        return createCreditsRecord(
            userId = userId,
            amount = amount,
            type = CreditsRecordType.REWARD,
            description = description,
            relatedId = relatedId
        )
    }
    
    /**
     * 退款积分
     */
    fun refundCredits(userId: Long, amount: Int, description: String, creationId: Long? = null): Long {
        if (amount <= 0) {
            return -1
        }
        
        return createCreditsRecord(
            userId = userId,
            amount = amount,
            type = CreditsRecordType.REFUND,
            description = description,
            relatedId = creationId
        )
    }
} 