package widebase.workspace.ide.app

import java.awt.Toolkit

import widebase.workspace.runtime.PluginLike

class Plugin extends PluginLike {

  import widebase.workspace. { runtime, util }

  protected var frame0: Frame = null

  val category = Plugin.category
  val homepage = Plugin.homepage
  val id = Plugin.id
  val name = Plugin.name

  def frame = frame0

  override def option = None

  override def register {

    frame0 = new Frame {

      iconImage = Toolkit.getDefaultToolkit.getImage(
        getClass.getResource("/icon/widebase-16x16.png"))

      pack
      splitPane.peer.setDividerLocation(splitPane.size.height - 125)

    }

    super.register

  }

  override def startup {

    frame.visible = true

    super.startup

  }

  override def unregister {

    frame.dispose
    super.unregister

  }
}

object Plugin {

  val category = "Core"
  val homepage = "http://widebase.github.com/"
  val id = classOf[Plugin].getPackage.getName
  val name = "Widebase IDE App"

}

