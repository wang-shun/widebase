package widebase.db.table

import scala.collection.mutable.LinkedHashMap

class PartitionMap extends LinkedHashMap[String, Table] {

  def parts = keys
  def tables = values

  override def toString =
    if(tables.size <= 0)
      "Empty"
    else
      tables.head.toString

}

object PartitionMap {

  def apply(elems: (String, Table)) = {

    val parts = new PartitionMap
    parts += elems._1 -> elems._2
    parts

  }
}

