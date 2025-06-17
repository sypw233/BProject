package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ovo.sypw.bsp.presentation.viewmodel.admin.ClassDialogState
import ovo.sypw.bsp.presentation.viewmodel.admin.ClassViewModel

/**
 * 班级添加/编辑Dialog
 */
@Composable
fun ClassDialog(viewModel: ClassViewModel) {
    val dialogState by viewModel.classDialogState.collectAsState()

    // 添加调试日志
    SideEffect {
        println("ClassDialog重组: isVisible=${dialogState.isVisible}, isEditMode=${dialogState.isEditMode}")
    }

    println("ClassDialog被调用: isVisible=${dialogState.isVisible}")

    if (dialogState.isVisible) {
        println("显示ClassDialogContent")
        ClassDialogContent(
            dialogState = dialogState,
            onClassNameChange = viewModel::updateClassName,
            onClassGradeChange = viewModel::updateClassGrade,
            onSave = viewModel::saveClass,
            onDismiss = viewModel::hideClassDialog
        )
    } else {
        println("ClassDialog不可见，不显示内容")
    }
}

/**
 * 班级Dialog内容组件
 * @param dialogState Dialog状态
 * @param onClassNameChange 班级名称变化回调
 * @param onClassGradeChange 年级变化回调
 * @param onSave 保存回调
 * @param onDismiss 取消回调
 */
@Composable
private fun ClassDialogContent(
    dialogState: ClassDialogState,
    onClassNameChange: (String) -> Unit,
    onClassGradeChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = {
            if (!dialogState.isLoading) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = if (dialogState.isEditMode) "编辑班级" else "添加班级",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 班级名称输入框
                OutlinedTextField(
                    value = dialogState.className,
                    onValueChange = onClassNameChange,
                    label = { Text("班级名称") },
                    placeholder = { Text("请输入班级名称") },
                    enabled = !dialogState.isLoading,
                    isError = dialogState.errorMessage != null && dialogState.errorMessage.contains(
                        "班级名称"
                    ),
                    supportingText = {
                        if (dialogState.errorMessage != null && dialogState.errorMessage.contains("班级名称")) {
                            Text(
                                text = dialogState.errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("班级名称不能超过50个字符")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                // 年级输入框
                OutlinedTextField(
                    value = dialogState.classGrade,
                    onValueChange = onClassGradeChange,
                    label = { Text("年级") },
                    placeholder = { Text("请输入年级") },
                    enabled = !dialogState.isLoading,
                    isError = dialogState.errorMessage != null && dialogState.errorMessage.contains(
                        "年级"
                    ),
                    supportingText = {
                        if (dialogState.errorMessage != null && dialogState.errorMessage.contains("年级")) {
                            Text(
                                text = dialogState.errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("年级不能超过20个字符")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 通用错误消息
                if (dialogState.errorMessage != null &&
                    !dialogState.errorMessage.contains("班级名称") &&
                    !dialogState.errorMessage.contains("年级")
                ) {
                    Text(
                        text = dialogState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 加载指示器
                if (dialogState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(20.dp).height(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (dialogState.isEditMode) "正在更新..." else "正在添加...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !dialogState.isLoading &&
                        dialogState.className.isNotBlank() &&
                        dialogState.classGrade.isNotBlank()
            ) {
                Text(if (dialogState.isEditMode) "更新" else "添加")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !dialogState.isLoading
            ) {
                Text("取消")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !dialogState.isLoading,
            dismissOnClickOutside = !dialogState.isLoading
        ),
        modifier = Modifier.widthIn(min = 400.dp, max = 600.dp)
    )

    // 自动聚焦到输入框
    LaunchedEffect(dialogState.isVisible) {
        if (dialogState.isVisible && !dialogState.isEditMode) {
            focusRequester.requestFocus()
        }
    }
}