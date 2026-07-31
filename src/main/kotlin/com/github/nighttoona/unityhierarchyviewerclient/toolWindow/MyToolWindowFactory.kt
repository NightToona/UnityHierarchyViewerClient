package com.github.nighttoona.unityhierarchyviewerclient.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyData
import com.github.nighttoona.unityhierarchyviewerclient.services.HierarchyListener
import com.github.nighttoona.unityhierarchyviewerclient.services.HierarchyService
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.tree.DefaultTreeModel


class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        content.setDisposer(myToolWindow)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) : Disposable {

        private val hierarchyService = toolWindow.project.service<HierarchyService>()
        private val reconnectButton = JButton("重新连接")
        private val hierarchyTree = HierarchyTree()


        private val hierarchyListener = object : HierarchyListener {
            override fun onChange(data: HierarchyData) {
                hierarchyTree.model = hierarchyTree.buildHierarchyTree(data)
            }
        }


        init {
            // 调节树的显示效果
            hierarchyTree.showsRootHandles = true
            hierarchyTree.rowHeight = 0
            hierarchyTree.model = DefaultTreeModel(null)
            hierarchyTree.emptyText.text = "等待 Unity Hierarchy 端数据响应……"


            reconnectButton.addActionListener {
                hierarchyService.runClient()
            }

            // 初始化监听器
            hierarchyService.addListener(hierarchyListener)
        }

        override fun dispose() {
            // 销毁监听器
            hierarchyService.removeListener(hierarchyListener)
        }


        fun getContent(): JBPanel<*> {

            val toolbarPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.LEFT)).apply {
                add(reconnectButton)
            }

            return JBPanel<JBPanel<*>>(BorderLayout()).apply {
                add(toolbarPanel, BorderLayout.NORTH)
                add(JBScrollPane(hierarchyTree), BorderLayout.CENTER)
            }


        }


        /*
        fun getContent() = JBPanel<JBPanel<*>>(BorderLayout()).apply {

            add(JBScrollPane(hierarchyTree))

        }*/
    }
}
