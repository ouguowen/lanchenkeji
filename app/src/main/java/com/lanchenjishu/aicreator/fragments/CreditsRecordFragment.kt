package com.lanchenjishu.aicreator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lanchenjishu.aicreator.adapters.CreditsRecordAdapter
import com.lanchenjishu.aicreator.databinding.FragmentCreditsRecordBinding
import com.lanchenjishu.aicreator.models.CreditsRecordType
import com.lanchenjishu.aicreator.viewmodels.CreditsRecordViewModel
import com.lanchenjishu.aicreator.viewmodels.UserViewModel

/**
 * 积分记录Fragment
 */
class CreditsRecordFragment : Fragment() {
    
    private var _binding: FragmentCreditsRecordBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var creditsRecordViewModel: CreditsRecordViewModel
    private lateinit var adapter: CreditsRecordAdapter
    
    // 当前选中的记录类型，null表示全部
    private var currentType: CreditsRecordType? = null
    
    // 分页参数
    private var currentPage = 0
    private val pageSize = 20
    private var hasMoreData = true
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditsRecordBinding.inflate(inflater, container, false)
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
        
        // 设置TabLayout
        setupTabLayout()
        
        // 设置RecyclerView
        setupRecyclerView()
        
        // 设置下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
        
        // 观察积分记录数据
        observeData()
        
        // 加载初始数据
        loadData()
    }
    
    /**
     * 设置TabLayout
     */
    private fun setupTabLayout() {
        // 添加"全部"选项卡
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("全部"))
        
        // 添加各类型选项卡
        CreditsRecordType.values().forEach { type ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(type.displayName))
        }
        
        // 设置选项卡选择监听
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    currentType = if (position == 0) {
                        null // 全部
                    } else {
                        CreditsRecordType.values()[position - 1]
                    }
                    
                    // 重置分页参数
                    currentPage = 0
                    hasMoreData = true
                    
                    // 刷新数据
                    loadData()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = CreditsRecordAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // 添加滚动监听，实现加载更多
        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0
                        && hasMoreData
                        && !binding.swipeRefresh.isRefreshing) {
                        loadMoreData()
                    }
                }
            }
        })
    }
    
    /**
     * 观察数据变化
     */
    private fun observeData() {
        // 观察积分记录
        creditsRecordViewModel.creditsRecords.observe(viewLifecycleOwner) { records ->
            binding.swipeRefresh.isRefreshing = false
            
            if (currentPage == 0) {
                adapter.setRecords(records)
            } else {
                adapter.addRecords(records)
            }
            
            // 判断是否还有更多数据
            hasMoreData = records.size == pageSize
            
            // 显示空视图
            binding.tvEmpty.visibility = if (currentPage == 0 && records.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (records.isEmpty() && currentPage == 0) View.GONE else View.VISIBLE
        }
        
        // 观察记录总数
        creditsRecordViewModel.recordCount.observe(viewLifecycleOwner) { count ->
            hasMoreData = (currentPage + 1) * pageSize < count
        }
        
        // 观察加载状态
        creditsRecordViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading && currentPage == 0) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        
        // 观察错误信息
        creditsRecordViewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                creditsRecordViewModel.clearError()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    
    /**
     * 加载数据
     */
    private fun loadData() {
        val user = userViewModel.currentUser.value
        if (user != null) {
            if (currentType == null) {
                creditsRecordViewModel.getUserCreditsRecords(
                    userId = user.id,
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
            } else {
                creditsRecordViewModel.getUserCreditsRecordsByType(
                    userId = user.id,
                    type = currentType!!,
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
            }
        } else {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
            binding.swipeRefresh.isRefreshing = false
        }
    }
    
    /**
     * 刷新数据
     */
    private fun refreshData() {
        currentPage = 0
        hasMoreData = true
        loadData()
    }
    
    /**
     * 加载更多数据
     */
    private fun loadMoreData() {
        if (hasMoreData) {
            currentPage++
            loadData()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 