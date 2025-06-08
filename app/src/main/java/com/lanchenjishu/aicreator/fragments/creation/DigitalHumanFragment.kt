package com.lanchenjishu.aicreator.fragments.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentDigitalHumanBinding
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.util.Date

/**
 * 数字人生成Fragment
 */
class DigitalHumanFragment : Fragment() {
    
    private var _binding: FragmentDigitalHumanBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDigitalHumanBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        historyViewModel = ViewModelProvider(requireActivity())[CreationHistoryViewModel::class.java]
        
        // 设置返回按钮点击事件
        binding.btnBack.setOnClickListener {
            (parentFragment as? CreateFragment)?.backToCreationSelection()
        }
        
        // 设置数字人模板选择器
        setupDigitalHumanTemplateSpinner()
        
        // 设置场景选择器
        setupSceneSpinner()
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateDigitalHuman()
        }
    }
    
    /**
     * 设置数字人模板选择器
     */
    private fun setupDigitalHumanTemplateSpinner() {
        val templates = arrayOf(
            "商务男性", "商务女性", "青年男性", "青年女性", 
            "中年男性", "中年女性", "老年男性", "老年女性"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            templates
        )
        
        binding.spinnerTemplate.adapter = adapter
    }
    
    /**
     * 设置场景选择器
     */
    private fun setupSceneSpinner() {
        val scenes = arrayOf(
            "办公室", "户外", "会议室", "演讲台", 
            "教室", "家庭", "虚拟背景", "自定义背景"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            scenes
        )
        
        binding.spinnerScene.adapter = adapter
    }
    
    /**
     * 生成数字人
     */
    private fun generateDigitalHuman() {
        val template = binding.spinnerTemplate.selectedItem.toString()
        val scene = binding.spinnerScene.selectedItem.toString()
        val script = binding.etScript.text.toString().trim()
        
        if (script.isEmpty()) {
            binding.tilScript.error = "请输入脚本内容"
            return
        }
        
        val user = userViewModel.currentUser.value
        if (user == null) {
            Toast.makeText(requireContext(), R.string.login_first, Toast.LENGTH_SHORT).show()
            return
        }
        
        // 检查积分
        val requiredCredits = 30 // 假设每次生成需要30积分
        if (user.credits < requiredCredits) {
            Toast.makeText(requireContext(), R.string.credits_not_enough, Toast.LENGTH_SHORT).show()
            return
        }
        
        // 显示加载状态
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGenerate.isEnabled = false
        
        // 创建历史记录
        val creationHistory = CreationHistory(
            userId = user.id,
            creationType = CreationType.DIGITAL_HUMAN,
            title = script.take(30), // 取前30个字符作为标题
            prompt = "模板: $template\n场景: $scene\n脚本: $script",
            createTime = Date(),
            status = CreationStatus.PENDING,
            creditsCost = requiredCredits
        )
        
        // 保存历史记录
        val historyId = historyViewModel.createCreationHistory(creationHistory)
        
        // 模拟API调用生成数字人视频
        binding.root.postDelayed({
            // 更新历史记录状态
            if (historyId > 0) {
                historyViewModel.updateCreationHistoryStatus(
                    historyId,
                    CreationStatus.COMPLETED,
                    "https://example.com/digital_human_video.mp4" // 模拟生成的视频URL
                )
            }
            
            // 显示结果
            binding.videoView.visibility = View.VISIBLE
            binding.videoView.setVideoPath("android.resource://" + requireContext().packageName + "/" + R.raw.sample_video)
            binding.videoView.start()
            
            // 显示操作按钮
            binding.btnDownload.visibility = View.VISIBLE
            binding.btnShare.visibility = View.VISIBLE
            
            // 隐藏加载状态
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            
            Toast.makeText(requireContext(), "数字人生成成功", Toast.LENGTH_SHORT).show()
        }, 6000) // 延迟6秒模拟生成过程
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 