package widebase.ui.ide

import event. { NewEdit, NewWorksheet }

import javax.swing.ImageIcon

import moreswing.swing.i18n.LocaleManager

import scala.collection.mutable.LinkedHashMap
import scala.swing.Button

import widebase.ui.toolkit.ToolBarLike
import widebase.ui.toolkit.event.EventForwarding

/** Main frame's tool bar.
 * 
 * @author myst3r10n
 */
class AppToolBar extends ToolBarLike {

  protected val items = LinkedHashMap[String, Any](
    "NewEdit" -> new Button with EventForwarding {

      val publishEvent = NewEdit
      icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))
      tooltip = LocaleManager.text("New_edit")

    },
    "NewWorksheet" -> new Button with EventForwarding {

      val publishEvent = NewWorksheet
      icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))
      tooltip = LocaleManager.text("New_worksheet")

    }
  )

  setup

  def buttons(name: String) = items(name).isInstanceOf[Button]

}

