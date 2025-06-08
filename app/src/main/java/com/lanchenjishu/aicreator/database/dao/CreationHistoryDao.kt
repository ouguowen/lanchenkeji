package com.lanchenjishu.aicreator.database.dao

import com.lanchenjishu.aicreator.database.BaseDao
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

/**
 * 创作历史数据访问对象
 */
class CreationHistoryDao : BaseDao() {
    
    /**
     * 根据ID查询创作历史
     */
    fun findById(id: Long): CreationHistory? {
        val sql = "SELECT * FROM creation_history WHERE id = ?"
        return executeQuery(sql, arrayOf(id)) { resultSet ->
            mapResultSetToCreationHistory(resultSet)
        }
    }
    
    /**
     * 根据用户ID查询创作历史列表
     */
    fun findByUserId(userId: Long): List<CreationHistory> {
        val sql = "SELECT * FROM creation_history WHERE user_id = ? ORDER BY create_time DESC"
        return executeQueryList(sql, arrayOf(userId)) { resultSet ->
            mapResultSetToCreationHistory(resultSet)
        }
    }
    
    /**
     * 根据用户ID和创作类型查询创作历史列表
     */
    fun findByUserIdAndType(userId: Long, creationType: CreationType): List<CreationHistory> {
        val sql = "SELECT * FROM creation_history WHERE user_id = ? AND creation_type = ? ORDER BY create_time DESC"
        return executeQueryList(sql, arrayOf(userId, creationType.name)) { resultSet ->
            mapResultSetToCreationHistory(resultSet)
        }
    }
    
    /**
     * 分页查询创作历史
     */
    fun findByPage(userId: Long, page: Int, pageSize: Int): List<CreationHistory> {
        val offset = (page - 1) * pageSize
        val sql = "SELECT * FROM creation_history WHERE user_id = ? ORDER BY create_time DESC LIMIT ?, ?"
        return executeQueryList(sql, arrayOf(userId, offset, pageSize)) { resultSet ->
            mapResultSetToCreationHistory(resultSet)
        }
    }
    
    /**
     * 保存创作历史
     */
    fun save(creationHistory: CreationHistory): Long {
        val sql = """
            INSERT INTO creation_history (
                user_id, creation_type, title, prompt, result_url, 
                thumbnail_url, create_time, status, credits_cost
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        
        val createTime = Timestamp(creationHistory.createTime.time)
        
        return executeInsertWithGeneratedKey(
            sql, arrayOf(
                creationHistory.userId,
                creationHistory.creationType.name,
                creationHistory.title,
                creationHistory.prompt,
                creationHistory.resultUrl,
                creationHistory.thumbnailUrl,
                createTime,
                creationHistory.status.name,
                creationHistory.creditsCost
            )
        )
    }
    
    /**
     * 更新创作历史
     */
    fun update(creationHistory: CreationHistory): Int {
        val sql = """
            UPDATE creation_history SET 
                title = ?, prompt = ?, result_url = ?, thumbnail_url = ?, 
                status = ?, credits_cost = ?
            WHERE id = ?
        """.trimIndent()
        
        return executeUpdate(
            sql, arrayOf(
                creationHistory.title,
                creationHistory.prompt,
                creationHistory.resultUrl,
                creationHistory.thumbnailUrl,
                creationHistory.status.name,
                creationHistory.creditsCost,
                creationHistory.id
            )
        )
    }
    
    /**
     * 更新创作状态
     */
    fun updateStatus(id: Long, status: CreationStatus, resultUrl: String? = null): Int {
        val sql = if (resultUrl != null) {
            "UPDATE creation_history SET status = ?, result_url = ? WHERE id = ?"
        } else {
            "UPDATE creation_history SET status = ? WHERE id = ?"
        }
        
        return if (resultUrl != null) {
            executeUpdate(sql, arrayOf(status.name, resultUrl, id))
        } else {
            executeUpdate(sql, arrayOf(status.name, id))
        }
    }
    
    /**
     * 删除创作历史
     */
    fun delete(id: Long): Int {
        val sql = "DELETE FROM creation_history WHERE id = ?"
        return executeUpdate(sql, arrayOf(id))
    }
    
    /**
     * 将ResultSet映射为CreationHistory对象
     */
    private fun mapResultSetToCreationHistory(resultSet: ResultSet): CreationHistory {
        return CreationHistory(
            id = resultSet.getLong("id"),
            userId = resultSet.getLong("user_id"),
            creationType = CreationType.valueOf(resultSet.getString("creation_type")),
            title = resultSet.getString("title"),
            prompt = resultSet.getString("prompt"),
            resultUrl = resultSet.getString("result_url"),
            thumbnailUrl = resultSet.getString("thumbnail_url"),
            createTime = Date(resultSet.getTimestamp("create_time").time),
            status = CreationStatus.valueOf(resultSet.getString("status")),
            creditsCost = resultSet.getInt("credits_cost")
        )
    }
} 