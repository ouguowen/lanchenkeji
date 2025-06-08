package com.lanchenjishu.aicreator.fragments.creation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentVoiceCloneBinding
import com.lanchenjishu.aicreator.fragments.CreateFragment
import com.lanchenjishu.aicreator.models.CreationHistory
import com.lanchenjishu.aicreator.models.CreationStatus
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.viewmodels.CreationHistoryViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 声音克隆Fragment
 */
class VoiceCloneFragment : Fragment() {
    
    private var _binding: FragmentVoiceCloneBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var historyViewModel: CreationHistoryViewModel
    
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFilePath: String? = null
    private var selectedAudioUri: Uri? = null
    
    private val PICK_AUDIO_REQUEST = 1
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceCloneBinding.inflate(inflater, container, false)
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
        
        // 设置录音按钮点击事件
        binding.btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (checkPermission()) {
                    startRecording()
                } else {
                    requestPermission()
                }
            }
        }
        
        // 设置选择音频文件按钮点击事件
        binding.btnSelectAudio.setOnClickListener {
            openAudioPicker()
        }
        
        // 设置生成按钮点击事件
        binding.btnGenerate.setOnClickListener {
            generateClonedVoice()
        }
    }
    
    /**
     * 打开音频选择器
     */
    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_AUDIO_REQUEST)
    }
    
    /**
     * 处理音频选择结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedAudioUri = data.data
            
            // 显示选中的音频文件名
            val fileName = getFileNameFromUri(selectedAudioUri)
            binding.tvSelectedAudio.text = fileName
            binding.tvSelectedAudio.visibility = View.VISIBLE
            
            // 启用生成按钮
            binding.btnGenerate.isEnabled = true
        }
    }
    
    /**
     * 从Uri获取文件名
     */
    private fun getFileNameFromUri(uri: Uri?): String {
        uri?.let {
            val cursor = requireContext().contentResolver.query(it, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        return it.getString(displayNameIndex)
                    }
                }
            }
        }
        return "已选择音频文件"
    }
    
    /**
     * 检查录音权限
     */
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 请求录音权限
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }
    
    /**
     * 开始录音
     */
    private fun startRecording() {
        // 创建录音文件
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val audioFile = File.createTempFile("AUDIO_${timeStamp}_", ".3gp", storageDir)
        audioFilePath = audioFile.absolutePath
        
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
            
            // 更新UI
            isRecording = true
            binding.btnRecord.setText(R.string.stop_recording)
            binding.tvRecordingStatus.visibility = View.VISIBLE
            binding.recordingIndicator.visibility = View.VISIBLE
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "录音失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 停止录音
     */
    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            // 将录音文件路径转换为Uri
            val file = File(audioFilePath!!)
            selectedAudioUri = Uri.fromFile(file)
            
            // 更新UI
            isRecording = false
            binding.btnRecord.setText(R.string.start_recording)
            binding.tvRecordingStatus.visibility = View.GONE
            binding.recordingIndicator.visibility = View.GONE
            binding.tvSelectedAudio.text = "已录制音频"
            binding.tvSelectedAudio.visibility = View.VISIBLE
            
            // 启用生成按钮
            binding.btnGenerate.isEnabled = true
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "停止录音失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 生成克隆声音
     */
    private fun generateClonedVoice() {
        if (selectedAudioUri == null) {
            Toast.makeText(requireContext(), "请先录制或选择音频", Toast.LENGTH_SHORT).show()
            return
        }
        
        val text = binding.etText.text.toString().trim()
        if (text.isEmpty()) {
            binding.tilText.error = "请输入要合成的文本"
            return
        }
        
        val user = userViewModel.currentUser.value
        if (user == null) {
            Toast.makeText(requireContext(), R.string.login_first, Toast.LENGTH_SHORT).show()
            return
        }
        
        // 检查积分
        val requiredCredits = 15 // 假设每次生成需要15积分
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
            creationType = CreationType.VOICE_CLONE,
            title = text.take(50), // 取前50个字符作为标题
            prompt = text,
            thumbnailUrl = selectedAudioUri.toString(), // 使用选中的音频作为缩略图
            createTime = Date(),
            status = CreationStatus.PENDING,
            creditsCost = requiredCredits
        )
        
        // 保存历史记录
        val historyId = historyViewModel.createCreationHistory(creationHistory)
        
        // 模拟API调用生成克隆声音
        binding.root.postDelayed({
            // 更新历史记录状态
            if (historyId > 0) {
                historyViewModel.updateCreationHistoryStatus(
                    historyId,
                    CreationStatus.COMPLETED,
                    "https://example.com/cloned_voice.mp3" // 模拟生成的音频URL
                )
            }
            
            // 显示结果
            binding.audioPlayer.visibility = View.VISIBLE
            
            // 显示操作按钮
            binding.btnDownload.visibility = View.VISIBLE
            binding.btnShare.visibility = View.VISIBLE
            
            // 隐藏加载状态
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true
            
            Toast.makeText(requireContext(), "声音克隆成功", Toast.LENGTH_SHORT).show()
        }, 4000) // 延迟4秒模拟生成过程
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(requireContext(), "需要录音权限", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    
    override fun onDestroyView() {
        if (isRecording) {
            stopRecording()
        }
        super.onDestroyView()
        _binding = null
    }
} 