package com.lanchenjishu.aicreator.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanchenjishu.aicreator.dao.CreationHistoryDao
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.repositories.CreationHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * 创作历史记录ViewModel
 */
class CreationHistoryViewModel : ViewModel() {
    
    private val historyRepository = CreationHistoryRepository()
    private val historyDao = historyRepository.getCreationHistoryDao()
    
    private val _creationHistoryList = MutableLiveData<List<CreationHistory>>()
    val creationHistoryList: LiveData<List<CreationHistory>> = _creationHistoryList
    
    // 当前创作历史
    private val _currentCreationHistory = MutableLiveData<CreationHistory?>()
    val currentCreationHistory: LiveData<CreationHistory?> = _currentCreationHistory
    
    /**
     * 根据用户ID获取创作历史
     */
    fun getCreationHistoryByUserId(userId: Long): List<CreationHistory> {
        return historyDao.getCreationHistoryByUserId(userId)
    }
    
    /**
     * 根据用户ID和创作类型获取创作历史
     */
    fun getCreationHistoryByUserIdAndType(userId: Long, type: CreationType): List<CreationHistory> {
        return historyDao.getCreationHistoryByUserIdAndType(userId, type)
    }
    
    /**
     * 创建创作历史记录
     * @return 创建的历史记录ID
     */
    fun createCreationHistory(history: CreationHistory): Long {
        return historyDao.insertCreationHistory(history)
    }
    
    /**
     * 更新创作历史记录状态
     */
    fun updateCreationHistoryStatus(historyId: Long, status: CreationStatus, resultUrl: String? = null): Boolean {
        val history = historyDao.getCreationHistoryById(historyId) ?: return false
        
        val updatedHistory = history.copy(
            status = status,
            resultUrl = resultUrl,
            updateTime = Date()
        )
        
        return historyDao.updateCreationHistory(updatedHistory) > 0
    }
    
    /**
     * 删除创作历史记录
     */
    fun deleteCreationHistory(historyId: Long): Boolean {
        return historyDao.deleteCreationHistory(historyId) > 0
    }
    
    /**
     * 加载用户的创作历史记录（异步）
     */
    fun loadCreationHistory(userId: Long) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                historyDao.getCreationHistoryByUserId(userId)
            }
            _creationHistoryList.value = result
        }
    }
    
    /**
     * 根据创作类型加载历史
     */
    fun loadCreationHistoryByType(userId: Long, creationType: CreationType) {
        viewModelScope.launch {
            try {
                val historyList = withContext(Dispatchers.IO) {
                    historyDao.getCreationHistoryByUserIdAndType(userId, creationType)
                }
                _creationHistoryList.value = historyList
            } catch (e: Exception) {
                Log.e("CreationHistoryVM", "加载创作历史失败: ${e.message}")
                _creationHistoryList.value = emptyList()
            }
        }
    }
    
    /**
     * 分页加载创作历史
     */
    fun loadCreationHistoryByPage(userId: Long, page: Int, pageSize: Int) {
        viewModelScope.launch {
            try {
                val historyList = withContext(Dispatchers.IO) {
                    historyDao.getCreationHistoryByPage(userId, page, pageSize)
                }
                _creationHistoryList.value = historyList
            } catch (e: Exception) {
                Log.e("CreationHistoryVM", "加载创作历史失败: ${e.message}")
                _creationHistoryList.value = emptyList()
            }
        }
    }
    
    /**
     * 加载创作历史详情
     */
    fun loadCreationHistoryDetail(id: Long) {
        viewModelScope.launch {
            try {
                val history = withContext(Dispatchers.IO) {
                    historyDao.getCreationHistoryById(id)
                }
                _currentCreationHistory.value = history
            } catch (e: Exception) {
                Log.e("CreationHistoryVM", "加载创作历史详情失败: ${e.message}")
                _currentCreationHistory.value = null
            }
        }
    }
} 