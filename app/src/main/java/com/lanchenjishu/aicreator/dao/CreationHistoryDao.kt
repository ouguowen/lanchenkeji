package com.lanchenjishu.aicreator.dao

import com.lanchenjishu.aicreator.database.BaseDao
import com.lanchenjishu.aicreator.database.DatabaseManager
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.Date

/**
 * 创作历史DAO
 */
class CreationHistoryDao : BaseDao() {
    
    /**
     * 根据ID获取创作历史
     */
    fun getCreationHistoryById(id: Long): CreationHistory? {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_history WHERE id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            val resultSet = preparedStatement.executeQuery()
            
            return if (resultSet.next()) {
                mapResultSetToCreationHistory(resultSet)
            } else {
                null
            }
        } finally {
            connection.close()
        }
    }
    
    /**
     * 根据用户ID获取创作历史列表
     */
    fun getCreationHistoryByUserId(userId: Long): List<CreationHistory> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_history WHERE user_id = ? ORDER BY create_time DESC"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            val resultSet = preparedStatement.executeQuery()
            
            val historyList = mutableListOf<CreationHistory>()
            while (resultSet.next()) {
                historyList.add(mapResultSetToCreationHistory(resultSet))
            }
            
            return historyList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 根据用户ID和创作类型获取创作历史列表
     */
    fun getCreationHistoryByUserIdAndType(userId: Long, type: CreationType): List<CreationHistory> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_history WHERE user_id = ? AND creation_type = ? ORDER BY create_time DESC"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            preparedStatement.setInt(2, type.value)
            val resultSet = preparedStatement.executeQuery()
            
            val historyList = mutableListOf<CreationHistory>()
            while (resultSet.next()) {
                historyList.add(mapResultSetToCreationHistory(resultSet))
            }
            
            return historyList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 分页获取创作历史列表
     */
    fun getCreationHistoryByPage(userId: Long, page: Int, pageSize: Int): List<CreationHistory> {
        val connection = DatabaseManager.getConnection()
        val offset = (page - 1) * pageSize
        val sql = "SELECT * FROM creation_history WHERE user_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, userId)
            preparedStatement.setInt(2, pageSize)
            preparedStatement.setInt(3, offset)
            val resultSet = preparedStatement.executeQuery()
            
            val historyList = mutableListOf<CreationHistory>()
            while (resultSet.next()) {
                historyList.add(mapResultSetToCreationHistory(resultSet))
            }
            
            return historyList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 插入创作历史
     * @return 插入的记录ID
     */
    fun insertCreationHistory(history: CreationHistory): Long {
        val connection = DatabaseManager.getConnection()
        val sql = """
            INSERT INTO creation_history (
                user_id, creation_type, title, prompt, thumbnail_url, result_url, 
                create_time, update_time, status, credits_cost
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        
        try {
            val preparedStatement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setLong(1, history.userId)
            preparedStatement.setInt(2, history.creationType.value)
            preparedStatement.setString(3, history.title)
            preparedStatement.setString(4, history.prompt)
            preparedStatement.setString(5, history.thumbnailUrl)
            preparedStatement.setString(6, history.resultUrl)
            preparedStatement.setTimestamp(7, Timestamp(history.createTime.time))
            preparedStatement.setTimestamp(8, history.updateTime?.let { Timestamp(it.time) })
            preparedStatement.setInt(9, history.status.value)
            preparedStatement.setInt(10, history.creditsCost)
            
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
     * 更新创作历史
     * @return 影响的行数
     */
    fun updateCreationHistory(history: CreationHistory): Int {
        val connection = DatabaseManager.getConnection()
        val sql = """
            UPDATE creation_history SET 
                user_id = ?, creation_type = ?, title = ?, prompt = ?, 
                thumbnail_url = ?, result_url = ?, create_time = ?, 
                update_time = ?, status = ?, credits_cost = ?
            WHERE id = ?
        """.trimIndent()
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, history.userId)
            preparedStatement.setInt(2, history.creationType.value)
            preparedStatement.setString(3, history.title)
            preparedStatement.setString(4, history.prompt)
            preparedStatement.setString(5, history.thumbnailUrl)
            preparedStatement.setString(6, history.resultUrl)
            preparedStatement.setTimestamp(7, Timestamp(history.createTime.time))
            preparedStatement.setTimestamp(8, history.updateTime?.let { Timestamp(it.time) })
            preparedStatement.setInt(9, history.status.value)
            preparedStatement.setInt(10, history.creditsCost)
            preparedStatement.setLong(11, history.id)
            
            return preparedStatement.executeUpdate()
        } finally {
            connection.close()
        }
    }
    
    /**
     * 删除创作历史
     * @return 影响的行数
     */
    fun deleteCreationHistory(id: Long): Int {
        val connection = DatabaseManager.getConnection()
        val sql = "DELETE FROM creation_history WHERE id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            
            return preparedStatement.executeUpdate()
        } finally {
            connection.close()
        }
    }
    
    /**
     * 将ResultSet映射为CreationHistory对象
     */
    private fun mapResultSetToCreationHistory(resultSet: ResultSet): CreationHistory {
        return CreationHistory(
            id = resultSet.getLong("id"),
            userId = resultSet.getLong("user_id"),
            creationType = CreationType.fromValue(resultSet.getInt("creation_type")),
            title = resultSet.getString("title"),
            prompt = resultSet.getString("prompt"),
            thumbnailUrl = resultSet.getString("thumbnail_url"),
            resultUrl = resultSet.getString("result_url"),
            createTime = Date(resultSet.getTimestamp("create_time").time),
            updateTime = resultSet.getTimestamp("update_time")?.let { Date(it.time) },
            status = CreationStatus.fromValue(resultSet.getInt("status")),
            creditsCost = resultSet.getInt("credits_cost")
        )
    }
} 