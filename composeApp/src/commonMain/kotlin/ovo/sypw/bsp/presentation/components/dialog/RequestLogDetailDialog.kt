package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ovo.sypw.bsp.presentation.viewmodel.admin.RequestLogDetailState

/**
 * 请求日志详情Dialog
 * 显示请求日志的详细信息
 */
@Composable
fun RequestLogDetailDialog(
    detailState: RequestLogDetailState,
    onDismiss: () -> Unit
) {
    if (detailState.isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 标题
                    Text(
                        text = "请求日志详情",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (detailState.isLoading) {
                        // 加载状态
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("加载中...")
                        }
                    } else if (detailState.errorMessage != null) {
                        // 错误状态
                        Text(
                            text = "加载失败: ${detailState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (detailState.requestLog != null) {
                        // 显示详情内容
                        val requestLog = detailState.requestLog

                        // 基本信息
                        DetailSection(title = "基本信息") {
                            DetailItem(label = "日志ID", value = requestLog.id.toString())
                            DetailItem(label = "用户ID", value = requestLog.userId.toString())
                            DetailItem(label = "用户名", value = requestLog.username)
                            DetailItem(
                                label = "创建时间",
                                value = requestLog.createdAt
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 请求信息
                        DetailSection(title = "请求信息") {
                            DetailItem(
                                label = "请求方法",
                                value = requestLog.requestMethod,
                                valueColor = getRequestMethodColor(requestLog.requestMethod)
                            )
                            DetailItem(label = "请求URL", value = requestLog.requestUrl)
                            DetailItem(
                                label = "请求参数",
                                value = requestLog.requestParams ?: "无"
                            )
                            DetailItem(
                                label = "请求体",
                                value = requestLog.requestBody ?: "无"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 响应信息
                        DetailSection(title = "响应信息") {
                            DetailItem(
                                label = "响应状态",
                                value = requestLog.responseStatus.toString(),
                                valueColor = getResponseStatusColor(requestLog.responseStatus)
                            )
                            DetailItem(
                                label = "响应时间",
                                value = "${requestLog.responseTime}ms"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 客户端信息
                        DetailSection(title = "客户端信息") {
                            DetailItem(label = "IP地址", value = requestLog.ipAddress)
                            DetailItem(
                                label = "用户代理",
                                value = requestLog.userAgent ?: "未知"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 关闭按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("关闭")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 详情区域组件
 */
@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

/**
 * 详情项组件
 */
@Composable
private fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 获取请求方法颜色
 * @param method 请求方法
 * @return 方法颜色
 */
@Composable
private fun getRequestMethodColor(method: String): Color {
    return when (method.uppercase()) {
        "GET" -> MaterialTheme.colorScheme.primary
        "POST" -> MaterialTheme.colorScheme.secondary
        "PUT" -> MaterialTheme.colorScheme.tertiary
        "DELETE" -> MaterialTheme.colorScheme.error
        "PATCH" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

/**
 * 获取响应状态码颜色
 * @param status 响应状态码
 * @return 状态颜色
 */
@Composable
private fun getResponseStatusColor(status: Int): Color {
    return when (status) {
        in 200..299 -> MaterialTheme.colorScheme.primary // 成功
        in 300..399 -> MaterialTheme.colorScheme.secondary // 重定向
        in 400..499 -> MaterialTheme.colorScheme.tertiary // 客户端错误
        in 500..599 -> MaterialTheme.colorScheme.error // 服务器错误
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}