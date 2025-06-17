package ovo.sypw.bsp.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bproject.composeapp.generated.resources.Res
import bproject.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import ovo.sypw.bsp.Greeting

/**
 * 首页屏幕组件
 * 显示应用的主要内容和欢迎信息
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }
    val greeting = remember { Greeting().greet() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        Text(
            text = "欢迎使用应用",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // 交互按钮
        Button(
            onClick = { showContent = !showContent },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(if (showContent) "隐藏内容" else "显示内容")
        }

        // 动画显示内容
        AnimatedVisibility(showContent) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = "Compose Multiplatform Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = "Compose: $greeting",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "这是一个跨平台应用示例",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部信息
        Text(
            text = "Kotlin Multiplatform 强力驱动",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}