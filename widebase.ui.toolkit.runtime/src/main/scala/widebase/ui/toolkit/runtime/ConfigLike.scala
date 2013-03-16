package widebase.ui.toolkit.runtime

import java.io.File

trait ConfigLike {

  val app: String
  val plugins: Array[File]

}

