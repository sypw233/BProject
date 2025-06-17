package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 分页组件
 */
@Composable
fun <T> PaginationComponent(
    pageInfo: PageResultDto<T>,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    var showPageSizeDropdown by remember { mutableStateOf(false) }
    val pageSizeOptions = listOf(9, 18, 27, 36)

    Card(
        modifier = Modifier.fillMaxWidth()
//            .height(100.dp)
    ) {
        if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
            // 紧凑型：垂直布局
            Column(
                modifier = Modifier.padding(layoutConfig.cardPadding),
                verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
            ) {
                // 页面大小选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 分页信息
                    Text(
                        text = "共 ${pageInfo.total} 条记录 ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "每页显示:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Box {
                            TextButton(
                                onClick = { showPageSizeDropdown = true }
                            ) {
                                Text("${pageInfo.size} 条")
                            }

                            DropdownMenu(
                                expanded = showPageSizeDropdown,
                                onDismissRequest = { showPageSizeDropdown = false }
                            ) {
                                pageSizeOptions.forEach { size ->
                                    DropdownMenuItem(
                                        text = { Text("$size 条") },
                                        onClick = {
                                            onPageSizeChange(size)
                                            showPageSizeDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                    }

                    // 页面导航
                    Row(
//                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 上一页
                        IconButton(
                            onClick = { onPageChange(pageInfo.current - 1) },
                            enabled = pageInfo.current > 1
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "上一页"
                            )
                        }

                        // 页码显示
                        Text(
                            text = "${pageInfo.current} / ${pageInfo.pages}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 下一页
                        IconButton(
                            onClick = { onPageChange(pageInfo.current + 1) },
                            enabled = pageInfo.current < pageInfo.pages
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "下一页"
                            )
                        }
                    }
                }


            }
        } else {
            // 中等型和扩展型：水平布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(layoutConfig.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分页信息和页面大小选择
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing)
                ) {
                    Text(
                        text = "共 ${pageInfo.total} 条记录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "每页显示:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Box {
                        TextButton(
                            onClick = { showPageSizeDropdown = true }
                        ) {
                            Text("${pageInfo.size} 条")
                        }

                        DropdownMenu(
                            expanded = showPageSizeDropdown,
                            onDismissRequest = { showPageSizeDropdown = false }
                        ) {
                            pageSizeOptions.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text("$size 条") },
                                    onClick = {
                                        onPageSizeChange(size)
                                        showPageSizeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 页面导航
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 上一页
                    IconButton(
                        onClick = { onPageChange(pageInfo.current - 1) },
                        enabled = pageInfo.current > 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "上一页"
                        )
                    }

                    // 页码按钮组
                    PaginationButtons(
                        currentPage = pageInfo.current,
                        totalPages = pageInfo.pages,
                        onPageChange = onPageChange
                    )

                    // 下一页
                    IconButton(
                        onClick = { onPageChange(pageInfo.current + 1) },
                        enabled = pageInfo.current < pageInfo.pages
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "下一页"
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分页按钮组件
 */
@Composable
private fun PaginationButtons(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    val visiblePages = getVisiblePages(currentPage, totalPages)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visiblePages.forEach { page ->
            when (page) {
                -1 -> {
                    // 省略号
                    Text(
                        text = "...",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    Button(
                        onClick = { if (page != currentPage) onPageChange(page) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (page == currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (page == currentPage) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        ),
                        modifier = Modifier.width(40.dp)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp),
//                        border = if (page != currentPage) {
//                            BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
//                        } else null
                    ) {
                        Text(
                            text = page.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * 获取可见的页码列表
 */
private fun getVisiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val result = mutableListOf<Int>()

    // 总是显示第一页
    result.add(1)

    when {
        currentPage <= 4 -> {
            // 当前页在前面
            result.addAll(2..5)
            result.add(-1) // 省略号
            result.add(totalPages)
        }

        currentPage >= totalPages - 3 -> {
            // 当前页在后面
            result.add(-1) // 省略号
            result.addAll((totalPages - 4)..totalPages)
        }

        else -> {
            // 当前页在中间
            result.add(-1) // 省略号
            result.addAll((currentPage - 1)..(currentPage + 1))
            result.add(-1) // 省略号
            result.add(totalPages)
        }
    }

    return result
}
