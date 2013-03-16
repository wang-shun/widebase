package widebase.ui.toolkit

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
class PreferenceManager extends ViewPane {

  import TabbedPane.Layout

  import widebase.ui.toolkit.runtime

  // Load from config.
  private val prefs = Preferences.userNodeForPackage(getClass)

  override val popupMenu = new ViewMenu(this)

  protected var pluginData = Array[Array[Any]]()

  runtime.plugin.values.foreach { plugin =>

    pluginData = pluginData ++
      Array[Array[Any]](Array(plugin.label, plugin.scope))

  }

  pages += new TabbedDesktopPane.Page(
    LocaleManager.text("Plugins"),
    new ImageIcon(getClass.getResource("/icon/configure.png")),
    new ScrollPane { contents = new Table(pluginData, Seq("Label", "Scope")) } )

  runtime.plugin.values.foreach { plugin =>

    if(plugin.option != None)
      pages += plugin.option.get

  }

  if(pages.length > 0)
    selection.index = 0

  def restore {

    restore("preferenceManager")

  }
}

