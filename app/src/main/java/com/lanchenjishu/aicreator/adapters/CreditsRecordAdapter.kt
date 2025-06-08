package com.lanchenjishu.aicreator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lanchenjishu.aicreator.databinding.ItemCreditsRecordBinding
import com.lanchenjishu.aicreator.models.CreditsRecord
import com.lanchenjishu.aicreator.models.CreditsRecordType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 积分记录适配器
 */
class CreditsRecordAdapter : RecyclerView.Adapter<CreditsRecordAdapter.ViewHolder>() {
    
    private val records = mutableListOf<CreditsRecord>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCreditsRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }
    
    override fun getItemCount(): Int = records.size
    
    /**
     * 设置积分记录
     */
    fun setRecords(newRecords: List<CreditsRecord>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }
    
    /**
     * 添加更多积分记录
     */
    fun addRecords(moreRecords: List<CreditsRecord>) {
        val startPosition = records.size
        records.addAll(moreRecords)
        notifyItemRangeInserted(startPosition, moreRecords.size)
    }
    
    /**
     * 清空积分记录
     */
    fun clearRecords() {
        records.clear()
        notifyDataSetChanged()
    }
    
    inner class ViewHolder(private val binding: ItemCreditsRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(record: CreditsRecord) {
            // 设置积分记录类型
            binding.tvRecordType.text = record.type.displayName
            
            // 设置类型对应的颜色
            val typeColor = when (record.type) {
                CreditsRecordType.RECHARGE -> android.graphics.Color.parseColor("#4CAF50") // 绿色
                CreditsRecordType.CONSUMPTION -> android.graphics.Color.parseColor("#F44336") // 红色
                CreditsRecordType.REWARD -> android.graphics.Color.parseColor("#2196F3") // 蓝色
                CreditsRecordType.REFUND -> android.graphics.Color.parseColor("#FF9800") // 橙色
            }
            binding.tvRecordType.setTextColor(typeColor)
            
            // 设置记录时间
            binding.tvRecordTime.text = dateFormat.format(record.createTime)
            
            // 设置描述
            binding.tvDescription.text = record.description
            
            // 设置金额
            val amountText = if (record.amount > 0) "+${record.amount}" else "${record.amount}"
            binding.tvAmount.text = amountText
            
            // 设置金额颜色
            val amountColor = if (record.amount >= 0) {
                android.graphics.Color.parseColor("#4CAF50") // 绿色
            } else {
                android.graphics.Color.parseColor("#F44336") // 红色
            }
            binding.tvAmount.setTextColor(amountColor)
            
            // 设置余额
            binding.tvBalance.text = record.balance.toString()
        }
    }
} 