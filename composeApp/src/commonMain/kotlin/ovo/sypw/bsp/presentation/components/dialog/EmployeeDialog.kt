package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import ovo.sypw.bsp.presentation.viewmodel.admin.EmployeeDialogState
import ovo.sypw.bsp.presentation.viewmodel.admin.EmployeeViewModel
import ovo.sypw.bsp.utils.file.createFileUtils

/**
 * 员工添加/编辑Dialog组件
 * 参考部门Dialog的实现，提供员工信息的添加和编辑功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDialog(
    employeeViewModel: EmployeeViewModel,
    dialogState: EmployeeDialogState,
    onDismiss: () -> Unit
) {
    val departments by employeeViewModel.departments.collectAsState()
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
                            text = if (dialogState.isEditMode) "编辑员工" else "添加员工",
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
                        // 用户名输入（编辑模式下禁用）
                        OutlinedTextField(
                            value = dialogState.username,
                            onValueChange = employeeViewModel::updateEmployeeUsername,
                            label = { Text("用户名") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !dialogState.isEditMode,
                            singleLine = true,
                            placeholder = { Text("请输入用户名") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 真实姓名输入
                        OutlinedTextField(
                            value = dialogState.realName,
                            onValueChange = employeeViewModel::updateEmployeeRealName,
                            label = { Text("真实姓名") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入真实姓名") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 头像选择区域
                        Column {
                            Text(
                                text = "头像",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 头像显示区域
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = CircleShape
                                        )
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            employeeViewModel.selectAvatar()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 显示选中的头像或默认图标
                                    if (dialogState.selectedAvatarBytes != null) {
                                        val fileUtils = createFileUtils()
                                        val imageBitmap =
                                            fileUtils.bytesToImageBitmap(dialogState.selectedAvatarBytes)
                                        if (imageBitmap != null) {
                                            Image(
                                                bitmap = imageBitmap,
                                                contentDescription = "选中的头像",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "默认头像",
                                                modifier = Modifier.size(40.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else if (!dialogState.avatarUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = dialogState.avatarUrl,
                                            contentDescription = "当前头像",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "默认头像",
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // 上传状态指示器
                                    if (dialogState.isUploadingAvatar) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = Color.Black.copy(alpha = 0.5f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                // 头像操作按钮
                                Column {
                                    Button(
                                        onClick = { employeeViewModel.selectAvatar() },
                                        enabled = !dialogState.isUploadingAvatar,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("选择头像")
                                    }

                                    if (dialogState.selectedAvatarBytes != null || !dialogState.avatarUrl.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { employeeViewModel.clearSelectedAvatar() },
                                            enabled = !dialogState.isUploadingAvatar,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("清除头像")
                                        }
                                    }
                                }
                            }

                            // 头像提示信息
                            if (!dialogState.avatarUrl.isNullOrEmpty() && dialogState.selectedAvatarBytes == null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "当前使用已上传的头像",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (dialogState.selectedAvatarBytes != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "已选择新头像，保存时将自动上传",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 密码输入（仅在添加模式下显示）
                        if (!dialogState.isEditMode) {
                            var passwordVisible by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = dialogState.password,
                                onValueChange = employeeViewModel::updateEmployeePassword,
                                label = { Text("密码") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("请输入密码") },
                                visualTransformation = if (passwordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                trailingIcon = {
                                    TextButton(
                                        onClick = { passwordVisible = !passwordVisible }
                                    ) {
                                        Text(if (passwordVisible) "隐藏" else "显示")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }


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
                                    onClick = { employeeViewModel.updateEmployeeGender(1) }
                                )
                                Text("男")
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.gender == 2,
                                    onClick = { employeeViewModel.updateEmployeeGender(2) }
                                )
                                Text("女")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 职位选择
                        var jobExpanded by remember { mutableStateOf(false) }
                        val jobOptions = mapOf(
                            1 to "班主任",
                            2 to "讲师",
                            3 to "学工主管",
                            4 to "教研主管",
                            5 to "咨询师",
                        )

                        ExposedDropdownMenuBox(
                            expanded = jobExpanded,
                            onExpandedChange = { jobExpanded = !jobExpanded }
                        ) {
                            OutlinedTextField(
                                value = jobOptions[dialogState.job] ?: "普通员工",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("职位") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = jobExpanded
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = jobExpanded,
                                onDismissRequest = { jobExpanded = false }
                            ) {
                                jobOptions.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            employeeViewModel.updateEmployeeJob(value)
                                            jobExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 部门选择（使用真实的部门数据）
                        var departmentExpanded by remember { mutableStateOf(false) }
                        val selectedDepartment =
                            departments.find { it.id == dialogState.departmentId }

                        ExposedDropdownMenuBox(
                            expanded = departmentExpanded,
                            onExpandedChange = { departmentExpanded = !departmentExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedDepartment?.name ?: "请选择部门",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("部门") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = departmentExpanded
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = departmentExpanded,
                                onDismissRequest = { departmentExpanded = false }
                            ) {
                                departments.forEach { department ->
                                    DropdownMenuItem(
                                        text = { Text(department.name) },
                                        onClick = {
                                            employeeViewModel.updateEmployeeDepartmentId(
                                                department.id ?: 1
                                            )
                                            departmentExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 入职日期输入
                        OutlinedTextField(
                            value = dialogState.entryDate,
                            onValueChange = employeeViewModel::updateEmployeeEntryDate,
                            label = { Text("入职日期") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入入职日期（可选，格式：YYYY-MM-DD）") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !dialogState.isLoading
                        ) {
                            Text("取消")
                        }

                        Button(
                            onClick = employeeViewModel::saveEmployee,
                            enabled = !dialogState.isLoading
                        ) {
                            if (dialogState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (dialogState.isEditMode) "更新" else "添加")
                        }
                    }
                }
            }
        }
    }
}