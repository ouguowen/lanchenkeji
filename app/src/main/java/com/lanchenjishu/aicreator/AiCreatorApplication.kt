package com.lanchenjishu.aicreator

import android.app.Application
import android.util.Log
import com.lanchenjishu.aicreator.database.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 应用程序类
 */
class AiCreatorApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val TAG = "AiCreatorApplication"
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库
        initializeDatabase()
    }
    
    /**
     * 初始化数据库
     */
    private fun initializeDatabase() {
        applicationScope.launch {
            try {
                DatabaseManager.initialize(applicationContext)
                Log.i(TAG, "数据库初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "数据库初始化失败", e)
            }
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // 关闭数据库连接
        DatabaseManager.close()
    }
} 