package widebase.ui.ide

import de.sciss.scalainterpreter. { CodePane, Interpreter, SplitPane, Style }

import java.awt.Toolkit
import java.io.File
import java.util.Locale
import java.util.prefs.Preferences

import javax.swing. { JDialog, JFrame, JOptionPane, UIManager }
import javax.swing.UIManager.LookAndFeelInfo

import moreswing.swing.i18n.LocaleManager
import moreswing.swing.plaf.RedesignManager

import net.liftweb.common. { Loggable, Logger }

import scala.swing. { Dimension, Swing }

import widebase.ui.ide.worksheet.EditPanel

/** Application.
 * 
 * @author myst3r10n
 */
object App extends Logger with Loggable {

  val prefs = Preferences.userRoot

  def main(args: Array[String]) {

    var laf = ""
    var locale = ""

    // Treat locale arguments.
    if(locale.isEmpty)
      locale = prefs.get("app.locale", "en_US")
    else
      prefs.put("app.locale", locale)

    Locale.setDefault(new Locale(locale.split('_')(0), locale.split('_')(1)))
    LocaleManager.locale = new Locale(locale.split('_')(0), locale.split('_')(1))

    // Treat laf arguments.
    val lafs3rd = Array[UIManager.LookAndFeelInfo](
      new UIManager.LookAndFeelInfo(
        "EaSynth",
        "com.easynth.lookandfeel.EaSynthLookAndFeel"),
      new UIManager.LookAndFeelInfo(
        "Napkin",
        "net.sourceforge.napkinlaf.NapkinLookAndFeel"),
      new UIManager.LookAndFeelInfo(
        "NimROD",
        "com.nilo.plaf.nimrod.NimRODLookAndFeel"),
      new UIManager.LookAndFeelInfo(
        "Quaqua",
        "ch.randelshofer.quaqua.QuaquaLookAndFeel"),
      new UIManager.LookAndFeelInfo(
        "Tiny",
        "de.muntjak.tinylookandfeel.TinyLookAndFeel")
    )

    lafs3rd.foreach(laf3rd =>

      if(!UIManager.getInstalledLookAndFeels.exists(laf =>
        laf.getClassName == laf3rd.getClassName))
        UIManager.installLookAndFeel(laf3rd))

    if(laf.isEmpty) {
      laf = prefs.get("app.laf", "")

      if(laf.isEmpty) {
        laf = "javax.swing.plaf.metal.MetalLookAndFeel"
        prefs.put("app.laf", laf)
      }
    } else
      prefs.put("app.laf", laf)

    val found = UIManager.getInstalledLookAndFeels.find(is =>
      is.getClassName == laf).orNull

    if(found == null) {

      error("Pluggable look and feel missed: " + laf)
      sys.exit(1)

    }

    found.getClassName match {
      case "de.muntjak.tinylookandfeel.TinyLookAndFeel" =>

        Toolkit.getDefaultToolkit().setDynamicLayout(true)
        System.setProperty("sun.awt.noerasebackground", "true")
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)

      case _ =>
    }

    try {

      RedesignManager.laf = found

    } catch {

      case e: ClassNotFoundException =>

        val default = new LookAndFeelInfo(
          "MetalLookAndFeel",
          "javax.swing.plaf.metal.MetalLookAndFeel")

        RedesignManager.laf = default

        prefs.put("app.laf", default.getClassName)

        JOptionPane.showMessageDialog(
          null,
          LocaleManager.text("Not_found_?", found.getClassName),
          LocaleManager.text("Look_&_Feel"),
          JOptionPane.ERROR_MESSAGE)

    }

    // Main GUI liftoff!
    Swing.onEDT {

      val frame = new AppMainFrame {

        iconImage = Toolkit.getDefaultToolkit.getImage(
          getClass.getResource("/icon/widebase-16x16.png"))

        override def closeOperation {

          shutdown
          super.closeOperation

        }

        size = new Dimension(0, 0)
        pack
        splitPane.setDividerLocation(splitPane.getHeight - 125)
        visible = true

      }

      EditPanel.intpCfg.out = Some(frame.log.writer)
      EditPanel.actor.start

      val init = new File(System.getProperty("user.dir") + "/" + "sbin/Init.scala")

      if(init.exists)
        EditPanel.actor ! Some(EditPanel.load(init))

    }
  }

  def shutdown {

    EditPanel.actor ! EditPanel.Abort

  }
}

