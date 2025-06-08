package com.lanchenjishu.aicreator.repositories

import com.lanchenjishu.aicreator.dao.CreationHistoryDao

/**
 * 创作历史仓库
 */
class CreationHistoryRepository {
    
    private val creationHistoryDao = CreationHistoryDao()
    
    /**
     * 获取创作历史DAO
     */
    fun getCreationHistoryDao(): CreationHistoryDao {
        return creationHistoryDao
    }
} 