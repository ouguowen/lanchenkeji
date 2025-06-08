package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentRegisterBinding
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        
        // 设置注册按钮点击事件
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val nickname = binding.etNickname.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (validateInput(username, nickname, password, confirmPassword)) {
                register(username, nickname, password)
            }
        }
        
        // 监听注册结果
        userViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            
            if (result > 0) {
                Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                // 注册成功，切换到登录页
                val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)
                viewPager?.currentItem = 0
            } else if (result == -1L) {
                Toast.makeText(requireContext(), R.string.username_exists, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.register_failed, Toast.LENGTH_SHORT).show()
            }
        }
        
        // 设置条款点击事件
        binding.tvTerms.setOnClickListener {
            // TODO: 显示用户协议和隐私政策
            Toast.makeText(requireContext(), "用户协议和隐私政策", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun validateInput(username: String, nickname: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        
        if (username.isEmpty()) {
            binding.tilUsername.error = "请输入用户名"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }
        
        if (nickname.isEmpty()) {
            binding.tilNickname.error = "请输入昵称"
            isValid = false
        } else {
            binding.tilNickname.error = null
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "请输入密码"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "密码至少6位"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "请确认密码"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.password_not_match)
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }
        
        return isValid
    }
    
    private fun register(username: String, nickname: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        userViewModel.register(username, password, nickname)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 