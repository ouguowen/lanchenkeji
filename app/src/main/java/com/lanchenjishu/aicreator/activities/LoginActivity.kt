package com.lanchenjishu.aicreator.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lanchenjishu.aicreator.databinding.ActivityLoginBinding
import com.lanchenjishu.aicreator.fragments.LoginFragment
import com.lanchenjishu.aicreator.fragments.RegisterFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化ViewPager
        val pagerAdapter = LoginPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        
        // 关联TabLayout和ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "登录"
                1 -> "注册"
                else -> null
            }
        }.attach()
    }
    
    /**
     * ViewPager适配器
     */
    private inner class LoginPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LoginFragment()
                1 -> RegisterFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }
} 