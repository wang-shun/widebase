package widebase.workspace.ide.app

import java.awt.Desktop
import java.awt.event. { InputEvent, KeyEvent }
import java.net.URL
import java.util.UUID

import javax.swing. { ImageIcon, JOptionPane, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing.Separator

import widebase.workspace. {

  Action,
  LocaleMenu,
  LookAndFeelMenu,
  Menu,
  MenuItem,
  PreferenceManager

}

/** Menu bar of app frame.
 * 
 * @author myst3r10n
 */
class MenuBar(frame: Frame) extends widebase.workspace.MenuBar {

  import widebase.workspace.runtime

  this += "File" -> new Menu("File")
  this("File") += "New" -> new Menu("New")

  this("File").sub("New") += "Page" -> new MenuItem(
    new Action("Page") {

      def apply {

        frame.pagedPane.add

      }

      icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))

    }
  )

  this("File") += UUID.randomUUID.toString -> new Separator

  this("File") += "Exit" -> new MenuItem(
    new Action("Exit") {

      icon = new ImageIcon(getClass.getResource("/icon/application-exit.png"))
      mnemonic = KeyEvent.VK_X
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK))
      toolTip = "Exit"

      def apply {

        frame.dispose
        runtime.shutdown

      }
    }
  )

  this += "Tools" -> new Menu("Tools")

  this("Tools") += "Locale" -> new LocaleMenu("Locale") {

    frame.listenTo(this)

  }

  this("Tools") += "LookAndFeel" -> new LookAndFeelMenu("Look_&_Feels") {

    frame.listenTo(this)

  }

  this("Tools") += UUID.randomUUID.toString -> new Separator

  this("Tools") += "Preferences" -> new MenuItem(
    new Action("Preferences") {

      icon = new ImageIcon(getClass.getResource("/icon/preferences-system.png"))

      def apply {

        object Break extends Throwable

        try {

          frame.pagedPane.pages.foreach { page =>

            if(page.content.isInstanceOf[PreferenceManager]) {

              frame.pagedPane.selection.page = page
              throw Break

            }
          }

          frame.pagedPane.pages += new TabbedDesktopPane.Page(
            LocaleManager.text("Preferences"),
            new ImageIcon(getClass.getResource("/icon/preferences-system.png")),
            new PreferenceManager)

        } catch { case Break => }
      }
    }
  )

  this += "Help" -> new Menu("Help")

  this("Help") += "WidebaseHome" -> new MenuItem(
    new Action("Widebase Home") {

      icon = new ImageIcon(getClass.getResource("/icon/widebase-16x16.png"))
      toolTip = "Widebase_home"

      def apply {

        if(Desktop.isDesktopSupported)
          Desktop.getDesktop.browse(new URL("http://widebase.github.com/").toURI)
        else
          JOptionPane.showMessageDialog(
            frame.peer,
            "Class java.awt.Desktop.browse not supported",
            "Environment",
            JOptionPane.ERROR_MESSAGE)

      }
    }
  )

  this("Help") += "WidebaseHandbook" -> new MenuItem(
    new Action("Widebase Handbook") {

      icon = new ImageIcon(getClass.getResource("/icon/help-contents.png"))
      toolTip = "Widebase_handbook"

      def apply {

        if(Desktop.isDesktopSupported)
          Desktop.getDesktop.browse(new URL(
            "http://widebase.github.com/docs/books/widebase-handbook/html/index.html").toURI)
        else
          JOptionPane.showMessageDialog(
            frame.peer,
            "Class java.awt.Desktop.browse not supported",
            "Environment",
            JOptionPane.ERROR_MESSAGE)

      }
    }
  )

  this("Help") += UUID.randomUUID.toString -> new Separator

  this("Help") += "About" -> new MenuItem(
    new Action("About...") {

      icon = new ImageIcon(getClass.getResource("/icon/help-about.png"))
      toolTip = "About..."

      def apply {

        JOptionPane.showMessageDialog(
          frame.peer,
          "Widebase IDE",
          "About...",
          JOptionPane.INFORMATION_MESSAGE,
          new ImageIcon(getClass.getResource("/icon/widebase-logo-32x32.png")))

      }
    }
  )
}

