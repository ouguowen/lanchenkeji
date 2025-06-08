package com.lanchenjishu.aicreator.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.activities.MainActivity
import com.lanchenjishu.aicreator.databinding.FragmentLoginBinding
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        
        // 设置登录按钮点击事件
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(username, password)) {
                login(username, password)
            }
        }
        
        // 监听登录结果
        userViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            
            if (result != null) {
                Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT).show()
            }
        }
        
        // 设置忘记密码点击事件
        binding.tvForgotPassword.setOnClickListener {
            // TODO: 实现忘记密码功能
            Toast.makeText(requireContext(), "忘记密码功能开发中", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.tilUsername.error = "请输入用户名"
            return false
        } else {
            binding.tilUsername.error = null
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "请输入密码"
            return false
        } else {
            binding.tilPassword.error = null
        }
        
        return true
    }
    
    private fun login(username: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        userViewModel.login(username, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 