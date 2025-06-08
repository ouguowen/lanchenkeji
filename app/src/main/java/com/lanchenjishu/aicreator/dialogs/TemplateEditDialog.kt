package com.lanchenjishu.aicreator.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.DialogTemplateEditBinding
import com.lanchenjishu.aicreator.models.CreationTemplate
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationTemplateViewModel
import java.util.Date

/**
 * 模板编辑对话框
 */
class TemplateEditDialog(
    context: Context,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner,
    private val creationType: CreationType,
    private val templateId: Long? = null
) : Dialog(context) {
    
    private lateinit var binding: DialogTemplateEditBinding
    private lateinit var templateViewModel: CreationTemplateViewModel
    
    // 当前编辑的模板
    private var currentTemplate: CreationTemplate? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogTemplateEditBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // 设置对话框宽度
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        
        // 初始化ViewModel
        templateViewModel = ViewModelProvider(viewModelStoreOwner)[CreationTemplateViewModel::class.java]
        
        // 设置对话框标题
        binding.tvTitle.text = if (templateId == null) {
            context.getString(R.string.create_template)
        } else {
            context.getString(R.string.edit_template)
        }
        
        // 设置关闭按钮
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        
        // 设置保存按钮
        binding.btnSave.setOnClickListener {
            saveTemplate()
        }
        
        // 如果是编辑模式，加载模板数据
        if (templateId != null) {
            loadTemplate(templateId)
        }
        
        // 观察选中的模板
        observeSelectedTemplate()
    }
    
    /**
     * 观察选中的模板
     */
    private fun observeSelectedTemplate() {
        templateViewModel.selectedTemplate.observe(lifecycleOwner) { template ->
            template?.let {
                currentTemplate = it
                
                // 填充表单
                binding.etTemplateName.setText(it.templateName)
                binding.etDescription.setText(it.description ?: "")
                binding.etPromptTemplate.setText(it.promptTemplate)
            }
        }
    }
    
    /**
     * 加载模板
     */
    private fun loadTemplate(id: Long) {
        templateViewModel.loadTemplateById(id)
    }
    
    /**
     * 保存模板
     */
    private fun saveTemplate() {
        val templateName = binding.etTemplateName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val promptTemplate = binding.etPromptTemplate.text.toString().trim()
        
        // 验证表单
        if (templateName.isEmpty()) {
            Toast.makeText(context, R.string.template_name_empty, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (promptTemplate.isEmpty()) {
            Toast.makeText(context, R.string.template_prompt_empty, Toast.LENGTH_SHORT).show()
            return
        }
        
        // 创建模板对象
        val template = CreationTemplate(
            id = currentTemplate?.id ?: 0,
            templateName = templateName,
            templateType = creationType,
            promptTemplate = promptTemplate,
            description = if (description.isEmpty()) null else description,
            thumbnailUrl = currentTemplate?.thumbnailUrl,
            isSystem = currentTemplate?.isSystem ?: false,
            createTime = currentTemplate?.createTime ?: Date(),
            updateTime = Date()
        )
        
        // 保存模板
        if (templateId == null) {
            // 新建模板
            val id = templateViewModel.createTemplate(template)
            if (id > 0) {
                Toast.makeText(context, R.string.add_template_success, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        } else {
            // 更新模板
            val success = templateViewModel.updateTemplate(template)
            if (success) {
                Toast.makeText(context, R.string.update_template_success, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
    
    companion object {
        /**
         * 显示模板编辑对话框
         */
        fun show(
            context: Context,
            viewModelStoreOwner: ViewModelStoreOwner,
            lifecycleOwner: LifecycleOwner,
            creationType: CreationType,
            templateId: Long? = null
        ): TemplateEditDialog {
            val dialog = TemplateEditDialog(
                context,
                viewModelStoreOwner,
                lifecycleOwner,
                creationType,
                templateId
            )
            dialog.show()
            return dialog
        }
    }
} 