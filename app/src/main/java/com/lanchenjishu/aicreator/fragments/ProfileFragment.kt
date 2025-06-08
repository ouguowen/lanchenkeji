package com.lanchenjishu.aicreator.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.activities.LoginActivity
import com.lanchenjishu.aicreator.databinding.FragmentProfileBinding
import com.lanchenjishu.aicreator.fragments.RechargeFragment
import com.lanchenjishu.aicreator.fragments.CreditsRecordFragment
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var creditsViewModel: CreditsRecordViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        creditsViewModel = ViewModelProvider(requireActivity())[CreditsRecordViewModel::class.java]
        
        // 设置UserViewModel到CreditsRecordViewModel
        creditsViewModel.setUserViewModel(userViewModel)
        
        // 设置点击事件
        setupClickListeners()
        
        // 监听用户数据变化
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // 显示用户信息
                binding.tvUsername.text = user.username
                binding.tvNickname.text = user.nickname
                binding.tvCredits.text = getString(R.string.credits, user.credits)
                binding.tvVipLevel.text = getString(R.string.vip_level, user.vipLevel)
                
                // 加载头像
                if (!user.avatar.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(user.avatar)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(binding.ivAvatar)
                }
                
                // 显示用户面板，隐藏登录按钮
                binding.layoutUserInfo.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
            } else {
                // 隐藏用户面板，显示登录按钮
                binding.layoutUserInfo.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
            }
        }
    }
    
    private fun setupClickListeners() {
        // 登录按钮点击事件
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        
        // 退出登录按钮点击事件
        binding.btnLogout.setOnClickListener {
            userViewModel.logout()
            Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show()
        }
        
        // 积分记录按钮（小按钮）点击事件
        binding.btnCreditsRecord.setOnClickListener {
            if (userViewModel.currentUser.value != null) {
                navigateToCreditsRecordFragment()
            } else {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
        
        // 积分记录按钮（菜单项）点击事件
        binding.btnCreditsHistory.setOnClickListener {
            if (userViewModel.currentUser.value != null) {
                navigateToCreditsRecordFragment()
            } else {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
        
        // 充值按钮点击事件
        binding.btnRecharge.setOnClickListener {
            if (userViewModel.currentUser.value != null) {
                // 打开充值页面
                navigateToRechargeFragment()
            } else {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
        
        // 开通会员按钮点击事件
        binding.btnVip.setOnClickListener {
            // TODO: 实现开通会员功能
            Toast.makeText(requireContext(), "会员功能开发中", Toast.LENGTH_SHORT).show()
        }
        
        // 设置按钮点击事件
        binding.btnSettings.setOnClickListener {
            // TODO: 跳转到设置页面
            Toast.makeText(requireContext(), "设置功能开发中", Toast.LENGTH_SHORT).show()
        }
        
        // 关于按钮点击事件
        binding.btnAbout.setOnClickListener {
            // TODO: 显示关于信息
            Toast.makeText(requireContext(), "AI创作助手 v1.0", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 跳转到充值页面
     */
    private fun navigateToRechargeFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RechargeFragment())
            .addToBackStack(null)
            .commit()
    }
    
    /**
     * 跳转到积分记录页面
     */
    private fun navigateToCreditsRecordFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CreditsRecordFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 