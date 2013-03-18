package widebase.ui.ide.editor

import de.sciss.scalainterpreter.CodePane

import java.io.File

import scala.swing. { BorderPanel, Component, Publisher }

/** Panel of edit.
 * 
 * @author myst3r10n
 */
class EditPanel extends BorderPanel with Publisher {

  var currentFile: File = null

  val toolBar = new ToolBar(this)
  val codeCfg = CodePane.Config()

  val codePane = CodePane(codeCfg)

  add(toolBar, BorderPanel.Position.North)

  add(
    new Component { override lazy val peer = codePane.component },
    BorderPanel.Position.Center)


  listenTo(this, toolBar)

}

