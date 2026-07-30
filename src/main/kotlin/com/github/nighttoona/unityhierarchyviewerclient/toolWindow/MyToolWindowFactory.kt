package com.github.nighttoona.unityhierarchyviewerclient.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyData
import com.github.nighttoona.unityhierarchyviewerclient.services.HierarchyListener
import com.github.nighttoona.unityhierarchyviewerclient.services.HierarchyService
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service


class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        content.setDisposer(myToolWindow)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow): Disposable {

        private val hierarchyService = toolWindow.project.service<HierarchyService>()
        private val sceneJBLabel = JBLabel("等待 Unity Hierarchy 数据……")


        private val hierarchyListener = object : HierarchyListener{
            override fun onChange(data: HierarchyData) {
                sceneJBLabel.text = "当前场景: ${data.sceneName}"
            }
        }


        init {
            // 初始化监听器
            hierarchyService.addListener(hierarchyListener)
        }

        override fun dispose(){
            // 销毁监听器
            hierarchyService.removeListener(hierarchyListener)
        }


        fun getContent() = JBPanel<JBPanel<*>>().apply {

            add(sceneJBLabel)

        }
    }
}
