package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.databinding.FragmentTextToImageBinding
import com.lanchenjishu.aicreator.dialogs.TemplateDialog
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.CreationTemplateViewModel
import java.util.Date

/**
 * 文生图Fragment
 */
class TextToImageFragment : Fragment() {
    
    private var _binding: FragmentTextToImageBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var historyViewModel: CreationHistoryViewModel
    private lateinit var templateViewModel: CreationTemplateViewModel
    
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
        historyViewModel = ViewModelProvider(requireActivity())[CreationHistoryViewModel::class.java]
        templateViewModel = ViewModelProvider(requireActivity())[CreationTemplateViewModel::class.java]
        
        // 设置选择模板按钮点击事件
        binding.btnChooseTemplate.setOnClickListener {
            showTemplateDialog()
        }
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateImage()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * 显示模板选择对话框
     */
    private fun showTemplateDialog() {
        TemplateDialog.show(
            requireContext(),
            CreationType.TEXT_TO_IMAGE,
            requireActivity(),
            viewLifecycleOwner
        ) { template ->
            // 将模板内容填充到输入框
            binding.etPrompt.setText(template.promptTemplate)
            
            // 设置选中的模板
            templateViewModel.selectTemplate(template)
            
            Toast.makeText(
                requireContext(),
                "已选择模板：${template.templateName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * 生成图片
     */
    private fun generateImage() {
        val prompt = binding.etPrompt.text.toString().trim()
        
        if (prompt.isEmpty()) {
            Toast.makeText(requireContext(), "请输入提示词", Toast.LENGTH_SHORT).show()
            return
        }
        
        // TODO: 调用AI接口生成图片
        // 这里模拟生成图片，实际应该调用AI接口
        
        // 保存创作历史
        val history = CreationHistory(
            userId = 1, // 假设当前用户ID为1
            creationType = CreationType.TEXT_TO_IMAGE,
            prompt = prompt,
            result = "https://example.com/generated_image.jpg", // 模拟生成的图片URL
            creationTime = Date(),
            creditsUsed = 10 // 假设消耗10积分
        )
        
        historyViewModel.insertCreationHistory(history)
        
        Toast.makeText(requireContext(), "图片生成请求已提交", Toast.LENGTH_SHORT).show()
        
        // 清空输入框
        binding.etPrompt.setText("")
    }
} 