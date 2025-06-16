package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Admin页面配置管理
 * 统一管理Admin页面的所有配置信息，包括Tab选项、侧边栏选项等
 */
object AdminConfig {
    
    /**
     * Admin Tab页面定义
     */
    data class AdminTab(
        val index: Int,
        val title: String,
        val icon: ImageVector,
        val route: String
    )
    
    /**
     * Admin页面的所有Tab选项
     */
    val adminTabs = listOf(
        AdminTab(
            index = 0,
            title = "数据仪表板",
            icon = Icons.Default.Dashboard,
            route = "admin/dashboard"
        ),
        AdminTab(
            index = 1,
            title = "部门管理",
            icon = Icons.Default.Business,
            route = "admin/department"
        ),
        AdminTab(
            index = 2,
            title = "员工管理",
            icon = Icons.Default.Group,
            route = "admin/employee"
        ),
        AdminTab(
            index = 3,
            title = "班级管理",
            icon = Icons.Default.School,
            route = "admin/class"
        ),
        AdminTab(
            index = 4,
            title = "学生管理",
            icon = Icons.Default.Person,
            route = "admin/student"
        ),
        AdminTab(
            index = 5,
            title = "公告管理",
            icon = Icons.Default.Campaign,
            route = "admin/announcement"
        ),
        AdminTab(
            index = 6,
            title = "图片测试",
            icon = Icons.Default.Image,
            route = "admin/imagetest"
        ),
        AdminTab(
            index = 7,
            title = "文件上传",
            icon = Icons.Default.Upload,
            route = "admin/fileupload"
        )
    )
    
    /**
     * 获取Tab标题
     * @param index Tab索引
     * @return Tab标题
     */
    fun getTabTitle(index: Int): String {
        return adminTabs.getOrNull(index)?.title ?: "未知页面"
    }
    
    /**
     * 获取Tab图标
     * @param index Tab索引
     * @return Tab图标
     */
    fun getTabIcon(index: Int): ImageVector {
        return adminTabs.getOrNull(index)?.icon ?: Icons.Default.Business
    }
    
    /**
     * 获取Tab路由
     * @param index Tab索引
     * @return Tab路由
     */
    fun getTabRoute(index: Int): String {
        return adminTabs.getOrNull(index)?.route ?: "admin"
    }
    
    /**
     * 获取所有Tab标题列表
     * @return Tab标题列表
     */
    fun getAllTabTitles(): List<String> {
        return adminTabs.map { it.title }
    }
    
    /**
     * 获取Tab总数
     * @return Tab总数
     */
    fun getTabCount(): Int {
        return adminTabs.size
    }
    
    /**
     * 验证Tab索引是否有效
     * @param index Tab索引
     * @return 是否有效
     */
    fun isValidTabIndex(index: Int): Boolean {
        return index in 0 until adminTabs.size
    }
}