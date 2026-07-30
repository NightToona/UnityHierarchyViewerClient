package com.github.nighttoona.unityhierarchyviewerclient.toolWindow

import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyData
import com.github.nighttoona.unityhierarchyviewerclient.parser.HierarchyNode
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class HierarchyTree : Tree() {


    // 根据 HierarchyData 构建树模型
    fun buildHierarchyTree(data: HierarchyData): DefaultTreeModel {

        val sceneRoot = DefaultMutableTreeNode(data.sceneName)

        data.roots.forEach { rootNode ->
            val rootTreeNode = buildTreeNode(rootNode)
            sceneRoot.add(rootTreeNode)
        }

        return DefaultTreeModel(sceneRoot)
    }

    // 递归构建树节点
    private fun buildTreeNode(node: HierarchyNode): DefaultMutableTreeNode {

        val treeNode = DefaultMutableTreeNode(node.name)

        node.children.forEach{ child ->
            val childTreeNode = buildTreeNode(child)
            treeNode.add(childTreeNode)
        }

        return treeNode
    }


}