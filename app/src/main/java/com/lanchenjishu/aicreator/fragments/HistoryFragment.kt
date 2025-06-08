package com.lanchenjishu.aicreator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.adapters.CreationHistoryAdapter
import com.lanchenjishu.aicreator.databinding.FragmentHistoryBinding
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 历史记录Fragment
 */
class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    private lateinit var adapter: CreationHistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        historyViewModel = ViewModelProvider(requireActivity())[CreationHistoryViewModel::class.java]
        
        // 设置RecyclerView
        setupRecyclerView()
        
        // 设置下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadHistoryData()
        }
        
        // 设置类型筛选
        setupTypeFilter()
        
        // 监听登录状态
        observeLoginStatus()
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = CreationHistoryAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // 设置点击事件
        adapter.onItemClickListener = { history ->
            showHistoryDetail(history)
        }
        
        adapter.onViewClickListener = { history ->
            viewHistoryResult(history)
        }
        
        adapter.onShareClickListener = { history ->
            shareHistoryResult(history)
        }
        
        adapter.onDeleteClickListener = { history ->
            deleteHistory(history)
        }
    }
    
    /**
     * 设置类型筛选
     */
    private fun setupTypeFilter() {
        binding.chipAll.setOnClickListener {
            binding.chipAll.isChecked = true
            loadHistoryData(null)
        }
        
        binding.chipTextToImage.setOnClickListener {
            binding.chipAll.isChecked = false
            loadHistoryData(CreationType.TEXT_TO_IMAGE)
        }
        
        binding.chipImageToVideo.setOnClickListener {
            binding.chipAll.isChecked = false
            loadHistoryData(CreationType.IMAGE_TO_VIDEO)
        }
        
        binding.chipVoiceClone.setOnClickListener {
            binding.chipAll.isChecked = false
            loadHistoryData(CreationType.VOICE_CLONE)
        }
        
        binding.chipCopywriting.setOnClickListener {
            binding.chipAll.isChecked = false
            loadHistoryData(CreationType.COPYWRITING)
        }
        
        binding.chipDigitalHuman.setOnClickListener {
            binding.chipAll.isChecked = false
            loadHistoryData(CreationType.DIGITAL_HUMAN)
        }
    }
    
    /**
     * 监听登录状态
     */
    private fun observeLoginStatus() {
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // 用户已登录，加载历史记录
                binding.layoutNotLogin.visibility = View.GONE
                binding.layoutContent.visibility = View.VISIBLE
                loadHistoryData()
            } else {
                // 用户未登录，显示登录提示
                binding.layoutNotLogin.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE
            }
        }
    }
    
    /**
     * 加载历史记录数据
     */
    private fun loadHistoryData(type: CreationType? = null) {
        val userId = userViewModel.currentUser.value?.id ?: return
        
        // 开始加载
        binding.swipeRefreshLayout.isRefreshing = true
        
        // 获取历史记录数据
        val historyList = if (type == null) {
            // 获取所有类型
            historyViewModel.getCreationHistoryByUserId(userId)
        } else {
            // 获取指定类型
            historyViewModel.getCreationHistoryByUserIdAndType(userId, type)
        }
        
        // 更新UI
        if (historyList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        } else {
            adapter.setHistoryList(historyList)
            binding.recyclerView.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
        }
        
        // 结束加载
        binding.swipeRefreshLayout.isRefreshing = false
    }
    
    /**
     * 显示历史记录详情
     */
    private fun showHistoryDetail(history: CreationHistory) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        val message = """
            类型：${history.creationType.displayName}
            创建时间：${dateFormat.format(history.createTime)}
            状态：${history.status.displayName}
            消耗积分：${history.creditsCost}
            
            ${history.prompt}
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(history.title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
    
    /**
     * 查看历史记录结果
     */
    private fun viewHistoryResult(history: CreationHistory) {
        // 只有已完成的记录才能查看
        if (history.status != CreationStatus.COMPLETED) {
            Toast.makeText(requireContext(), "该记录尚未完成", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 根据不同类型处理查看逻辑
        when (history.creationType) {
            CreationType.TEXT_TO_IMAGE, CreationType.IMAGE_TO_VIDEO, CreationType.VOICE_CLONE, CreationType.DIGITAL_HUMAN -> {
                // 打开URL
                if (!history.resultUrl.isNullOrEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(history.resultUrl))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "无法打开链接: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "结果链接不可用", Toast.LENGTH_SHORT).show()
                }
            }
            CreationType.COPYWRITING -> {
                // 直接显示文案内容
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(history.title)
                    .setMessage(history.resultUrl ?: "内容不可用")
                    .setPositiveButton("关闭", null)
                    .setNeutralButton("复制") { _, _ ->
                        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("文案内容", history.resultUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(requireContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }
    
    /**
     * 分享历史记录结果
     */
    private fun shareHistoryResult(history: CreationHistory) {
        // 只有已完成的记录才能分享
        if (history.status != CreationStatus.COMPLETED) {
            Toast.makeText(requireContext(), "该记录尚未完成", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 根据不同类型处理分享逻辑
        when (history.creationType) {
            CreationType.TEXT_TO_IMAGE, CreationType.IMAGE_TO_VIDEO, CreationType.VOICE_CLONE, CreationType.DIGITAL_HUMAN -> {
                // 分享链接
                if (!history.resultUrl.isNullOrEmpty()) {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享 - ${history.title}")
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "我用AI创作助手创建了\"${history.title}\": ${history.resultUrl}")
                    startActivity(Intent.createChooser(shareIntent, "分享到"))
                } else {
                    Toast.makeText(requireContext(), "结果链接不可用", Toast.LENGTH_SHORT).show()
                }
            }
            CreationType.COPYWRITING -> {
                // 分享文案内容
                if (!history.resultUrl.isNullOrEmpty()) {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享 - ${history.title}")
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "我用AI创作助手创建了文案:\n\n${history.resultUrl}")
                    startActivity(Intent.createChooser(shareIntent, "分享到"))
                } else {
                    Toast.makeText(requireContext(), "内容不可用", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    /**
     * 删除历史记录
     */
    private fun deleteHistory(history: CreationHistory) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除确认")
            .setMessage("确定要删除这条历史记录吗？")
            .setPositiveButton("确定") { _, _ ->
                val success = historyViewModel.deleteCreationHistory(history.id)
                if (success) {
                    Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
                    // 适配器中已经处理了删除项的UI更新
                } else {
                    Toast.makeText(requireContext(), "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 