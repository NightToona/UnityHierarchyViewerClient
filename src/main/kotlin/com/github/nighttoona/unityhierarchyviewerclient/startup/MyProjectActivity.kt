package com.github.nighttoona.unityhierarchyviewerclient.startup

import com.github.nighttoona.unityhierarchyviewerclient.network.HierarchyTcpClient
import com.github.nighttoona.unityhierarchyviewerclient.services.HierarchyService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

// 启动时执行的活动，负责启动TCP客户端
class MyProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {

        // 从当前项目的统一管理容器里，取得那个共享实例
        val hierarchyService = project.service<HierarchyService>()

        // 供全局使用的TCP客户端运行实例
        hierarchyService.runClient()
    }
}