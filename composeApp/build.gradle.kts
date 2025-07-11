import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
//        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "ovo.sypw.bsp")
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            // Android平台特定的Ktor客户端
            implementation(libs.ktor.client.android)
            // Koin Android支持
            implementation(libs.koin.android)


        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kmp.viewmodel)
            implementation(libs.kmp.viewmodel.compose)
            implementation(libs.material.icons.extended)
            // Ktor网络请求核心库
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            // Koin依赖注入
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.kmp.viewmodel.koin.compose)
            // 跨平台日期时间库
            implementation(libs.kotlinx.datetime)
            // 图片加载库
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            // 富文本编辑器
            implementation(libs.richeditor.compose)
            // Markdown渲染器
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.m3)

            implementation(libs.compose.chart)

            // FileKit - 跨平台文件操作库
            implementation("io.github.vinceglb:filekit-core:0.8.7")
            implementation("io.github.vinceglb:filekit-compose:0.8.7")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // Desktop平台特定的Ktor客户端
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            // iOS平台特定的Ktor客户端
            implementation(libs.ktor.client.darwin)
//            implementation(libs.compose.chart.ios)
        }

    }
}

android {
    namespace = "ovo.sypw.bsp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ovo.sypw.bsp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    // 修复Lint分析器bug - 禁用有问题的检查器
    lint {
        disable += "NullSafeMutableLiveData"
        // 可选：如果还有其他lint问题，可以添加更多禁用项
        abortOnError = false
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

}

compose.desktop {
    application {
        mainClass = "ovo.sypw.bsp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ovo.sypw.bsp"
            packageVersion = "1.000000000000000000000.1"
        }
    }
}
