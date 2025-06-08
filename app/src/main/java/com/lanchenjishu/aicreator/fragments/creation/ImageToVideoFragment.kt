package com.lanchenjishu.aicreator.fragments.creation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentImageToVideoBinding
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.util.Date

/**
 * 图生视频Fragment
 */
class ImageToVideoFragment : Fragment() {
    
    private var _binding: FragmentImageToVideoBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageToVideoBinding.inflate(inflater, container, false)
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
        
        // 设置选择图片按钮点击事件
        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateVideo()
        }
    }
    
    /**
     * 打开图库选择图片
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    
    /**
     * 处理图片选择结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            
            // 显示选中的图片
            binding.ivSelected.setImageURI(selectedImageUri)
            binding.ivSelected.visibility = View.VISIBLE
            
            // 启用生成按钮
            binding.btnGenerate.isEnabled = true
        }
    }
    
    /**
     * 生成视频
     */
    private fun generateVideo() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "请先选择图片", Toast.LENGTH_SHORT).show()
            return
        }
        
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
        val requiredCredits = 20 // 假设每次生成需要20积分
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
            creationType = CreationType.IMAGE_TO_VIDEO,
            title = prompt.take(50), // 取前50个字符作为标题
            prompt = prompt,
            thumbnailUrl = selectedImageUri.toString(), // 使用选中的图片作为缩略图
            createTime = Date(),
            status = CreationStatus.PENDING,
            creditsCost = requiredCredits
        )
        
        // 保存历史记录
        val historyId = historyViewModel.createCreationHistory(creationHistory)
        
        // 模拟API调用生成视频
        binding.root.postDelayed({
            // 更新历史记录状态
            if (historyId > 0) {
                historyViewModel.updateCreationHistoryStatus(
                    historyId,
                    CreationStatus.COMPLETED,
                    "https://example.com/generated_video.mp4" // 模拟生成的视频URL
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
            
            Toast.makeText(requireContext(), "视频生成成功", Toast.LENGTH_SHORT).show()
        }, 5000) // 延迟5秒模拟生成过程
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 