<div align="center">

# Unity Hierarchy Viewer

在 JetBrains Rider 中实时查看 Unity 场景层级

[![Build](https://github.com/NightToona/UnityHierarchyViewerClient/actions/workflows/build.yml/badge.svg)](https://github.com/NightToona/UnityHierarchyViewerClient/actions/workflows/build.yml)
[![Unity Package](https://img.shields.io/badge/Unity-Package-222C37?logo=unity)](https://github.com/NightToona/com.nighttoona.idehierarchyviewer)
![Version](https://img.shields.io/badge/version-0.1.1-blue)
![Status](https://img.shields.io/badge/status-work%20in%20progress-orange)

</div>

## 项目简介

**Unity Hierarchy Viewer** 是一款 Rider 插件，用于在 Rider 中查看 Unity
场景的 Hierarchy，减少写脚本时来回切换 Unity Editor 的次数。

插件通过 TCP 接收 Unity 端发送的数据，并在工具窗口中显示 GameObject 的层级结构。

更多细节请查看开发笔记：[Project - Hierarchy Tool](https://github.com/NightToona/UnityClient-Engineering-Notes/blob/0089f13df53eada2bcab9eb2dff6a382e4bb3bfd/Projects/Project%201%20-%20Hierarchy%20Tool.md)

> [!IMPORTANT]
> 项目还在开发中，目前没有发布到 JetBrains Marketplace。
> 使用时需要配合
> [Unity Package](https://github.com/NightToona/com.nighttoona.idehierarchyviewer)。

## 功能

- 在 Rider 中显示 Unity Hierarchy
- 按父子关系展示 GameObject
- Unity Hierarchy 变化时自动刷新
- 支持心跳和断线重连（完善中）

## 安装

先在 Unity 项目中安装配套的
[com.nighttoona.idehierarchyviewer](https://github.com/NightToona/com.nighttoona.idehierarchyviewer)。

Rider 插件目前需要从源码构建，环境需要 **JDK 17**：

```powershell
.\gradlew.bat buildPlugin
```

构建完成后，在 `build/distributions/` 中找到插件 ZIP，然后在 Rider 中选择：

```text
Settings → Plugins → ⚙ → Install Plugin from Disk...
```

## 使用

1. 启动 Unity 端。
2. 启动 Rider 并打开 `Unity Hierarchy View` 工具窗口。
3. 收到 Unity 端的数据后，Hierarchy 会自动显示并更新。

目前通信地址固定为 `127.0.0.1:44571`，建议先启动 Unity 端，再启动 Rider。

## 开发进度

- [x] Rider 与 Unity 之间的 TCP 通信
- [x] 解析 Unity Hierarchy 数据
- [x] 在 Rider 中显示和刷新层级树
- [ ] 完善连接状态与重连
- [ ] 优化 GameObject 的显示效果
- [ ] 支持自定义地址和端口
- [ ] 发布到 JetBrains Marketplace

## 最后

如果发现问题，欢迎在 [Issues](https://github.com/NightToona/UnityHierarchyViewerClient/issues)
中告诉我。

---

本项目基于
[IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
创建。
