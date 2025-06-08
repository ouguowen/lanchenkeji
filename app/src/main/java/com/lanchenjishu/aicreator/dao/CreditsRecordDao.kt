package com.lanchenjishu.aicreator.dao

import com.lanchenjishu.aicreator.database.BaseDao
import com.lanchenjishu.aicreator.database.DatabaseManager
import com.lanchenjishu.aicreator.models.CreditsRecord
import com.lanchenjishu.aicreator.models.CreditsRecordType
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.Date

/**
 * 积分记录DAO
 */
class CreditsRecordDao : BaseDao() {
    
    /**
     * 根据ID获取积分记录
     */
    fun getCreditsRecordById(id: Long): CreditsRecord? {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM credits_records WHERE id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            val resultSet = preparedStatement.executeQuery()
            
            return if (resultSet.next()) {
                mapResultSetToCreditsRecord(resultSet)
            } else {
                null
            }
        } finally {
            connection.close()
        }
    }
    
    /**
     * 获取用户的积分记录
     */
    fun getUserCreditsRecords(userId: Long, limit: Int = 50, offset: Int = 0): List<CreditsRecord> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM credits_records WHERE user_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            preparedStatement.setInt(2, limit)
            preparedStatement.setInt(3, offset)
            val resultSet = preparedStatement.executeQuery()
            
            val records = mutableListOf<CreditsRecord>()
            while (resultSet.next()) {
                records.add(mapResultSetToCreditsRecord(resultSet))
            }
            
            return records
        } finally {
            connection.close()
        }
    }
    
    /**
     * 获取用户的积分记录数量
     */
    fun getUserCreditsRecordCount(userId: Long): Int {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT COUNT(*) FROM credits_records WHERE user_id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            val resultSet = preparedStatement.executeQuery()
            
            return if (resultSet.next()) {
                resultSet.getInt(1)
            } else {
                0
            }
        } finally {
            connection.close()
        }
    }
    
    /**
     * 按类型获取用户的积分记录
     */
    fun getUserCreditsRecordsByType(userId: Long, type: CreditsRecordType, limit: Int = 50, offset: Int = 0): List<CreditsRecord> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM credits_records WHERE user_id = ? AND type = ? ORDER BY create_time DESC LIMIT ? OFFSET ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            preparedStatement.setString(2, type.value)
            preparedStatement.setInt(3, limit)
            preparedStatement.setInt(4, offset)
            val resultSet = preparedStatement.executeQuery()
            
            val records = mutableListOf<CreditsRecord>()
            while (resultSet.next()) {
                records.add(mapResultSetToCreditsRecord(resultSet))
            }
            
            return records
        } finally {
            connection.close()
        }
    }
    
    /**
     * 插入积分记录
     * @return 插入的记录ID
     */
    fun insertCreditsRecord(record: CreditsRecord): Long {
        val connection = DatabaseManager.getConnection()
        val sql = """
            INSERT INTO credits_records (
                user_id, amount, balance, type, description, related_id, create_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        
        try {
            val preparedStatement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setLong(1, record.userId)
            preparedStatement.setInt(2, record.amount)
            preparedStatement.setInt(3, record.balance)
            preparedStatement.setString(4, record.type.value)
            preparedStatement.setString(5, record.description)
            preparedStatement.setObject(6, record.relatedId)
            preparedStatement.setTimestamp(7, Timestamp(record.createTime.time))
            
            preparedStatement.executeUpdate()
            
            // 获取生成的ID
            val generatedKeys = preparedStatement.generatedKeys
            return if (generatedKeys.next()) {
                generatedKeys.getLong(1)
            } else {
                -1
            }
        } finally {
            connection.close()
        }
    }
    
    /**
     * 将ResultSet映射为CreditsRecord对象
     */
    private fun mapResultSetToCreditsRecord(resultSet: ResultSet): CreditsRecord {
        return CreditsRecord(
            id = resultSet.getLong("id"),
            userId = resultSet.getLong("user_id"),
            amount = resultSet.getInt("amount"),
            balance = resultSet.getInt("balance"),
            type = CreditsRecordType.fromValue(resultSet.getString("type")),
            description = resultSet.getString("description"),
            relatedId = resultSet.getObject("related_id")?.let { it as Long },
            createTime = Date(resultSet.getTimestamp("create_time").time)
        )
    }
} 