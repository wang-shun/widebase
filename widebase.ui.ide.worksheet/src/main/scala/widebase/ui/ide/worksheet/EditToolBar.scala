package widebase.ui.ide.worksheet

import java.util.UUID

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.collection.mutable.LinkedHashMap
import scala.swing. { Button, Publisher, Separator }
import scala.swing.event. { ButtonClicked, Event }

import widebase.ui.toolkit.ToolBarLike
import widebase.ui.toolkit.event.EventForwarding

/** Tool bar of frame.
 *
 * @author myst3r10n
 */
class EditToolBar extends ToolBarLike {

  import widebase.ui.ide.event._

  peer.setFloatable(false)

  protected val items = LinkedHashMap[String, Any](
    "FileOpen" -> new Button with EventForwarding {

      val publishEvent = FileOpen
      icon = new ImageIcon(getClass.getResource("/icon/document-open.png"))
      tooltip = LocaleManager.text("File_open")

    },
    "FileSave" -> new Button with EventForwarding {

      val publishEvent = FileSave
      icon = new ImageIcon(getClass.getResource("/icon/document-save.png"))
      tooltip = LocaleManager.text("File_save")

    },
    "FileSaveAs" -> new Button with EventForwarding {

      val publishEvent = FileSaveAs
      icon = new ImageIcon(getClass.getResource("/icon/document-save-as.png"))
      tooltip = LocaleManager.text("File_save_as")

    },
    UUID.randomUUID.toString -> new Separator,
    "EditCut" -> new Button with EventForwarding {

      val publishEvent = EditCut
      icon = new ImageIcon(getClass.getResource("/icon/edit-cut.png"))
      tooltip = LocaleManager.text("File_save_as")

    },
    "EditCopy" -> new Button with EventForwarding {

      val publishEvent = EditCopy
      icon = new ImageIcon(getClass.getResource("/icon/edit-copy.png"))
      tooltip = LocaleManager.text("Edit_copy")

    },
    "EditPaste" -> new Button with EventForwarding {

      val publishEvent = EditPaste
      icon = new ImageIcon(getClass.getResource("/icon/edit-paste.png"))
      tooltip = LocaleManager.text("Edit_paste")

    },
    UUID.randomUUID.toString -> new Separator,
    "InterpretContent" -> new Button with EventForwarding {

      val publishEvent = InterpretContent
      icon = new ImageIcon(getClass.getResource("/icon/player_play.png"))
      tooltip = LocaleManager.text("Interpret_content")

    },
    "InterpretSelection" -> new Button with EventForwarding {

      val publishEvent = InterpretSelection
      icon = new ImageIcon(getClass.getResource("/icon/player_playselection.png"))
      tooltip = LocaleManager.text("Interpret_selection")

    }
  )

  setup

  def buttons(name: String) = items(name).isInstanceOf[Button]

}

