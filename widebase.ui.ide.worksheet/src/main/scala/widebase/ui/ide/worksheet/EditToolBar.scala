package widebase.ui.ide.worksheet

import java.awt.Component

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Publisher }
import scala.swing.event.ButtonClicked

import widebase.ui.ide.event._

/** Tool bar of frame.
 *
 * @param name of tool bar
 * @param orientation of tool bar
 *
 * @author myst3r10n
 */
class EditToolBar(
  name: String,
  orientation: Int)
  extends JToolBar(name, orientation) with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(orientation: Int) = this("", orientation)
  def this(name: String) = this(name, SwingConstants.HORIZONTAL)

  setFloatable(false)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/document-open.png"))
    tooltip = LocaleManager.text("File_open")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(FileOpen)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/document-save.png"))
    tooltip = LocaleManager.text("File_save")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(FileSave)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/document-save-as.png"))
    tooltip = LocaleManager.text("File_save_as")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(FileSaveAs)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/edit-cut.png"))
    tooltip = LocaleManager.text("Edit_cut")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(EditCut)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/edit-copy.png"))
    tooltip = LocaleManager.text("Edit_copy")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(EditCopy)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/edit-paste.png"))
    tooltip = LocaleManager.text("Edit_paste")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(EditPaste)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/player_play.png"))
    tooltip = LocaleManager.text("Interpret_content")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(InterpretContent)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/player_playselection.png"))
    tooltip = LocaleManager.text("Interpret_selection")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => EditToolBar.this.publish(InterpretSelection)

    }
  } ).peer)
}

