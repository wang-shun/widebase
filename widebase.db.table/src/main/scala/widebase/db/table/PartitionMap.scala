package widebase.db.table

import scala.collection.mutable.LinkedHashMap

class PartitionMap extends LinkedHashMap[String, Table] {

  def parts = keys
  def tables = values

}

object PartitionMap {

  def apply(elems: (String, Table)) = {

    val parts = new PartitionMap
    parts += elems._1 -> elems._2
    parts

  }
}

