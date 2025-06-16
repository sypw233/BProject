package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 班级数据传输对象
 */
@Serializable
data class ClassDto(
    /**
     * 班级ID
     */
    val id: Int? = null,
    
    /**
     * 班级名称
     */
    val name: String,
    
    /**
     * 年级
     */
    val grade: String
)

/**
 * 创建班级请求DTO
 */
@Serializable
data class ClassCreateDto(
    /**
     * 班级名称
     */
    val name: String,
    
    /**
     * 年级
     */
    val grade: String
)

/**
 * 更新班级请求DTO
 */
@Serializable
data class ClassUpdateDto(
    /**
     * 班级ID
     */
    val id: Int,
    
    /**
     * 班级名称
     */
    val name: String,
    
    /**
     * 年级
     */
    val grade: String
)