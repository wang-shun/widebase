package widebase.ui.ide.editor

import event.EditSelection

import de.sciss.scalainterpreter.CodePane

import java.awt.event. { InputEvent, KeyEvent }
import java.io.File

import javax.swing.KeyStroke

import scala.swing. { BorderPanel, Component, Publisher }

//import widebase.ui.ide.event._

/** Pane of edit.
 * 
 * @author myst3r10n
 */
class EditPanel extends BorderPanel with Publisher {

  var currentFile: File = null

  val toolBar = new ToolBar(this)
  val codeCfg = CodePane.Config()

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_O,
    InputEvent.CTRL_MASK) -> { () =>

    toolBar.button("Open").action.apply

  }

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_S,
    InputEvent.CTRL_MASK) -> { () =>

    toolBar.button("Save").action.apply

  }

  for(i <- 0 to 8)
    codeCfg.keyMap += KeyStroke.getKeyStroke(
      KeyEvent.VK_1 + i,
      InputEvent.ALT_MASK) -> { () =>

      
      EditPanel.this.publish(EditSelection(i))

    }

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_0,
    InputEvent.ALT_MASK) -> { () =>

    EditPanel.this.publish(EditSelection(9))

  }

  codeCfg.keyMap += KeyStroke.getKeyStroke("F9") -> { () =>

    toolBar.button("Content").action.apply

  }

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_ENTER,
    InputEvent.SHIFT_MASK) -> { () =>

    toolBar.button("Selection").action.apply

  }

  val codePane = CodePane(codeCfg)

  add(toolBar, BorderPanel.Position.North)

  add(
    new Component { override lazy val peer = codePane.component },
    BorderPanel.Position.Center)


  listenTo(this, toolBar)

}

