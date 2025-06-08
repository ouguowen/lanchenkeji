package com.lanchenjishu.aicreator.fragments.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentTextToImageBinding
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.fragments.RechargeFragment
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.util.Date

/**
 * 文生图Fragment
 */
class TextToImageFragment : Fragment() {
    
    private var _binding: FragmentTextToImageBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    private lateinit var creditsViewModel: CreditsRecordViewModel
    
    // 积分消费配置
    companion object {
        const val REQUIRED_CREDITS = 10 // 每次生成需要10积分
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextToImageBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        historyViewModel = ViewModelProvider(requireActivity())[CreationHistoryViewModel::class.java]
        creditsViewModel = ViewModelProvider(requireActivity())[CreditsRecordViewModel::class.java]
        
        // 设置UserViewModel到CreditsRecordViewModel
        creditsViewModel.setUserViewModel(userViewModel)
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateImage()
        }
        
        // 设置返回按钮点击事件
        binding.btnBack.setOnClickListener {
            (parentFragment as? CreateFragment)?.backToCreationSelection()
        }
        
        // 设置模板按钮点击事件
        binding.btnTemplate.setOnClickListener {
            // TODO: 显示模板选择
            Toast.makeText(requireContext(), "模板功能开发中", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 生成图片
     */
    private fun generateImage() {
        val prompt = binding.etPrompt.text.toString().trim()
        
        if (prompt.isEmpty()) {
            binding.tilPrompt.error = "请输入提示词"
            return
        }
        
        val user = userViewModel.currentUser.value
        if (user == null) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 检查积分
        if (user.credits < REQUIRED_CREDITS) {
            showCreditsNotEnoughDialog()
            return
        }
        
        // 显示加载状态
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGenerate.isEnabled = false
        
        // 创建历史记录
        val creationHistory = CreationHistory(
            userId = user.id,
            creationType = CreationType.TEXT_TO_IMAGE,
            title = prompt.take(50), // 取前50个字符作为标题
            prompt = prompt,
            createTime = Date(),
            status = CreationStatus.PENDING,
            creditsCost = REQUIRED_CREDITS
        )
        
        // 保存历史记录
        val historyId = historyViewModel.createCreationHistory(creationHistory)
        
        // 消费积分
        val consumeResult = creditsViewModel.consumeCredits(
            userId = user.id,
            amount = REQUIRED_CREDITS,
            description = "文生图消费 - ${prompt.take(20)}...",
            creationId = historyId
        )
        
        if (!consumeResult) {
            // 积分消费失败
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            Toast.makeText(requireContext(), "积分消费失败，请稍后再试", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 模拟API调用生成图片
        binding.root.postDelayed({
            // 更新历史记录状态
            if (historyId > 0) {
                historyViewModel.updateCreationHistoryStatus(
                    historyId,
                    CreationStatus.COMPLETED,
                    "https://example.com/generated_image.jpg" // 模拟生成的图片URL
                )
            }
            
            // 显示结果
            binding.ivResult.visibility = View.VISIBLE
            binding.ivResult.setImageResource(R.drawable.sample_generated_image)
            
            // 显示操作按钮
            binding.btnDownload.visibility = View.VISIBLE
            binding.btnShare.visibility = View.VISIBLE
            
            // 隐藏加载状态
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            
            Toast.makeText(requireContext(), "图片生成成功", Toast.LENGTH_SHORT).show()
        }, 3000) // 延迟3秒模拟生成过程
    }
    
    /**
     * 显示积分不足对话框
     */
    private fun showCreditsNotEnoughDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("积分不足")
            .setMessage("您的积分不足，无法完成此操作。每次生成图片需要消耗${REQUIRED_CREDITS}积分，您当前的积分为${userViewModel.currentUser.value?.credits ?: 0}。是否前往充值？")
            .setPositiveButton("去充值") { _, _ ->
                // 跳转到充值页面
                navigateToRecharge()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    /**
     * 跳转到充值页面
     */
    private fun navigateToRecharge() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RechargeFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 