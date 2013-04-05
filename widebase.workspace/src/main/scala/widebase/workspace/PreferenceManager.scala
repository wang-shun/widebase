package widebase.workspace

import java.awt.event. { ItemEvent, ItemListener }

import java.util.prefs.Preferences

import javax.swing.ImageIcon

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n. { LCheckMenuItem, LocaleManager }
import moreswing.swing.plaf.Redesign

import scala.swing. {

  Alignment,
  BorderPanel,
  CheckMenuItem,
  Label,
  ScrollPane,
  Separator,
  Table,
  TabbedPane

}

import scala.swing.event. { ButtonClicked, MouseMoved }

/** A central place of preference tabs.
 *
 * @author myst3r10n
 */
class PreferenceManager extends PagedPane {

  import TabbedPane.Layout

  import widebase.workspace.runtime

  override val popupMenu = new PageMenu(this)

  protected var pluginData = Array[Array[Any]]()

  runtime.plugin.values.foreach { plugin =>

    pluginData =
      Array[Array[Any]](Array(
        plugin.id,
        plugin.name,
        plugin.category,
        plugin.homepage)) ++ pluginData

  }

  pages += new TabbedDesktopPane.Page(
    LocaleManager.text("Plugins"),
    new ImageIcon(getClass.getResource("/icon/preferences-plugin.png")),
    new ScrollPane {

      contents = new Table(
        pluginData,
        Seq(
          "ID",
          "Name",
          "Category",
          "Homepage"))
      
    }
  )

  runtime.plugin.values.foreach { plugin =>

    if(plugin.option != None)
      pages += plugin.option.get

  }

  if(pages.length > 0)
    selection.index = 0

  override def backup = "PreferenceManager"

}

