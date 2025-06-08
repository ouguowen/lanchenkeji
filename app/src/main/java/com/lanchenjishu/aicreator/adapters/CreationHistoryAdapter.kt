package com.lanchenjishu.aicreator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.ItemCreationHistoryBinding
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 创作历史记录适配器
 */
class CreationHistoryAdapter : RecyclerView.Adapter<CreationHistoryAdapter.ViewHolder>() {
    
    private val historyList = mutableListOf<CreationHistory>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    // 点击事件回调
    var onItemClickListener: ((CreationHistory) -> Unit)? = null
    var onViewClickListener: ((CreationHistory) -> Unit)? = null
    var onShareClickListener: ((CreationHistory) -> Unit)? = null
    var onDeleteClickListener: ((CreationHistory) -> Unit)? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCreationHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history)
    }
    
    override fun getItemCount(): Int = historyList.size
    
    /**
     * 设置历史记录列表
     */
    fun setHistoryList(list: List<CreationHistory>) {
        historyList.clear()
        historyList.addAll(list)
        notifyDataSetChanged()
    }
    
    /**
     * 添加历史记录
     */
    fun addHistory(history: CreationHistory) {
        historyList.add(0, history) // 添加到列表开头
        notifyItemInserted(0)
    }
    
    /**
     * 删除历史记录
     */
    fun removeHistory(position: Int) {
        if (position in 0 until historyList.size) {
            historyList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    
    inner class ViewHolder(private val binding: ItemCreationHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            // 设置整个条目的点击事件
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(historyList[position])
                }
            }
            
            // 设置查看按钮点击事件
            binding.btnView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onViewClickListener?.invoke(historyList[position])
                }
            }
            
            // 设置分享按钮点击事件
            binding.btnShare.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onShareClickListener?.invoke(historyList[position])
                }
            }
            
            // 设置删除按钮点击事件
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener?.invoke(historyList[position])
                    removeHistory(position)
                }
            }
        }
        
        /**
         * 绑定数据
         */
        fun bind(history: CreationHistory) {
            // 设置创作类型
            binding.tvType.text = history.creationType.displayName
            
            // 根据不同类型设置不同颜色
            when (history.creationType) {
                com.lanchenjishu.aicreator.models.CreationType.TEXT_TO_IMAGE ->
                    binding.tvType.setBackgroundResource(R.drawable.bg_tag_text_to_image)
                com.lanchenjishu.aicreator.models.CreationType.IMAGE_TO_VIDEO ->
                    binding.tvType.setBackgroundResource(R.drawable.bg_tag_image_to_video)
                com.lanchenjishu.aicreator.models.CreationType.VOICE_CLONE ->
                    binding.tvType.setBackgroundResource(R.drawable.bg_tag_voice_clone)
                com.lanchenjishu.aicreator.models.CreationType.COPYWRITING ->
                    binding.tvType.setBackgroundResource(R.drawable.bg_tag_copywriting)
                com.lanchenjishu.aicreator.models.CreationType.DIGITAL_HUMAN ->
                    binding.tvType.setBackgroundResource(R.drawable.bg_tag_digital_human)
            }
            
            // 设置标题
            binding.tvTitle.text = history.title
            
            // 设置日期
            binding.tvDate.text = dateFormat.format(history.createTime)
            
            // 设置状态
            binding.tvStatus.text = history.status.displayName
            
            // 根据状态设置不同颜色
            when (history.status) {
                CreationStatus.PENDING -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.warning))
                    binding.btnView.isEnabled = false
                }
                CreationStatus.COMPLETED -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.success))
                    binding.btnView.isEnabled = true
                }
                CreationStatus.FAILED -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.error))
                    binding.btnView.isEnabled = false
                }
            }
            
            // 加载缩略图
            if (!history.thumbnailUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(history.thumbnailUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.ivThumbnail)
            } else {
                // 根据创作类型设置默认图标
                val defaultImageRes = when (history.creationType) {
                    com.lanchenjishu.aicreator.models.CreationType.TEXT_TO_IMAGE -> R.drawable.ic_text_to_image
                    com.lanchenjishu.aicreator.models.CreationType.IMAGE_TO_VIDEO -> R.drawable.ic_image_to_video
                    com.lanchenjishu.aicreator.models.CreationType.VOICE_CLONE -> R.drawable.ic_voice_clone
                    com.lanchenjishu.aicreator.models.CreationType.COPYWRITING -> R.drawable.ic_copywriting
                    com.lanchenjishu.aicreator.models.CreationType.DIGITAL_HUMAN -> R.drawable.ic_digital_human
                }
                binding.ivThumbnail.setImageResource(defaultImageRes)
            }
        }
    }
} 