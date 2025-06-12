package ovo.sypw.bsp.utils

/**
 * 日志工具使用示例
 * 展示如何在KMP项目中使用跨平台日志功能
 */
class LoggerExample {
    
    fun demonstrateLogging() {
        // 1. 基本日志使用
        Logger.d("MyTag", "这是一个调试日志")
        Logger.i("MyTag", "这是一个信息日志")
        Logger.w("MyTag", "这是一个警告日志")
        Logger.e("MyTag", "这是一个错误日志")
        
        // 2. 简化的日志使用（使用默认标签"KMP"）
        Logger.d("简化的调试日志")
        Logger.i("简化的信息日志")
        Logger.w("简化的警告日志")
        Logger.e("简化的错误日志")
        
        // 3. 带异常的日志
        try {
            throw Exception("这是一个测试异常")
        } catch (e: Exception) {
            Logger.e("ExceptionTag", "捕获到异常", e)
        }
        
        // 4. 打印对象信息
        val testObject = mapOf("key1" to "value1", "key2" to "value2")
        Logger.printObject("ObjectTag", testObject)
        Logger.printObject(testObject) // 使用默认标签
        
        // 5. 使用扩展函数（推荐方式）
        this.logd("使用扩展函数的调试日志")
        this.logi("使用扩展函数的信息日志")
        this.logw("使用扩展函数的警告日志")
        this.loge("使用扩展函数的错误日志")
        
        // 6. 在网络请求中使用
        demonstrateNetworkLogging()
        
        // 7. 在数据处理中使用
        demonstrateDataProcessingLogging()
    }
    
    private fun demonstrateNetworkLogging() {
        // 模拟网络请求日志
        Logger.i("Network", "开始发送登录请求")
        
        // 模拟请求参数日志
        val requestData = mapOf(
            "username" to "testuser",
            "password" to "***" // 注意：密码应该脱敏
        )
        Logger.d("Network", "请求参数: $requestData")
        
        // 模拟响应日志
        Logger.i("Network", "登录请求成功")
        
        // 模拟错误日志
        try {
            // 模拟网络错误
            throw Exception("网络连接超时")
        } catch (e: Exception) {
            Logger.e("Network", "网络请求失败", e)
        }
    }
    
    private fun demonstrateDataProcessingLogging() {
        // 模拟数据处理日志
        Logger.d("DataProcessor", "开始处理用户数据")
        
        val userData = listOf(
            mapOf("id" to 1, "name" to "张三"),
            mapOf("id" to 2, "name" to "李四")
        )
        
        Logger.i("DataProcessor", "处理 ${userData.size} 条用户数据")
        
        userData.forEach { user ->
            Logger.d("DataProcessor", "处理用户: ${user["name"]}")
        }
        
        Logger.i("DataProcessor", "数据处理完成")
    }
}

/**
 * 在ViewModel中使用日志的示例
 */
class ExampleViewModel {
    
    fun login(username: String, password: String) {
        // 使用扩展函数记录日志
        this.logi("开始登录流程")
        this.logd("用户名: $username")
        
        try {
            // 模拟登录逻辑
            if (username.isBlank()) {
                this.logw("用户名为空")
                return
            }
            
            this.logi("登录成功")
        } catch (e: Exception) {
            this.loge("登录失败", e)
        }
    }
}

/**
 * 在Repository中使用日志的示例
 */
class ExampleRepository {
    
    suspend fun fetchData(): Result<String> {
        return try {
            Logger.i("Repository", "开始获取数据")
            
            // 模拟网络请求
            val data = "模拟数据"
            
            Logger.d("Repository", "获取到数据: $data")
            Logger.i("Repository", "数据获取成功")
            
            Result.success(data)
        } catch (e: Exception) {
            Logger.e("Repository", "数据获取失败", e)
            Result.failure(e)
        }
    }
}