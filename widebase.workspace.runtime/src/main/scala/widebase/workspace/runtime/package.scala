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

import javax.swing.ImageIcon

import net.liftweb.common. { Loggable, Logger }

import scala.collection.mutable.Map
import scala.swing. { BorderPanel, Dialog, Label, ProgressBar }

/** Runtime package.
 *
 * @author myst3r10n
 */
package object runtime extends Logger with Loggable {

  import scala.util.control.Breaks. { break, breakable }

  val app = Map[String, AppLike]()
  val plugin = Map[String, PluginLike]()

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

  def load(file: File): Option[String] = {

    var reader: BufferedReader = null

    try {

      reader = new BufferedReader(new InputStreamReader(
        new DataInputStream(new FileInputStream(file))))

      var code = ""
      var line = reader.readLine

      while(line != null) {

        if(!code.isEmpty)
          code += System.getProperty("line.separator")
        code += line
        line = reader.readLine

      }

      Some(code)

    } finally {

      if(reader != null)
        reader.close

    }
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

      config.plugins.foreach { plugin =>

        if(!plugin.exists)
          throw new FileNotFoundException(plugin.getPath)

        progress.max += 1

      }

      dialog.pack
      dialog.centerOnScreen
      dialog.visible = true

      if(!interpreter.interpret(config.app).isInstanceOf[Interpreter.Success])
        throw new Exception("App failed: " + file.getPath)

      progress.value = 1

      config.plugins.foreach { plugin =>

        if(!interpreter.interpret(load(plugin).get).isInstanceOf[Interpreter.Success]) {

          runtime.plugin.values.foreach(_.unregister)
          app.values.foreach(_.frame.dispose)
          throw new Exception("Plugin failed: " + plugin.getPath)

        }

        progress.value += 1

      }

      if(!interpreter.interpret("app.frame.visible = true").isInstanceOf[Interpreter.Success]) {

        runtime.plugin.values.foreach(_.unregister)
        app.values.foreach(_.frame.dispose)
        throw new Exception("Frame failed: visible")

      }

      thread.start

    } finally {

      dialog.dispose

    }
  }

  def shutdown {

    thread.interrupt

  }
}

