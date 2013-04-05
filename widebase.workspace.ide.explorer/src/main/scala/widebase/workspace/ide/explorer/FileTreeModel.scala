package widebase.workspace.ide.explorer

import java.io. { File, FilenameFilter }
import java.util.Vector

import javax.swing.tree. { TreeModel, TreePath }
import javax.swing.event. { TreeModelEvent, TreeModelListener }

import scala.util.control.Breaks. { break, breakable }

class FileTreeModel(root: File) extends TreeModel {

  private val filter = new FilenameFilter {

    def accept(dir: File, name: String) = {

      new File(dir.getPath + "/" + name).isDirectory ||
      name.endsWith(".csv") ||
      name.endsWith(".scala") ||
      name.endsWith(".txt")

    }
  }

  private val listeners = new Vector[TreeModelListener]

  def addTreeModelListener(l: TreeModelListener) {

    if(l != null && !listeners.contains(l))
      listeners.addElement(l)

  }

  def getChild(parent: Object, index: Int) = {

    val directory = parent.asInstanceOf[File]
    val children = directory.list(filter)

    new File(directory, children(index)) {

      override def toString = getName

    }
  }

  def getChildCount(parent: Object): Int = {

    val file = parent.asInstanceOf[File]

    if(file.isDirectory) {

      val list = file.list(filter)

      if(list != null)
        return list.length

    }

    0

  }

  def getIndexOfChild(parent: Object, child: Object): Int = {

    val directory = parent.asInstanceOf[File]
    val file = child.asInstanceOf[File]
    val children = directory.list(filter)

    for(i <- 0 to children.length - 1)
      if(file.getName.equals(children(i)))
        return i

    -1

  }

  def getRoot = root

  def isLeaf(node: Object) = node.asInstanceOf[File].isFile

  def removeTreeModelListener(l: TreeModelListener) {

    if(l != null)
      listeners.removeElement(l)

  }

  def valueForPathChanged(path: TreePath, value: Object) {

    val oldFile = path.getLastPathComponent.asInstanceOf[File]
    val fileParentPath = oldFile.getParent
    val newFileName = value.asInstanceOf[String]
    val targetFile = new File(fileParentPath, newFileName)

    oldFile.renameTo(targetFile)

    val parent = new File(fileParentPath)
    val changedChildrenIndices = Array(getIndexOfChild(parent, targetFile))
    val changedChildren = Array[Object](targetFile)

    fireTreeNodesChanged(
      path.getParentPath,
      changedChildrenIndices,
      changedChildren)

  }

  def fireTreeNodesChanged(event: TreeModelEvent) {

    val listenerCount = listeners.elements

    while(listenerCount.hasMoreElements)
      listenerCount.nextElement.asInstanceOf[TreeModelListener].treeNodesChanged(event)

  }

  def fireTreeNodesInserted(event: TreeModelEvent) {

    val listenerCount = listeners.elements

    while(listenerCount.hasMoreElements)
      listenerCount.nextElement.asInstanceOf[TreeModelListener].treeNodesInserted(event)

  }

  def fireTreeNodesRemoved(event: TreeModelEvent) {

    val listenerCount = listeners.elements

    while(listenerCount.hasMoreElements)
      listenerCount.nextElement.asInstanceOf[TreeModelListener].treeNodesRemoved(event)

  }

  def fireTreeStructureChanged(event: TreeModelEvent) {

    val listenerCount = listeners.elements

    while(listenerCount.hasMoreElements)
      listenerCount.nextElement.asInstanceOf[TreeModelListener].treeStructureChanged(event)

  }

  protected def fireTreeNodesChanged(
    parentPath: TreePath,
    indices: Array[Int],
    children: Array[Object]) {

    val event = new TreeModelEvent(this, parentPath, indices, children)
    val iterator = listeners.iterator
    var listener: TreeModelListener = null

    while(iterator.hasNext) {

      listener = iterator.next.asInstanceOf[TreeModelListener]
      listener.treeNodesChanged(event)

    }
  }
}

