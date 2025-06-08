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
import com.google.android.material.tabs.TabLayout
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.adapters.TemplateAdapter
import com.lanchenjishu.aicreator.databinding.DialogTemplateManagementBinding
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationTemplateViewModel

/**
 * 模板管理对话框
 */
class TemplateManagementDialog(
    context: Context,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) : Dialog(context) {
    
    private lateinit var binding: DialogTemplateManagementBinding
    private lateinit var templateViewModel: CreationTemplateViewModel
    private lateinit var adapter: TemplateAdapter
    
    // 当前选中的创作类型
    private var currentType: CreationType = CreationType.TEXT_TO_IMAGE
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogTemplateManagementBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // 设置对话框宽度
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        
        // 初始化ViewModel
        templateViewModel = ViewModelProvider(viewModelStoreOwner)[CreationTemplateViewModel::class.java]
        
        // 设置关闭按钮
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        
        // 设置RecyclerView
        setupRecyclerView()
        
        // 设置Tab
        setupTabLayout()
        
        // 设置添加模板按钮
        binding.btnAddTemplate.setOnClickListener {
            showTemplateEditDialog()
        }
        
        // 观察模板数据
        observeTemplates()
        
        // 加载模板
        loadTemplates()
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = TemplateAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        
        // 设置可编辑状态
        adapter.setEditable(true)
        
        // 设置编辑按钮点击事件
        adapter.onEditClickListener = { template ->
            showTemplateEditDialog(template.id)
        }
        
        // 设置删除按钮点击事件
        adapter.onDeleteClickListener = { template ->
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_confirm)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    templateViewModel.deleteTemplate(template.id)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
    
    /**
     * 设置TabLayout
     */
    private fun setupTabLayout() {
        // 添加创作类型Tab
        CreationType.values().forEach { type ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(type.displayName))
        }
        
        // 设置Tab选择监听
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentType = CreationType.values()[it.position]
                    loadTemplates()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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
        templateViewModel.loadTemplatesByType(currentType)
    }
    
    /**
     * 显示模板编辑对话框
     * @param templateId 模板ID，如果为null则是新建模板
     */
    private fun showTemplateEditDialog(templateId: Long? = null) {
        TemplateEditDialog.show(
            context,
            viewModelStoreOwner,
            lifecycleOwner,
            currentType,
            templateId
        )
    }
    
    companion object {
        /**
         * 显示模板管理对话框
         */
        fun show(
            context: Context,
            viewModelStoreOwner: ViewModelStoreOwner,
            lifecycleOwner: LifecycleOwner
        ): TemplateManagementDialog {
            val dialog = TemplateManagementDialog(
                context,
                viewModelStoreOwner,
                lifecycleOwner
            )
            dialog.show()
            return dialog
        }
    }
} 