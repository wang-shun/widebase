package widebase.ui.ide

import event. { Exit, NewEdit, NewPreferenceSheet, NewWorksheet }

import java.awt. { Desktop, Toolkit }
import java.awt.event._
import java.net.URL
import java.util.Locale
import java.util.prefs.Preferences

import javax.swing. { ImageIcon, JOptionPane, KeyStroke, UIManager }

import moreswing.swing.i18n._
import moreswing.swing.plaf.RedesignManager

import scala.swing. {

  Action,
  ButtonGroup,
  Menu,
  MenuBar,
  MenuItem,
  RadioMenuItem,
  Separator

}

import scala.swing.event.ButtonClicked

/** Main frame's menu bar.
 * 
 * @author myst3r10n
 */
class AppMainMenuBar extends MenuBar {

  val prefs = Preferences.userRoot

  contents += new Menu("Workbook") with LMenu {
    contents += new Menu("New") with LMenu {
      contents += new MenuItem(new Action("Edit") with LAction {

        mnemonic = KeyEvent.VK_N
        accelerator = Some(
          KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK))
        tooltip = "New_Edit"

        def apply = { AppMainMenuBar.this.publish(NewEdit) }

      } ) { icon = new ImageIcon(getClass.getResource("/icon/tab-new.png")) }

      contents += new MenuItem(new Action("Sheet") with LAction {

        tooltip = "New_Sheet"
        def apply = { AppMainMenuBar.this.publish(NewWorksheet) }

      } ) { icon = new ImageIcon(getClass.getResource("/icon/window-new.png")) }
    }

    contents += new Separator

    contents += new MenuItem(new Action("Exit") with LAction {

      mnemonic = KeyEvent.VK_X
      accelerator = Some(
        KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK))
      tooltip = "Exit"

      def apply = { AppMainMenuBar.this.publish(Exit) }

    } ) { icon = new ImageIcon(getClass.getResource("/icon/application-exit.png")) }
  }

  contents += new Menu("Tools") with LMenu {
    contents += new Menu("Locale") with LMenu {

      val locales = Array[String](
        "en_US",
        "de_CH",
        "de_DE")

      contents ++= (new ButtonGroup {

        locales.sortWith { (a, b) => a < b } foreach(l =>
          buttons += new RadioMenuItem(l) with LRadioMenuItem {

            selected = l == LocaleManager.locale.toString

            reactions += {
              case ButtonClicked(_) =>
                if(selected && l != LocaleManager.locale.toString) {

                  prefs.put("app.locale", l)
                  Locale.setDefault(new Locale(l.split('_')(0), l.split('_')(1)))
                  LocaleManager.locale = new Locale(l.split('_')(0), l.split('_')(1))

                }
            }
          }
        )
      } ).buttons
    }

    contents += new Menu("Look_&_Feels") with LMenu {

      var lafs = Array[UIManager.LookAndFeelInfo]()

      UIManager.getInstalledLookAndFeels.foreach(laf =>
        if(!lafs.exists(l => l.getClassName == laf.getClassName))
          lafs = lafs :+ laf)

      contents ++= (new ButtonGroup {

        lafs.sortWith { (a, b) => a.getName < b.getName } foreach(laf =>
          buttons += new RadioMenuItem(laf.getName) {

            selected = laf.getClassName == UIManager.getLookAndFeel.getClass.getName

            reactions += {
              case ButtonClicked(_) =>

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
                  prefs.put("app.laf", laf.getClassName)

                object Break extends Exception

                try {

                  buttons.foreach(
                    button =>
                      if(previous.getName == button.text) {

                        button.selected = true
                        throw Break

                      }
                  )

                } catch { case Break => }
            }
          }
        )
      } ).buttons
    }

    contents += new Separator

    contents += new MenuItem(
      new Action("Preferences")
      with LAction {

      tooltip = "Open_Preferences"
      def apply = { AppMainMenuBar.this.publish(NewPreferenceSheet) }

    } ) { icon = new ImageIcon(getClass.getResource("/icon/preferences-system.png")) }
  }

  contents += new Menu("Help") with LMenu {

    contents += new MenuItem(
      new Action("Widebase Home")
      with LAction {

      tooltip = "Widebase_home"
      def apply = {

        if(Desktop.isDesktopSupported)
          Desktop.getDesktop.browse(new URL("http://widebase.github.com/").toURI)
        else
          JOptionPane.showMessageDialog(
            null,
            "Class java.awt.Desktop.browse not supported",
            "Environment",
            JOptionPane.ERROR_MESSAGE)

      }
    } ) { icon = new ImageIcon(getClass.getResource("/icon/widebase-16x16.png")) }

    contents += new MenuItem(
      new Action("Widebase Handbook")
      with LAction {

      tooltip = "Widebase_handbook"
      def apply = {

        if(Desktop.isDesktopSupported)
          Desktop.getDesktop.browse(new URL(
            "http://widebase.github.com/docs/books/widebase-handbook/html/index.html").toURI)
        else
          JOptionPane.showMessageDialog(
            null,
            "Class java.awt.Desktop.browse not supported",
            "Environment",
            JOptionPane.ERROR_MESSAGE)

        }
    } ) { icon = new ImageIcon(getClass.getResource("/icon/help-contents.png")) }

    contents += new Separator

    contents += new MenuItem(
      new Action("About...")
      with LAction {

      tooltip = "Widebase_handbook"
      def apply = {

        JOptionPane.showMessageDialog(
          null,
          "Widebase IDE",
          "About...",
          JOptionPane.INFORMATION_MESSAGE)

      }
    } ) { icon = new ImageIcon(getClass.getResource("/icon/help-about.png")) }
  }
}

