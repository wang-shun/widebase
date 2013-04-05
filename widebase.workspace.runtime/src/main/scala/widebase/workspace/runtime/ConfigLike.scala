package widebase.workspace.runtime

import java.io.File

trait ConfigLike {

  val app: String
  val plugins: Array[String]

}

