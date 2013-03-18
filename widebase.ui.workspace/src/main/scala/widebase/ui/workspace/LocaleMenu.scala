package widebase.ui.workspace

import moreswing.swing.i18n. { LocaleManager, LRadioMenuItem }

import scala.swing. { ButtonGroup, RadioMenuItem }

import widebase.ui.workspace.event.LocaleChanged

/* A menu with locale items.
 *
 * @param title of menu
 *
 * @author myst3r10n
 */
class LocaleMenu(title: String) extends Menu(title) {

  protected val list = Array(
    "en_US",
    "de_CH",
    "de_DE")

  object group extends ButtonGroup {

    list.toArray.sortWith { (a, b) => a < b } foreach { localeId =>

      LocaleMenu.this += localeId -> new RadioMenuItem("") with LRadioMenuItem {

        action = new Action(localeId) {

          selected = localeId == LocaleManager.locale.toString

          override def apply {

            if(selected && localeId != LocaleManager.locale.toString) {

              java.util.Locale.setDefault(new java.util.Locale(
                localeId.split('_')(0),
                localeId.split('_')(1)))

              LocaleManager.locale = new java.util.Locale(
                localeId.split('_')(0),
                localeId.split('_')(1))

              LocaleMenu.this.publish(LocaleChanged(localeId))

            }
          }
        }
      }

      group.this.buttons += radio(localeId)

    }
  }

  contents ++= group.buttons

}
