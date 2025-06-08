package com.lanchenjishu.aicreator.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanchenjishu.aicreator.models.CreationTemplate
import com.lanchenjishu.aicreator.models.CreationType
import com.lanchenjishu.aicreator.repositories.CreationTemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * 创作模板ViewModel
 */
class CreationTemplateViewModel : ViewModel() {
    
    private val repository = CreationTemplateRepository()
    private val templateDao = repository.getTemplateDao()
    
    private val _templates = MutableLiveData<List<CreationTemplate>>()
    val templates: LiveData<List<CreationTemplate>> = _templates
    
    private val _selectedTemplate = MutableLiveData<CreationTemplate?>()
    val selectedTemplate: LiveData<CreationTemplate?> = _selectedTemplate
    
    init {
        // 初始化预设模板
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.initializeDefaultTemplates()
            }
        }
    }
    
    /**
     * 加载所有模板
     */
    fun loadAllTemplates() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                templateDao.getAllTemplates()
            }
            _templates.value = result
        }
    }
    
    /**
     * 根据类型加载模板
     */
    fun loadTemplatesByType(type: CreationType) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                templateDao.getTemplatesByType(type)
            }
            _templates.value = result
        }
    }
    
    /**
     * 加载系统模板
     */
    fun loadSystemTemplates() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                templateDao.getSystemTemplates()
            }
            _templates.value = result
        }
    }
    
    /**
     * 根据ID加载模板
     */
    fun loadTemplateById(id: Long) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                templateDao.getTemplateById(id)
            }
            _selectedTemplate.value = result
        }
    }
    
    /**
     * 创建模板
     */
    fun createTemplate(template: CreationTemplate): Long {
        var id = -1L
        viewModelScope.launch {
            id = withContext(Dispatchers.IO) {
                templateDao.insertTemplate(template)
            }
            
            if (id > 0) {
                // 刷新模板列表
                loadTemplatesByType(template.templateType)
            }
        }
        return id
    }
    
    /**
     * 更新模板
     */
    fun updateTemplate(template: CreationTemplate): Boolean {
        var success = false
        viewModelScope.launch {
            val rowsAffected = withContext(Dispatchers.IO) {
                val updatedTemplate = template.copy(updateTime = Date())
                templateDao.updateTemplate(updatedTemplate)
            }
            
            success = rowsAffected > 0
            
            if (success) {
                // 刷新模板列表
                loadTemplatesByType(template.templateType)
                
                // 更新选中的模板
                if (_selectedTemplate.value?.id == template.id) {
                    _selectedTemplate.value = template
                }
            }
        }
        return success
    }
    
    /**
     * 删除模板
     */
    fun deleteTemplate(id: Long): Boolean {
        var success = false
        viewModelScope.launch {
            val rowsAffected = withContext(Dispatchers.IO) {
                templateDao.deleteTemplate(id)
            }
            
            success = rowsAffected > 0
            
            if (success) {
                // 从列表中移除
                val currentList = _templates.value ?: emptyList()
                _templates.value = currentList.filter { it.id != id }
                
                // 如果当前选中的是被删除的模板，则清空
                if (_selectedTemplate.value?.id == id) {
                    _selectedTemplate.value = null
                }
            }
        }
        return success
    }
    
    /**
     * 选择模板
     */
    fun selectTemplate(template: CreationTemplate?) {
        _selectedTemplate.value = template
    }
    
    /**
     * 根据提示词模板生成提示词
     */
    fun generatePromptFromTemplate(template: CreationTemplate, placeholders: Map<String, String>): String {
        var prompt = template.promptTemplate
        
        // 替换所有占位符
        placeholders.forEach { (key, value) ->
            prompt = prompt.replace("[$key]", value)
        }
        
        return prompt
    }
} 