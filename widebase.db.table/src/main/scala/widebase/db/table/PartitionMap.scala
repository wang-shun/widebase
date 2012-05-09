package widebase.db.table

import scala.collection.mutable.LinkedHashMap

/** A [[scala.collection.mutable.LinkedHashMap]] for pairs of string based partition domain and [[widebase.db.table.Table]].
 *
 * @author myst3r10n
 */
class PartitionMap extends LinkedHashMap[String, Table] {

  /** A serie of partition domains. */
  def parts = keys

  /** A serie of tables. */
  def tables = values

  /** A printable [[widebase.db.table.PartitionMap]]. */
  override def toString =
    if(tables.size <= 0)
      "Empty"
    else
      tables.head.toString

}

/** Companion of [[widebase.db.table.PartitionMap]].
 *
 * @author myst3r10n
 */
object PartitionMap {

  /** Creates [[widebase.db.table.PartitionMap]].
   *
   * @param pair of partition/table
   */
  def apply(pair: (String, Table)) = {

    val parts = new PartitionMap
    parts += pair._1 -> pair._2
    parts

  }
}

