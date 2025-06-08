package com.lanchenjishu.aicreator.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanchenjishu.aicreator.models.CreditsRecord
import com.lanchenjishu.aicreator.models.CreditsRecordType
import com.lanchenjishu.aicreator.repositories.CreditsRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 积分记录ViewModel
 */
class CreditsRecordViewModel : ViewModel() {
    
    private val repository = CreditsRecordRepository()
    
    private val _creditsRecords = MutableLiveData<List<CreditsRecord>>()
    val creditsRecords: LiveData<List<CreditsRecord>> = _creditsRecords
    
    private val _recordCount = MutableLiveData<Int>()
    val recordCount: LiveData<Int> = _recordCount
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _rechargeResult = MutableLiveData<RechargeResult>()
    val rechargeResult: LiveData<RechargeResult> = _rechargeResult
    
    /**
     * 获取用户的积分记录
     */
    fun getUserCreditsRecords(userId: Long, limit: Int = 50, offset: Int = 0) {
        _loading.value = true
        
        viewModelScope.launch {
            try {
                val records = withContext(Dispatchers.IO) {
                    repository.getUserCreditsRecords(userId, limit, offset)
                }
                
                _creditsRecords.value = records
                
                // 获取记录总数
                val count = withContext(Dispatchers.IO) {
                    repository.getUserCreditsRecordCount(userId)
                }
                
                _recordCount.value = count
            } catch (e: Exception) {
                _error.value = e.message ?: "获取积分记录失败"
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * 按类型获取用户的积分记录
     */
    fun getUserCreditsRecordsByType(userId: Long, type: CreditsRecordType, limit: Int = 50, offset: Int = 0) {
        _loading.value = true
        
        viewModelScope.launch {
            try {
                val records = withContext(Dispatchers.IO) {
                    repository.getUserCreditsRecordsByType(userId, type, limit, offset)
                }
                
                _creditsRecords.value = records
            } catch (e: Exception) {
                _error.value = e.message ?: "获取积分记录失败"
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * 充值积分
     */
    fun rechargeCredits(userId: Long, amount: Int, description: String) {
        _loading.value = true
        
        viewModelScope.launch {
            try {
                val recordId = withContext(Dispatchers.IO) {
                    repository.rechargeCredits(userId, amount, description)
                }
                
                if (recordId > 0) {
                    _rechargeResult.value = RechargeResult(true, "充值成功")
                    
                    // 刷新积分记录
                    getUserCreditsRecords(userId)
                } else {
                    _rechargeResult.value = RechargeResult(false, "充值失败")
                }
            } catch (e: Exception) {
                _rechargeResult.value = RechargeResult(false, e.message ?: "充值失败")
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * 模拟支付
     */
    fun simulatePayment(userId: Long, amount: Int, packageName: String): Boolean {
        // 这里模拟支付过程，实际应用中应对接支付SDK
        val description = "购买$amount积分 - $packageName套餐"
        
        viewModelScope.launch {
            _loading.value = true
            
            try {
                val recordId = withContext(Dispatchers.IO) {
                    repository.rechargeCredits(userId, amount, description)
                }
                
                if (recordId > 0) {
                    _rechargeResult.value = RechargeResult(true, "充值成功")
                    
                    // 刷新积分记录
                    getUserCreditsRecords(userId)
                } else {
                    _rechargeResult.value = RechargeResult(false, "充值失败")
                }
            } catch (e: Exception) {
                _rechargeResult.value = RechargeResult(false, e.message ?: "充值失败")
            } finally {
                _loading.value = false
            }
        }
        
        return true
    }
    
    /**
     * 消费积分
     */
    fun consumeCredits(userId: Long, amount: Int, description: String, creationId: Long? = null): Boolean {
        var success = false
        
        viewModelScope.launch {
            _loading.value = true
            
            try {
                val recordId = withContext(Dispatchers.IO) {
                    repository.consumeCredits(userId, amount, description, creationId)
                }
                
                success = recordId > 0
                
                // 刷新积分记录
                if (success) {
                    getUserCreditsRecords(userId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "积分消费失败"
            } finally {
                _loading.value = false
            }
        }
        
        return success
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * 充值结果
     */
    data class RechargeResult(
        val success: Boolean,
        val message: String
    )
} 