# MVVM Framework

Android MVVM 快速开发框架，基于 Kotlin + Jetpack + Hilt + Coroutines 构建。

[English](./README.md) | [中文](./README_ZH.md)

## 📥 安装

在 `build.gradle` (Project) 中添加 JitPack 仓库：

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

在 `build.gradle` (Module) 中添加依赖：

```kotlin
dependencies {
    implementation("com.github.你的用户名:mvvmframe:1.0.0")
}
```

## 📚 功能

- ✅ MVVM 架构基类 (BaseActivity, BaseFragment, BaseViewModel)
- ✅ Hilt 依赖注入
- ✅ Room 本地数据库
- ✅ Retrofit + OkHttp 网络请求
- ✅ Coil 图片加载
- ✅ Repository 模式
- ✅ 统一状态管理 (加载中/空状态/错误)
- ✅ 工具类 (Toast, Time, Network)

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.
