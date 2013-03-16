package widebase.ui.toolkit

import com.twitter.util.Eval

import de.sciss.scalainterpreter. { Interpreter, LogPane }

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

import net.liftweb.common. { Loggable, Logger }

import scala.collection.mutable.Map

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

      return Some(code)

    } catch {

      case e: Exception => error("Open failed\n" + e.getMessage)
//        Dialog.showMessage(null, "Open failed\n" + e.getMessage)

    } finally {

      if(reader != null)
        reader.close

    }

    None

  }

  def launch(filename: String) {

    val file = new File(filename)

    if(!file.exists)
      throw new FileNotFoundException(file.getPath)

    val eval = new Eval
    val config = eval[ConfigLike](file)

    config.plugins.foreach { plugin =>

      if(!file.exists) {

        error("File not found: " + file.getPath)
        return

      }
    }

    val result = interpreter.interpret(config.app)

    if(result.isInstanceOf[Interpreter.Success])
      thread.start
    else
      error("Configure failed: " + file.getPath)

    config.plugins.foreach(plugin => queue.add(load(plugin)))

  }

  def shutdown {

    thread.interrupt

  }
}

