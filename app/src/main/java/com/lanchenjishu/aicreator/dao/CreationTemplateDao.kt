package com.lanchenjishu.aicreator.dao

import com.lanchenjishu.aicreator.database.BaseDao
import com.lanchenjishu.aicreator.database.DatabaseManager
import com.lanchenjishu.aicreator.models.CreationTemplate
import com.lanchenjishu.aicreator.models.CreationType
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.Date

/**
 * 创作模板DAO
 */
class CreationTemplateDao : BaseDao() {
    
    /**
     * 根据ID获取创作模板
     */
    fun getTemplateById(id: Long): CreationTemplate? {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_template WHERE id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            val resultSet = preparedStatement.executeQuery()
            
            return if (resultSet.next()) {
                mapResultSetToTemplate(resultSet)
            } else {
                null
            }
        } finally {
            connection.close()
        }
    }
    
    /**
     * 获取所有模板
     */
    fun getAllTemplates(): List<CreationTemplate> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_template ORDER BY create_time DESC"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            val resultSet = preparedStatement.executeQuery()
            
            val templateList = mutableListOf<CreationTemplate>()
            while (resultSet.next()) {
                templateList.add(mapResultSetToTemplate(resultSet))
            }
            
            return templateList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 根据类型获取模板
     */
    fun getTemplatesByType(type: CreationType): List<CreationTemplate> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_template WHERE template_type = ? ORDER BY create_time DESC"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, type.value)
            val resultSet = preparedStatement.executeQuery()
            
            val templateList = mutableListOf<CreationTemplate>()
            while (resultSet.next()) {
                templateList.add(mapResultSetToTemplate(resultSet))
            }
            
            return templateList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 获取系统模板
     */
    fun getSystemTemplates(): List<CreationTemplate> {
        val connection = DatabaseManager.getConnection()
        val sql = "SELECT * FROM creation_template WHERE is_system = TRUE ORDER BY create_time DESC"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            val resultSet = preparedStatement.executeQuery()
            
            val templateList = mutableListOf<CreationTemplate>()
            while (resultSet.next()) {
                templateList.add(mapResultSetToTemplate(resultSet))
            }
            
            return templateList
        } finally {
            connection.close()
        }
    }
    
    /**
     * 插入创作模板
     * @return 插入的记录ID
     */
    fun insertTemplate(template: CreationTemplate): Long {
        val connection = DatabaseManager.getConnection()
        val sql = """
            INSERT INTO creation_template (
                template_name, template_type, prompt_template, description, 
                thumbnail_url, is_system, create_time, update_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        
        try {
            val preparedStatement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setString(1, template.templateName)
            preparedStatement.setInt(2, template.templateType.value)
            preparedStatement.setString(3, template.promptTemplate)
            preparedStatement.setString(4, template.description)
            preparedStatement.setString(5, template.thumbnailUrl)
            preparedStatement.setBoolean(6, template.isSystem)
            preparedStatement.setTimestamp(7, Timestamp(template.createTime.time))
            preparedStatement.setTimestamp(8, template.updateTime?.let { Timestamp(it.time) })
            
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
     * 更新创作模板
     * @return 影响的行数
     */
    fun updateTemplate(template: CreationTemplate): Int {
        val connection = DatabaseManager.getConnection()
        val sql = """
            UPDATE creation_template SET 
                template_name = ?, template_type = ?, prompt_template = ?, 
                description = ?, thumbnail_url = ?, is_system = ?, 
                update_time = ?
            WHERE id = ?
        """.trimIndent()
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, template.templateName)
            preparedStatement.setInt(2, template.templateType.value)
            preparedStatement.setString(3, template.promptTemplate)
            preparedStatement.setString(4, template.description)
            preparedStatement.setString(5, template.thumbnailUrl)
            preparedStatement.setBoolean(6, template.isSystem)
            preparedStatement.setTimestamp(7, Timestamp(Date().time))
            preparedStatement.setLong(8, template.id)
            
            return preparedStatement.executeUpdate()
        } finally {
            connection.close()
        }
    }
    
    /**
     * 删除创作模板
     * @return 影响的行数
     */
    fun deleteTemplate(id: Long): Int {
        val connection = DatabaseManager.getConnection()
        val sql = "DELETE FROM creation_template WHERE id = ?"
        
        try {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            
            return preparedStatement.executeUpdate()
        } finally {
            connection.close()
        }
    }
    
    /**
     * 将ResultSet映射为CreationTemplate对象
     */
    private fun mapResultSetToTemplate(resultSet: ResultSet): CreationTemplate {
        return CreationTemplate(
            id = resultSet.getLong("id"),
            templateName = resultSet.getString("template_name"),
            templateType = CreationType.fromValue(resultSet.getInt("template_type")),
            promptTemplate = resultSet.getString("prompt_template"),
            description = resultSet.getString("description"),
            thumbnailUrl = resultSet.getString("thumbnail_url"),
            isSystem = resultSet.getBoolean("is_system"),
            createTime = Date(resultSet.getTimestamp("create_time").time),
            updateTime = resultSet.getTimestamp("update_time")?.let { Date(it.time) }
        )
    }
} 