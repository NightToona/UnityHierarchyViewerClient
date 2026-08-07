<div align="center">

# Unity Hierarchy Viewer Rider Plugin

在 JetBrains Rider 中实时查看 Unity 场景层级

[![Build](https://github.com/NightToona/UnityHierarchyViewerClient/actions/workflows/build.yml/badge.svg)](https://github.com/NightToona/UnityHierarchyViewerClient/actions/workflows/build.yml)
[![Unity Package](https://img.shields.io/badge/Unity-Package-222C37?logo=unity)](https://github.com/NightToona/com.nighttoona.idehierarchyviewer)
![Version](https://img.shields.io/badge/version-0.5.1-blue)
![Status](https://img.shields.io/badge/status-MVP%20completed-success)

</div>

## 项目简介

**Unity Hierarchy Viewer Rider Plugin** 是一个 Rider 插件，用于将 Unity Editor 中的 Hierarchy 数据实时同步到 JetBrains IDE，并提供树形结构查看能力。

插件作为 Unity Editor 扩展的配套客户端，通过 TCP 接收 Unity 端发送的数据，解析 XML 数据并在 Rider Tool Window 中展示。

实时同步效果：

<img width="460" height="259" alt="Hierarchy" src="https://github.com/user-attachments/assets/cd93add4-e49c-42ee-9c87-9de2b7a491ee" />


更多细节请查看开发笔记：[Project - Hierarchy Tool](https://github.com/NightToona/UnityClient-Engineering-Notes/blob/0089f13df53eada2bcab9eb2dff6a382e4bb3bfd/Projects/Project%201%20-%20Hierarchy%20Tool.md)

> 当前版本已完成 Unity ↔ Rider 数据同步链路。
>
> 后续计划：
> - 发布 JetBrains Marketplace
> - UI展示优化
> - 更多配置支持

## 功能

- Unity Hierarchy 实时同步
- GameObject 父子结构树形展示
- XML 数据解析与模型映射
- 基于 TCP 的跨进程通信
- 心跳检测与连接状态管理
- Kotlin Coroutines 异步通信处理
- Rider Tool Window 集成

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

## 开发状态

### 已完成

- Rider Plugin 基础框架搭建
- Unity Hierarchy 数据接收
- XML 数据解析
- Tree UI 展示
- TCP 通信
- 心跳检测与连接状态管理

### 未来计划

- UI显示优化
- 多客户端支持
- JetBrains Marketplace 发布

## 最后

如果发现问题，欢迎在 [Issues](https://github.com/NightToona/UnityHierarchyViewerClient/issues)
中告诉我。

---

本项目基于
[IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
创建。
