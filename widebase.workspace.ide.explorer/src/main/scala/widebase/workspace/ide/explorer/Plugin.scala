package widebase.workspace.ide.explorer

import java.awt.BorderLayout
import java.awt.event. { MouseAdapter, MouseEvent }
import java.io.File
import java.util. { Collections, List, Vector }

import javax.swing.JTree
import javax.swing.event. { TreeSelectionEvent, TreeSelectionListener }
import javax.swing.tree.DefaultMutableTreeNode

import scala.swing. { BorderPanel, Component, Orientation, SplitPane }

import widebase.workspace. { PagedPane, Tree }
import widebase.workspace.ide.editor.EditPanel
import widebase.workspace.runtime.PluginLike

class Plugin extends PluginLike {

  import widebase.workspace.runtime
  import widebase.workspace.util

  val category = Plugin.category
  val homepage = Plugin.homepage
  val id = Plugin.id
  val name = Plugin.name

  override def option = None

  override def register {

    frame.panel.peer.remove(
      frame.panel.layoutManager.getLayoutComponent(BorderLayout.CENTER))

    val logPane = new Component {

      override lazy val peer = runtime.logPane.component

    }

    val hSplitPane = new SplitPane(
      Orientation.Horizontal,
      frame.pagedPane,
      logPane)

    val tree =  new Tree {

      val listener = new MouseAdapter {

        override def mousePressed(event: MouseEvent) {

          val row = peer.getRowForLocation(event.getX, event.getY)
          val path = peer.getPathForLocation(event.getX, event.getY)

          if(row != -1) {

            if(event.getClickCount == 2) {

              val file = path.getLastPathComponent.asInstanceOf[File]

              if(file.isFile) {

                frame.menuBar("File").sub("New").item("Edit").doClick

                frame.pagedPane.selection.page.content.asInstanceOf[PagedPane]
                  .selection.page.title = file.getName

                frame.pagedPane.selection.page.content.asInstanceOf[PagedPane]
                  .selection.page.content.asInstanceOf[EditPanel]
                  .currentFile = file

                frame.pagedPane.selection.page.content.asInstanceOf[PagedPane]
                  .selection.page.content.asInstanceOf[EditPanel]
                  .codePane.editor.setText(util.load(file).get)

              }
            }
          }
          }
      };

      peer.addMouseListener(listener)

    }

    val toolBar = new ToolBar(tree)
    toolBar.button("Refresh").doClick

    val explorer = new BorderPanel {

      add(toolBar, BorderPanel.Position.North)
      add(tree, BorderPanel.Position.Center)

    }

    val vSplitPane: SplitPane = new SplitPane(
      Orientation.Vertical,
      explorer,
      hSplitPane)

    val panel = new BorderPanel {

      add(frame.toolBar, BorderPanel.Position.North)
      add(vSplitPane, BorderPanel.Position.Center)

    }

    frame.contents = panel

    hSplitPane.peer.setDividerLocation(hSplitPane.size.height - 125)
    vSplitPane.peer.setDividerLocation(200)

    super.register

  }

  protected def frame = widebase.workspace.ide.app.plugin.frame

}

object Plugin {

  val category = "Core"
  val homepage = "http://widebase.github.com/"
  val id = classOf[Plugin].getPackage.getName
  val name = "Widebase IDE Explorer"

}

