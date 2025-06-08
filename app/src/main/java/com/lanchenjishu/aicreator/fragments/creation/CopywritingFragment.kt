package com.lanchenjishu.aicreator.fragments.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentCopywritingBinding
import com.lanchenjishu.aicreator.dialogs.TemplateDialog
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.fragments.RechargeFragment
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.CreationTemplateViewModel
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.util.Date

/**
 * 文案创作Fragment
 */
class CopywritingFragment : Fragment() {
    
    private var _binding: FragmentCopywritingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    private lateinit var templateViewModel: CreationTemplateViewModel
    private lateinit var creditsViewModel: CreditsRecordViewModel
    
    // 积分消费配置
    companion object {
        const val REQUIRED_CREDITS = 5 // 每次生成需要5积分
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCopywritingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        historyViewModel = ViewModelProvider(requireActivity())[CreationHistoryViewModel::class.java]
        templateViewModel = ViewModelProvider(requireActivity())[CreationTemplateViewModel::class.java]
        creditsViewModel = ViewModelProvider(requireActivity())[CreditsRecordViewModel::class.java]
        
        // 设置UserViewModel到CreditsRecordViewModel
        creditsViewModel.setUserViewModel(userViewModel)
        
        // 设置返回按钮点击事件
        binding.btnBack.setOnClickListener {
            (parentFragment as? CreateFragment)?.backToCreationSelection()
        }
        
        // 设置文案类型下拉选择器
        setupCopywritingTypeSpinner()
        
        // 设置选择模板按钮点击事件
        binding.btnChooseTemplate.setOnClickListener {
            showTemplateDialog()
        }
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateCopywriting()
        }
        
        // 设置复制按钮点击事件
        binding.btnCopy.setOnClickListener {
            copyToClipboard()
        }
    }
    
    /**
     * 显示模板选择对话框
     */
    private fun showTemplateDialog() {
        TemplateDialog.show(
            requireContext(),
            CreationType.COPYWRITING,
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
     * 设置文案类型下拉选择器
     */
    private fun setupCopywritingTypeSpinner() {
        val types = arrayOf(
            "产品宣传", "广告文案", "营销文章", "朋友圈文案", 
            "社交媒体", "活动文案", "节日祝福", "商品描述"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            types
        )
        
        binding.spinnerType.adapter = adapter
    }
    
    /**
     * 生成文案
     */
    private fun generateCopywriting() {
        val type = binding.spinnerType.selectedItem.toString()
        val prompt = binding.etPrompt.text.toString().trim()
        
        if (prompt.isEmpty()) {
            binding.tilPrompt.error = "请输入提示词"
            return
        }
        
        val user = userViewModel.currentUser.value
        if (user == null) {
            Toast.makeText(requireContext(), R.string.login_first, Toast.LENGTH_SHORT).show()
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
            creationType = CreationType.COPYWRITING,
            title = "[$type] " + prompt.take(40), // 取前40个字符作为标题
            prompt = "类型: $type\n内容: $prompt",
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
            description = "文案创作消费 - [$type] ${prompt.take(20)}...",
            creationId = historyId
        )
        
        if (!consumeResult) {
            // 积分消费失败
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            Toast.makeText(requireContext(), "积分消费失败，请稍后再试", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 模拟API调用生成文案
        binding.root.postDelayed({
            // 生成示例文案
            val result = generateSampleText(type, prompt)
            
            // 更新历史记录状态
            if (historyId > 0) {
                historyViewModel.updateCreationHistoryStatus(
                    historyId,
                    CreationStatus.COMPLETED,
                    result // 使用生成的文案作为结果URL
                )
            }
            
            // 显示结果
            binding.tvResult.text = result
            binding.tvResult.visibility = View.VISIBLE
            
            // 显示复制按钮
            binding.btnCopy.visibility = View.VISIBLE
            
            // 隐藏加载状态
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            
            Toast.makeText(requireContext(), "文案生成成功", Toast.LENGTH_SHORT).show()
        }, 2000) // 延迟2秒模拟生成过程
    }
    
    /**
     * 生成示例文案
     */
    private fun generateSampleText(type: String, prompt: String): String {
        // 这里只是简单地生成一些示例文案，实际应用中应该调用AI生成API
        return when (type) {
            "产品宣传" -> "【震撼发布】革命性产品来袭！$prompt，让您的生活焕然一新。独特设计，卓越品质，限时特惠，不容错过！"
            "广告文案" -> "✨ 惊艳登场 ✨\n$prompt\n🔥 全新升级，焕然一新 🔥\n立即购买，限时优惠！"
            "营销文章" -> "《如何通过$prompt提升您的生活质量》\n\n在当今快节奏的社会中，每个人都在寻找能够提升生活品质的方法。$prompt不仅是一种选择，更是一种生活态度...\n\n[阅读全文]"
            "朋友圈文案" -> "今日份的生活感悟：$prompt，让我明白了很多事情并不是看起来那么简单。人生路上，且行且珍惜，愿每一天都充满阳光☀"
            "社交媒体" -> "#今日话题 #$prompt\n\n你是否也有同样的感受？欢迎在评论区分享你的想法！👇"
            "活动文案" -> "🎉【重磅活动】🎉\n$prompt主题活动盛大开启！\n🕒 时间：XX月XX日\n📍 地点：线上线下同步\n🎁 惊喜好礼等你拿\n👉 立即报名，席位有限"
            "节日祝福" -> "值此佳节之际，祝您$prompt，心想事成，万事如意！🎉🎊"
            "商品描述" -> "【产品描述】\n$prompt，采用优质材料精心打造，精湛工艺，细节处理完美。使用感舒适，实用性强，是您日常生活的理想选择。\n\n【规格参数】\n尺寸：XXcm×XXcm\n重量：XX克\n材质：优质环保材料\n\n【温馨提示】\n本店支持7天无理由退换"
            else -> "感谢您的提示：\"$prompt\"。我们已根据您的需求生成了专业的文案内容，希望能够满足您的期望。如需调整，请随时告知我们。"
        }
    }
    
    /**
     * 复制文案到剪贴板
     */
    private fun copyToClipboard() {
        val text = binding.tvResult.text.toString()
        if (text.isNotEmpty()) {
            val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("文案内容", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 显示积分不足对话框
     */
    private fun showCreditsNotEnoughDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("积分不足")
            .setMessage("您的积分不足，无法完成此操作。每次生成文案需要消耗${REQUIRED_CREDITS}积分，您当前的积分为${userViewModel.currentUser.value?.credits ?: 0}。是否前往充值？")
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