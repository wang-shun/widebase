package widebase.workspace

import com.twitter.util.Eval

import de.sciss.scalainterpreter. { Interpreter, LogPane }

import java.awt. { Point, Toolkit }

import java.io. {

  BufferedReader,
  DataInputStream,
  File,
  FileInputStream,
  FileNotFoundException,
  InputStreamReader,
  Writer

}

import java.util.concurrent.ArrayBlockingQueue

import javax.swing. { ImageIcon, JOptionPane }

import net.liftweb.common. { Loggable, Logger }

import scala.collection.mutable.LinkedHashMap
import scala.swing. { BorderPanel, Dialog, Label, ProgressBar }

/** Runtime package.
 *
 * @author myst3r10n
 */
package object runtime extends Logger with Loggable {

  import scala.util.control.Breaks. { break, breakable }

  val plugin = LinkedHashMap[String, PluginLike]()

  val queue = new ArrayBlockingQueue[Option[String]](1024)

  val logPane = LogPane().makeDefault()
  val interpreterConfig = Interpreter.Config()

  interpreterConfig.out = Some(logPane.writer)

  val interpreter = Interpreter(interpreterConfig)

  object thread extends Thread {

    override def run {

      try {

        breakable {

          while(true) {

            val code = queue.take

            if(code == null)
              break

            code.foreach(interpret)

          }
        }
      } catch {

        case e: InterruptedException =>

      }
    }

    def interpret(code: String) = interpreter.interpret(code)

  }

  def launch(filename: String, image: ImageIcon = null) {

    val progress = new ProgressBar { max = 1 }

    val dialog = new Dialog {

      title = "Launch..."

      if(image == null)
        contents = progress
      else
        contents = new BorderPanel {

          add(new Label { icon = image }, BorderPanel.Position.Center)
          add(progress, BorderPanel.Position.South)

        }
    }

    try {

      val file = new File(filename)

      if(!file.exists)
        throw new FileNotFoundException(file.getPath)

      val eval = new Eval
      val config = eval[ConfigLike](file)

      config.plugin.foreach(plugin => progress.max += 1)

      dialog.pack
      dialog.centerOnScreen
      dialog.visible = true

      config.plugin.foreach { plugin =>

        if(!interpreter.interpret("(new " + plugin + ".Plugin).register")
          .isInstanceOf[Interpreter.Success]) {

          JOptionPane.showMessageDialog(
            null,
            logPane.component,
            "Plugin failed",
            JOptionPane.ERROR_MESSAGE)

          runtime.plugin.values.foreach(_.unregister)
          throw new Exception("Plugin failed: " + plugin)

        }

        progress.value += 1

      }

      thread.start

      runtime.plugin.values.foreach(_.startup)

    } finally {

      dialog.dispose

    }
  }

  def shutdown {

    runtime.plugin.values.foreach(_.unregister)
    thread.interrupt

  }
}

