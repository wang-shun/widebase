package widebase.ui.workspace

import javax.swing. { JOptionPane, UIManager }

import moreswing.swing.i18n. { LAction, LMenu, LocaleManager, LRadioMenuItem }

import scala.swing. { ButtonGroup, RadioMenuItem }
import scala.swing.event.ButtonClicked

import widebase.ui.workspace.event.LookAndFeelChanged

/** A menu with look and feel items.
 *
 * @param title of sub menu
 *
 * @author myst3r10n
 */
class LookAndFeelMenu(title: String) extends Menu(title) with LMenu {

  protected var list = Array[UIManager.LookAndFeelInfo]()

  UIManager.getInstalledLookAndFeels.foreach { laf =>

    if(!list.exists(l => l.getClassName == laf.getClassName))
      list = list :+ laf

  }

  object group extends ButtonGroup {

    list.sortWith { (a, b) => a.getName < b.getName } foreach { laf =>

      LookAndFeelMenu.this += laf.getName -> new RadioMenuItem("") with LRadioMenuItem {

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

      group.this.buttons += radio(laf.getName)

    }
  }

  contents ++= group.buttons

}

