import widebase.workspace.runtime.ConfigLike

new ConfigLike {

  val app = "val app = new widebase.workspace.ide.App"

  val plugins = Array[String](
    "widebase.workspace.ide.cli",
    "widebase.workspace.ide.explorer",
    "widebase.workspace.ide.table",
    "widebase.workspace.ide.chart",
    "widebase.workspace.ide.editor")

}

