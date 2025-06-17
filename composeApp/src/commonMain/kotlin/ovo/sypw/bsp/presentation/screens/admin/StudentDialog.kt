package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ovo.sypw.bsp.presentation.viewmodel.admin.StudentDialogState
import ovo.sypw.bsp.presentation.viewmodel.admin.StudentViewModel

/**
 * 学生添加/编辑Dialog组件
 * 参考员工Dialog的实现，提供学生信息的添加和编辑功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDialog(
    studentViewModel: StudentViewModel,
    dialogState: StudentDialogState,
    onDismiss: () -> Unit
) {
    val classes by studentViewModel.classes.collectAsState()
    if (dialogState.isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // 标题栏
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (dialogState.isEditMode) "编辑学生" else "添加学生",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 错误消息显示
                    if (dialogState.errorMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = dialogState.errorMessage,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 表单内容
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {

                        // 姓名输入
                        OutlinedTextField(
                            value = dialogState.name,
                            onValueChange = studentViewModel::updateStudentName,
                            label = { Text("姓名") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入姓名") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                // 性别选择
                                Text(
                                    text = "性别",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = dialogState.gender == 1,
                                            onClick = { studentViewModel.updateStudentGender(1) }
                                        )
                                        Text("男")
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = dialogState.gender == 2,
                                            onClick = { studentViewModel.updateStudentGender(2) }
                                        )
                                        Text("女")
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))


                                // 班级选择（使用真实的班级数据）
                                var classExpanded by remember { mutableStateOf(false) }
                                val selectedClass = classes.find { it.id == dialogState.classId }

                                ExposedDropdownMenuBox(
                                    expanded = classExpanded,
                                    onExpandedChange = { classExpanded = !classExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedClass?.name ?: "请选择班级",
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("班级") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = classExpanded
                                            )
                                        }
                                    )
                                    ExposedDropdownMenu(
                                        expanded = classExpanded,
                                        onDismissRequest = { classExpanded = false }
                                    ) {
                                        classes.forEach { classItem ->
                                            if (classItem.id == null) return@forEach
                                            DropdownMenuItem(
                                                text = { Text(classItem.name) },
                                                onClick = {
                                                    studentViewModel.updateStudentClassId(classItem.id)
                                                    classExpanded = false
                                                }
                                            )
                                        }
                                    }

                                }


                            }

                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // 状态选择
                        var statusExpanded by remember { mutableStateOf(false) }
                        val statusOptions = mapOf(
                            1 to "在读",
                            2 to "已毕业",
                            3 to "休学",
                            4 to "退学"
                        )
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = !statusExpanded }
                        ) {
                            OutlinedTextField(
                                value = statusOptions[dialogState.status] ?: "在读",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("状态") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = statusExpanded
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                statusOptions.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            studentViewModel.updateStudentStatus(value)
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(
                            value = dialogState.birthDate,
                            onValueChange = studentViewModel::updateStudentBirthDate,
                            label = { Text("出生日期") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入出生日期（可选，格式：YYYY-MM-DD）") }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(
                            value = dialogState.joinDate,
                            onValueChange = studentViewModel::updateStudentJoinDate,
                            label = { Text("入学日期") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入入学日期（可选，格式：YYYY-MM-DD）") }
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // 底部按钮
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                enabled = !dialogState.isLoading
                            ) {
                                Text("取消")
                            }

                            Button(
                                onClick = {
                                    studentViewModel.submitStudentForm()
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !dialogState.isLoading && dialogState.isValid()
                            ) {
                                if (dialogState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(if (dialogState.isEditMode) "更新" else "创建")
                            }
                        }
                    }
                }
            }
        }
    }

}