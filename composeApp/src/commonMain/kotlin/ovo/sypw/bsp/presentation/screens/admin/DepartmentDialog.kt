package ovo.sypw.bsp.presentation.screens.admin

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
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.presentation.viewmodel.DepartmentDialogState

/**
 * 部门添加/编辑Dialog
 * @param viewModel AdminViewModel实例
 */
@Composable
fun DepartmentDialog(
    viewModel: AdminViewModel
) {
    val dialogState by viewModel.departmentDialogState.collectAsState()
    
    if (dialogState.isVisible) {
        DepartmentDialogContent(
            dialogState = dialogState,
            onDepartmentNameChange = viewModel::updateDepartmentName,
            onSave = viewModel::saveDepartment,
            onDismiss = viewModel::hideDepartmentDialog
        )
    }
}

/**
 * 部门Dialog内容组件
 * @param dialogState Dialog状态
 * @param onDepartmentNameChange 部门名称变化回调
 * @param onSave 保存回调
 * @param onDismiss 取消回调
 */
@Composable
private fun DepartmentDialogContent(
    dialogState: DepartmentDialogState,
    onDepartmentNameChange: (String) -> Unit,
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
                text = if (dialogState.isEditMode) "编辑部门" else "添加部门",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 部门名称输入框
                OutlinedTextField(
                    value = dialogState.departmentName,
                    onValueChange = onDepartmentNameChange,
                    label = { Text("部门名称") },
                    placeholder = { Text("请输入部门名称") },
                    enabled = !dialogState.isLoading,
                    isError = dialogState.errorMessage != null,
                    supportingText = {
                        if (dialogState.errorMessage != null) {
                            Text(
                                text = dialogState.errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("部门名称不能超过50个字符")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                
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
                enabled = !dialogState.isLoading && dialogState.departmentName.isNotBlank()
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