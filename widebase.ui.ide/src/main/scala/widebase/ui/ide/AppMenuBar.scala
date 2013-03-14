package widebase.ui.ide

import java.awt.event. { InputEvent, KeyEvent }

import javax.swing. { ImageIcon, KeyStroke }

import moreswing.swing.i18n. { LAction, LMenu }

import scala.swing. { Action, Menu, MenuBar, MenuItem, Separator }

import widebase.ui.toolkit.event. { LocaleChanged, LookAndFeelChanged }
import widebase.ui.toolkit.menu. { LocaleMenu, LookAndFeelMenu }

/** Main frame's menu bar.
 * 
 * @author myst3r10n
 */
class AppMenuBar extends MenuBar {

  import event._

  object Workbook extends Menu("Workbook") with LMenu {

    object New extends Menu("New") with LMenu {

      object Edit extends MenuItem("") {

        action = new Action("Edit") with LAction {

          mnemonic = KeyEvent.VK_N
          accelerator = Some(KeyStroke.getKeyStroke(
            KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK))
          tooltip = "New_Edit"

          def apply = { AppMenuBar.this.publish(NewEdit) }

        }

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }

      object Sheet extends MenuItem("") {

        action = new Action("Sheet") with LAction {

          tooltip = "New_Sheet"
          def apply = { AppMenuBar.this.publish(NewWorksheet) }

        }

        icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))

      }

      contents += Edit += Sheet

    }

    object Exit extends MenuItem("") {

      action = new Action("Exit") with LAction {

        mnemonic = KeyEvent.VK_X
        accelerator = Some(
          KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK))
        tooltip = "Exit"
        def apply = { AppMenuBar.this.publish(widebase.ui.ide.event.Exit) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/application-exit.png"))

    }

    contents += New += new Separator += Exit

  }

  object Tools extends Menu("Tools") with LMenu {

    object Locale extends LocaleMenu {

      listenTo(this)
      reactions += { case LocaleChanged(replaced) =>

        AppMenuBar.this.publish(LocaleChanged(replaced))

      }
    }

    object LookAndFeel extends LookAndFeelMenu {

      listenTo(this)
      reactions += { case LookAndFeelChanged(replaced) =>

        AppMenuBar.this.publish(LookAndFeelChanged(replaced))

      }
    }

    object Preferences extends MenuItem("") {

      action = new Action("Preferences") with LAction {

        tooltip = "Open_Preferences"
        def apply = { AppMenuBar.this.publish(NewPreferenceSheet) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/preferences-system.png"))

    }

    contents += Locale += LookAndFeel += new Separator += Preferences

  }

  object Help extends Menu("Help") with LMenu {

    object WidebaseHome extends MenuItem("") {

      action = new Action("Widebase Home") with LAction {

        tooltip = "Widebase_home"
        def apply = { AppMenuBar.this.publish(widebase.ui.ide.event.WidebaseHome) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/widebase-16x16.png"))

    }

    object WidebaseHandbook extends MenuItem("") {

      action = new Action("Widebase Handbook") with LAction {

        tooltip = "Widebase_handbook"
        def apply = { AppMenuBar.this.publish(widebase.ui.ide.event.WidebaseHandbook) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/help-contents.png"))

    }

    object About extends MenuItem("") {

      action = new Action("About...") with LAction {

        tooltip = "Widebase_handbook"
        def apply = { AppMenuBar.this.publish(widebase.ui.ide.event.WidebaseAbout) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/help-about.png"))

    }

    contents += WidebaseHome += WidebaseHandbook += new Separator += About

  }

  contents += Workbook += Tools += Help

}

