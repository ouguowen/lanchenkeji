package com.lanchenjishu.aicreator.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.ActivityMainBinding
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.fragments.HistoryFragment
import com.lanchenjishu.aicreator.fragments.HomeFragment
import com.lanchenjishu.aicreator.fragments.ProfileFragment
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var creditsViewModel: CreditsRecordViewModel
    
    // 当前选中的Fragment
    private var currentFragment: Fragment? = null
    
    // 缓存的Fragment实例
    private val homeFragment by lazy { HomeFragment() }
    private val createFragment by lazy { CreateFragment() }
    private val historyFragment by lazy { HistoryFragment() }
    private val profileFragment by lazy { ProfileFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        creditsViewModel = ViewModelProvider(this)[CreditsRecordViewModel::class.java]
        
        // 设置UserViewModel到CreditsRecordViewModel
        creditsViewModel.setUserViewModel(userViewModel)
        
        // 初始化Toolbar
        setSupportActionBar(binding.toolbar)
        
        // 设置底部导航栏监听
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    switchFragment(homeFragment)
                    setToolbarTitle(getString(R.string.home))
                    true
                }
                R.id.menu_create -> {
                    switchFragment(createFragment)
                    setToolbarTitle(getString(R.string.create))
                    true
                }
                R.id.menu_history -> {
                    switchFragment(historyFragment)
                    setToolbarTitle(getString(R.string.history))
                    true
                }
                R.id.menu_profile -> {
                    switchFragment(profileFragment)
                    setToolbarTitle(getString(R.string.profile))
                    true
                }
                else -> false
            }
        }
        
        // 默认选中首页
        binding.bottomNavigation.selectedItemId = R.id.menu_home
    }
    
    /**
     * 切换Fragment
     */
    private fun switchFragment(targetFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        
        // 先隐藏当前的Fragment
        currentFragment?.let {
            transaction.hide(it)
        }
        
        // 如果目标Fragment未添加，则添加
        if (!targetFragment.isAdded) {
            transaction.add(R.id.fragment_container, targetFragment)
        }
        
        // 显示目标Fragment
        transaction.show(targetFragment)
        transaction.commit()
        
        // 更新当前Fragment
        currentFragment = targetFragment
    }
    
    /**
     * 设置Toolbar标题
     */
    private fun setToolbarTitle(title: String) {
        binding.toolbar.title = title
    }
} 