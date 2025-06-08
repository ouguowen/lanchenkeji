package com.lanchenjishu.aicreator.repositories

import com.lanchenjishu.aicreator.dao.CreationTemplateDao
import com.lanchenjishu.aicreator.models.CreationTemplate
import com.lanchenjishu.aicreator.models.CreationType

/**
 * 创作模板仓库
 */
class CreationTemplateRepository {
    
    private val templateDao = CreationTemplateDao()
    
    /**
     * 获取模板DAO
     */
    fun getTemplateDao(): CreationTemplateDao {
        return templateDao
    }
    
    /**
     * 初始化预设模板
     */
    fun initializeDefaultTemplates() {
        // 检查是否已经初始化过
        val templates = templateDao.getSystemTemplates()
        if (templates.isNotEmpty()) {
            return
        }
        
        // 文生图模板
        val textToImageTemplates = listOf(
            CreationTemplate(
                templateName = "写实风景",
                templateType = CreationType.TEXT_TO_IMAGE,
                promptTemplate = "高清写实风景照片，[描述]，4K，精细细节，自然光照",
                description = "生成高品质的写实风景图片",
                isSystem = true
            ),
            CreationTemplate(
                templateName = "动漫人物",
                templateType = CreationType.TEXT_TO_IMAGE,
                promptTemplate = "高质量动漫风格，[角色描述]，精细线条，明亮色彩，优质作画",
                description = "生成动漫风格的人物图片",
                isSystem = true
            ),
            CreationTemplate(
                templateName = "产品展示",
                templateType = CreationType.TEXT_TO_IMAGE,
                promptTemplate = "专业产品摄影，[产品描述]，工作室光照，白色背景，高清细节，商业风格",
                description = "生成商业风格的产品展示图片",
                isSystem = true
            )
        )
        
        // 文案创作模板
        val copywritingTemplates = listOf(
            CreationTemplate(
                templateName = "产品描述",
                templateType = CreationType.COPYWRITING,
                promptTemplate = "为[产品名称]创作一段吸引人的产品描述，突出其核心功能和优势，使用说服力强的语言，包含号召性用语。",
                description = "生成产品销售页面的描述文案",
                isSystem = true
            ),
            CreationTemplate(
                templateName = "营销邮件",
                templateType = CreationType.COPYWRITING,
                promptTemplate = "创作一封关于[主题]的营销邮件，包含吸引人的主题行、个性化开场白、产品价值介绍、优惠信息以及明确的行动召唤。",
                description = "生成营销邮件文案",
                isSystem = true
            ),
            CreationTemplate(
                templateName = "社交媒体",
                templateType = CreationType.COPYWRITING,
                promptTemplate = "为[平台名称]创作一条关于[主题]的引人入胜的社交媒体帖子，使用简洁有力的语言，包含相关话题标签和互动引导语。",
                description = "生成社交媒体文案",
                isSystem = true
            )
        )
        
        // 数字人模板
        val digitalHumanTemplates = listOf(
            CreationTemplate(
                templateName = "产品介绍",
                templateType = CreationType.DIGITAL_HUMAN,
                promptTemplate = "您好，我将为您介绍[产品名称]。这款产品的主要特点包括[特点]。它能够帮助用户[优势]。现在购买还可以享受[优惠]。",
                description = "数字人产品介绍脚本",
                isSystem = true
            ),
            CreationTemplate(
                templateName = "教学视频",
                templateType = CreationType.DIGITAL_HUMAN,
                promptTemplate = "大家好，今天我将教大家如何[主题]。首先，我们需要[步骤1]。接下来，[步骤2]。最后，[步骤3]。希望这个教程对大家有所帮助。",
                description = "数字人教学视频脚本",
                isSystem = true
            )
        )
        
        // 保存所有模板
        (textToImageTemplates + copywritingTemplates + digitalHumanTemplates).forEach {
            templateDao.insertTemplate(it)
        }
    }
} 