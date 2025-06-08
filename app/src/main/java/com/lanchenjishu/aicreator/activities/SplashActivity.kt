package com.lanchenjishu.aicreator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.database.DatabaseInitializer
import com.lanchenjishu.aicreator.databinding.ActivitySplashBinding
import com.lanchenjishu.aicreator.repositories.CreationTemplateRepository
import com.lanchenjishu.aicreator.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 启动动画
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.ivLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(fadeIn)
        binding.tvSlogan.startAnimation(fadeIn)
        
        // 初始化用户偏好设置
        val userPreferences = UserPreferences(this)
        
        // 延迟跳转
        lifecycleScope.launch {
            try {
                // 在后台线程初始化数据库
                withContext(Dispatchers.IO) {
                    try {
                        // 初始化数据库表结构
                        DatabaseInitializer.initialize()
                        
                        // 初始化预设模板
                        val templateRepository = CreationTemplateRepository()
                        templateRepository.initializeDefaultTemplates()
                        
                        Log.i(TAG, "数据库和模板初始化成功")
                    } catch (e: Exception) {
                        Log.e(TAG, "数据库初始化错误: ${e.message}")
                    }
                }
                
                delay(2000) // 2秒延迟
                
                // 判断是否已登录
                if (userPreferences.isLoggedIn()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "跳转错误: ${e.message}")
            }
        }
    }
} 