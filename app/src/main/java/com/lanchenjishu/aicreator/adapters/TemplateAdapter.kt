package com.lanchenjishu.aicreator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lanchenjishu.aicreator.databinding.ItemTemplateBinding
import com.lanchenjishu.aicreator.models.CreationTemplate

/**
 * 创作模板适配器
 */
class TemplateAdapter : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {
    
    private val templateList = mutableListOf<CreationTemplate>()
    private var isEditable = false
    
    // 点击事件回调
    var onTemplateClickListener: ((CreationTemplate) -> Unit)? = null
    var onUseClickListener: ((CreationTemplate) -> Unit)? = null
    var onEditClickListener: ((CreationTemplate) -> Unit)? = null
    var onDeleteClickListener: ((CreationTemplate) -> Unit)? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templateList[position]
        holder.bind(template)
    }
    
    override fun getItemCount(): Int = templateList.size
    
    /**
     * 设置模板列表
     */
    fun setTemplates(templates: List<CreationTemplate>) {
        templateList.clear()
        templateList.addAll(templates)
        notifyDataSetChanged()
    }
    
    /**
     * 设置是否可编辑
     */
    fun setEditable(editable: Boolean) {
        isEditable = editable
        notifyDataSetChanged()
    }
    
    inner class ViewHolder(private val binding: ItemTemplateBinding) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            // 设置整个条目的点击事件
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTemplateClickListener?.invoke(templateList[position])
                }
            }
            
            // 设置使用按钮点击事件
            binding.btnUse.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUseClickListener?.invoke(templateList[position])
                }
            }
            
            // 设置编辑按钮点击事件
            binding.btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClickListener?.invoke(templateList[position])
                }
            }
            
            // 设置删除按钮点击事件
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener?.invoke(templateList[position])
                    
                    // 从列表中移除
                    templateList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
        
        /**
         * 绑定数据
         */
        fun bind(template: CreationTemplate) {
            binding.tvTemplateName.text = template.templateName
            binding.tvTemplateDescription.text = template.description ?: ""
            binding.tvPromptPreview.text = template.promptTemplate
            
            // 根据是否可编辑显示编辑和删除按钮
            if (isEditable && !template.isSystem) {
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }
} 