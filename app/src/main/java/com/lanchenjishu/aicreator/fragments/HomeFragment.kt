package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.databinding.FragmentHomeBinding
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        
        // 初始化UI
        initUI()
        
        // 监听用户数据变化
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvWelcome.text = "欢迎回来，${it.nickname}"
            }
        }
    }
    
    private fun initUI() {
        // TODO: 初始化首页UI
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 