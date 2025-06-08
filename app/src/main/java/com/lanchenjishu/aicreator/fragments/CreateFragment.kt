package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lanchenjishu.aicreator.databinding.FragmentCreateBinding
import com.lanchenjishu.aicreator.fragments.creation.CopywritingFragment
import com.lanchenjishu.aicreator.fragments.creation.DigitalHumanFragment
import com.lanchenjishu.aicreator.fragments.creation.ImageToVideoFragment
import com.lanchenjishu.aicreator.fragments.creation.TextToImageFragment
import com.lanchenjishu.aicreator.fragments.creation.VoiceCloneFragment

class CreateFragment : Fragment() {
    
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 设置各种创作功能的点击事件
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 文生图
        binding.cardTextToImage.setOnClickListener {
            navigateToCreationFeature(TextToImageFragment())
        }
        
        // 图生视频
        binding.cardImageToVideo.setOnClickListener {
            navigateToCreationFeature(ImageToVideoFragment())
        }
        
        // 声音克隆
        binding.cardVoiceClone.setOnClickListener {
            navigateToCreationFeature(VoiceCloneFragment())
        }
        
        // 文案创作
        binding.cardCopywriting.setOnClickListener {
            navigateToCreationFeature(CopywritingFragment())
        }
        
        // 数字人
        binding.cardDigitalHuman.setOnClickListener {
            navigateToCreationFeature(DigitalHumanFragment())
        }
    }
    
    /**
     * 导航到创作功能页面
     */
    private fun navigateToCreationFeature(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentCreationContainer.id, fragment)
            .addToBackStack(null)
            .commit()
            
        // 显示创作容器，隐藏创作选择
        binding.scrollViewCreationSelection.visibility = View.GONE
        binding.fragmentCreationContainer.visibility = View.VISIBLE
    }
    
    /**
     * 返回创作选择
     */
    fun backToCreationSelection() {
        // 显示创作选择，隐藏创作容器
        binding.scrollViewCreationSelection.visibility = View.VISIBLE
        binding.fragmentCreationContainer.visibility = View.GONE
        
        // 清空返回栈
        requireActivity().supportFragmentManager.popBackStack()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}