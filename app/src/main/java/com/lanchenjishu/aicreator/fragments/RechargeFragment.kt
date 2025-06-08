package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanchenjishu.aicreator.R
import com.lanchenjishu.aicreator.databinding.FragmentRechargeBinding
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

/**
 * 充值Fragment
 */
class RechargeFragment : Fragment() {
    
    private var _binding: FragmentRechargeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var creditsRecordViewModel: CreditsRecordViewModel
    
    // 套餐信息
    private val packageInfoMap = mapOf(
        R.id.rb_package_1 to PackageInfo("基础套餐", 100),
        R.id.rb_package_2 to PackageInfo("标准套餐", 550),
        R.id.rb_package_3 to PackageInfo("高级套餐", 1200),
        R.id.rb_package_4 to PackageInfo("专业套餐", 4000)
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        creditsRecordViewModel = ViewModelProvider(requireActivity())[CreditsRecordViewModel::class.java]
        
        // 设置返回按钮
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        // 设置查看积分记录按钮
        binding.btnViewRecords.setOnClickListener {
            showCreditsRecordFragment()
        }
        
        // 设置立即支付按钮
        binding.btnPay.setOnClickListener {
            onPayClicked()
        }
        
        // 设置默认选中第一个套餐
        binding.rbPackage1.isChecked = true
        
        // 观察用户数据
        observeUserData()
    }
    
    /**
     * 观察用户数据
     */
    private fun observeUserData() {
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvCurrentCredits.text = user.credits.toString()
            }
        }
        
        creditsRecordViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnPay.isEnabled = !loading
        }
        
        creditsRecordViewModel.rechargeResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                if (result.success) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    /**
     * 点击支付按钮
     */
    private fun onPayClicked() {
        val userId = userViewModel.currentUser.value?.id ?: return
        
        // 获取选中的套餐
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        val packageInfo = packageInfoMap[checkedRadioButtonId] ?: return
        
        // 获取选中的支付方式
        val paymentMethod = when (binding.radioGroupPayment.checkedRadioButtonId) {
            R.id.rb_payment_1 -> "微信支付"
            R.id.rb_payment_2 -> "支付宝"
            else -> "微信支付"
        }
        
        // 显示确认对话框
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("确认支付")
            .setMessage("您将使用${paymentMethod}支付${packageInfo.name}，获得${packageInfo.credits}积分，确认继续吗？")
            .setPositiveButton("确认") { _, _ ->
                // 模拟支付
                creditsRecordViewModel.simulatePayment(
                    userId = userId,
                    amount = packageInfo.credits,
                    packageName = packageInfo.name
                )
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    /**
     * 显示支付成功对话框
     */
    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("支付成功")
            .setMessage("您的积分已充值成功！")
            .setPositiveButton("确定", null)
            .show()
    }
    
    /**
     * 显示积分记录Fragment
     */
    private fun showCreditsRecordFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CreditsRecordFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * 套餐信息
     */
    data class PackageInfo(
        val name: String,
        val credits: Int
    )
} 