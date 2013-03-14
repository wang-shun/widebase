package widebase.ui.toolkit.menu

import moreswing.swing.i18n. { LAction, LMenu, LocaleManager, LRadioMenuItem }

import scala.collection.mutable.Map
import scala.swing. { Action, ButtonGroup, Menu, RadioMenuItem }
import scala.swing.event.ButtonClicked

import widebase.ui.toolkit.event.LocaleChanged

class LocaleMenu extends Menu("Locale") with LMenu {

  val items = Map[String, RadioMenuItem](
    "en_US" -> null,
    "de_CH" -> null,
    "de_DE" -> null)

  object group extends ButtonGroup {

    items.keys.toArray.sortWith { (a, b) => a < b } foreach { localeId =>

      items(localeId) = new RadioMenuItem("") with LRadioMenuItem {

        action = new Action(localeId) with LAction {

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

      group.this.buttons += items(localeId)

    }
  }

  contents ++= group.buttons

}

