package com.lanchenjishu.aicreator.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * 数据库操作基类
 * 提供通用的数据库操作方法
 */
abstract class BaseDao {
    
    /**
     * 执行查询操作
     * @param sql SQL语句
     * @param params 参数列表
     * @param resultHandler 结果处理器
     */
    protected fun <T> executeQuery(
        sql: String, 
        params: Array<Any?> = emptyArray(),
        resultHandler: (ResultSet) -> T
    ): T? {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            connection = DatabaseConfig.getConnection()
            statement = connection.prepareStatement(sql)
            
            // 设置参数
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            
            // 执行查询
            resultSet = statement.executeQuery()
            
            // 处理结果
            return if (resultSet.next()) {
                resultHandler(resultSet)
            } else {
                null
            }
        } catch (e: SQLException) {
            throw RuntimeException("执行查询操作失败: $sql", e)
        } finally {
            closeResources(resultSet, statement, connection)
        }
    }
    
    /**
     * 执行查询操作，返回列表
     * @param sql SQL语句
     * @param params 参数列表
     * @param resultHandler 结果处理器
     */
    protected fun <T> executeQueryList(
        sql: String,
        params: Array<Any?> = emptyArray(),
        resultHandler: (ResultSet) -> T
    ): List<T> {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            connection = DatabaseConfig.getConnection()
            statement = connection.prepareStatement(sql)
            
            // 设置参数
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            
            // 执行查询
            resultSet = statement.executeQuery()
            
            // 处理结果
            val resultList = mutableListOf<T>()
            while (resultSet.next()) {
                resultList.add(resultHandler(resultSet))
            }
            
            return resultList
        } catch (e: SQLException) {
            throw RuntimeException("执行查询列表操作失败: $sql", e)
        } finally {
            closeResources(resultSet, statement, connection)
        }
    }
    
    /**
     * 执行更新操作（插入、更新、删除）
     * @param sql SQL语句
     * @param params 参数列表
     * @return 影响的行数
     */
    protected fun executeUpdate(
        sql: String,
        params: Array<Any?> = emptyArray()
    ): Int {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        
        try {
            connection = DatabaseConfig.getConnection()
            statement = connection.prepareStatement(sql)
            
            // 设置参数
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            
            // 执行更新
            return statement.executeUpdate()
        } catch (e: SQLException) {
            throw RuntimeException("执行更新操作失败: $sql", e)
        } finally {
            closeResources(null, statement, connection)
        }
    }
    
    /**
     * 执行插入操作并返回生成的主键
     * @param sql SQL语句
     * @param params 参数列表
     * @return 生成的主键
     */
    protected fun executeInsertWithGeneratedKey(
        sql: String,
        params: Array<Any?> = emptyArray()
    ): Long {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            connection = DatabaseConfig.getConnection()
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
            
            // 设置参数
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            
            // 执行更新
            statement.executeUpdate()
            
            // 获取生成的主键
            resultSet = statement.generatedKeys
            return if (resultSet.next()) {
                resultSet.getLong(1)
            } else {
                -1
            }
        } catch (e: SQLException) {
            throw RuntimeException("执行插入操作失败: $sql", e)
        } finally {
            closeResources(resultSet, statement, connection)
        }
    }
    
    /**
     * 关闭资源
     */
    private fun closeResources(
        resultSet: ResultSet?,
        statement: PreparedStatement?,
        connection: Connection?
    ) {
        try {
            resultSet?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        
        try {
            statement?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
} 