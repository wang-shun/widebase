package widebase.workspace.ide.cli

import java.io.File

import widebase.workspace.FrameLike
import widebase.workspace.runtime.PluginLike

class Plugin(frame: FrameLike) extends PluginLike {

  import widebase.workspace.runtime
  import widebase.workspace.util

  val category = Plugin.category
  val homepage = Plugin.homepage
  val id = Plugin.id
  val name = Plugin.name

  override def option = None

  override def register {

    runtime.queue.add(util.load(new File(System.getProperty("user.dir") + "/sbin/Cli.scala")))
    runtime.queue.add(util.load(new File(System.getProperty("user.dir") + "/sbin/Dbi.scala")))

    super.register

  }
}

object Plugin {

  val category = "Core"
  val homepage = "http://widebase.github.com/"
  val id = classOf[Plugin].getPackage.getName
  val name = "Widebase IDE CLI"

}

