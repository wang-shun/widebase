import java.io.File

import widebase.workspace.runtime.ConfigLike

new ConfigLike {

  val app = "val app = new widebase.workspace.ide.App"

  val plugins = Array[File](
    new File("plugin/widebase.workspace.ide.cli/Launch.scala"),
    new File("plugin/widebase.workspace.ide.table/Launch.scala"),
    new File("plugin/widebase.workspace.ide.chart/Launch.scala"),
    new File("plugin/widebase.workspace.ide.editor/Launch.scala"))

}

