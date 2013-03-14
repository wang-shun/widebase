package widebase.ui.toolkit.menu

import javax.swing. { JOptionPane, UIManager }

import moreswing.swing.i18n. { LAction, LMenu, LocaleManager, LRadioMenuItem }

import scala.collection.mutable.Map
import scala.swing. { Action, ButtonGroup, Menu, RadioMenuItem }
import scala.swing.event.ButtonClicked

import widebase.ui.toolkit.event.LookAndFeelChanged

class LookAndFeelMenu extends Menu("Look_&_Feels") with LMenu {

  val items = Map[UIManager.LookAndFeelInfo, RadioMenuItem]()

  UIManager.getInstalledLookAndFeels.foreach { laf =>

    if(!items.keys.exists(l => l.getClassName == laf.getClassName))
      items += laf -> null

  }

  object group extends ButtonGroup {

    items.keys.toArray.sortWith { (a, b) => a.getName < b.getName } foreach { laf =>

      items(laf) = new RadioMenuItem("") with LRadioMenuItem {

        selected = laf.getClassName == UIManager.getLookAndFeel.getClass.getName

        action = new Action(laf.getName) with LAction {

          override def apply {

            val previous =
              UIManager.getInstalledLookAndFeels.find(is =>
                is.getClassName == UIManager.getLookAndFeel.getClass.getName).orNull

            if(JOptionPane.showConfirmDialog(
              null,
              LocaleManager.text(
                "Restart_required_for_change,_apply?"),
                LocaleManager.text("Look_&_Feel") + ": " + laf.getName,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
              LookAndFeelMenu.this.publish(LookAndFeelChanged(laf))

            object Break extends Exception

            try {

              buttons.foreach { button =>

                if(previous.getName == button.text) {

                  button.selected = true
                  throw Break

                }
              }
            } catch {

              case Break =>

            }

          }
        }
      }

      group.this.buttons += items(laf)

    }
  }

  contents ++= group.buttons

}

