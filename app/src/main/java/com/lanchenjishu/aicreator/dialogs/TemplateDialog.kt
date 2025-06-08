package com.lanchenjishu.aicreator.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.adapters.TemplateAdapter
import com.lanchenjishu.aicreator.databinding.DialogTemplateBinding
import com.lanchenjishu.aicreator.models.CreationTemplate
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationTemplateViewModel

/**
 * 模板选择对话框
 */
class TemplateDialog(
    context: Context,
    private val creationType: CreationType,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) : Dialog(context) {
    
    private lateinit var binding: DialogTemplateBinding
    private lateinit var templateViewModel: CreationTemplateViewModel
    private lateinit var adapter: TemplateAdapter
    
    private var onTemplateSelectedListener: ((CreationTemplate) -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogTemplateBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // 设置对话框宽度
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        
        // 初始化ViewModel
        templateViewModel = ViewModelProvider(viewModelStoreOwner)[CreationTemplateViewModel::class.java]
        
        // 设置对话框标题
        binding.tvTitle.text = getDialogTitle()
        
        // 设置RecyclerView
        setupRecyclerView()
        
        // 观察模板数据
        observeTemplates()
        
        // 设置关闭按钮
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        
        // 设置管理模板按钮
        binding.btnManageTemplates.setOnClickListener {
            showTemplateManagementDialog()
        }
        
        // 加载模板
        loadTemplates()
    }
    
    /**
     * 设置模板选择回调
     */
    fun setOnTemplateSelectedListener(listener: (CreationTemplate) -> Unit) {
        onTemplateSelectedListener = listener
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = TemplateAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        
        // 设置使用按钮点击事件
        adapter.onUseClickListener = { template ->
            onTemplateSelectedListener?.invoke(template)
            dismiss()
        }
    }
    
    /**
     * 观察模板数据
     */
    private fun observeTemplates() {
        templateViewModel.templates.observe(lifecycleOwner) { templates ->
            if (templates.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmptyHint.visibility = View.GONE
                adapter.setTemplates(templates)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmptyHint.visibility = View.VISIBLE
            }
        }
    }
    
    /**
     * 加载模板
     */
    private fun loadTemplates() {
        templateViewModel.loadTemplatesByType(creationType)
    }
    
    /**
     * 获取对话框标题
     */
    private fun getDialogTitle(): String {
        return "选择${creationType.displayName}模板"
    }
    
    /**
     * 显示模板管理对话框
     */
    private fun showTemplateManagementDialog() {
        TemplateManagementDialog.show(
            context,
            viewModelStoreOwner,
            lifecycleOwner
        )
    }
    
    companion object {
        /**
         * 创建模板选择对话框
         */
        fun show(
            context: Context,
            creationType: CreationType,
            viewModelStoreOwner: ViewModelStoreOwner,
            lifecycleOwner: LifecycleOwner,
            onTemplateSelected: (CreationTemplate) -> Unit
        ): TemplateDialog {
            val dialog = TemplateDialog(context, creationType, viewModelStoreOwner, lifecycleOwner)
            dialog.setOnTemplateSelectedListener(onTemplateSelected)
            dialog.show()
            return dialog
        }
    }
} 