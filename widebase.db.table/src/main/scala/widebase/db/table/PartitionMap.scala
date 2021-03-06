package widebase.db.table

import java.util.concurrent.CountDownLatch

import scala.actors.Futures.future
import scala.collection.mutable. { Buffer, LinkedHashMap }

/** A [[scala.collection.mutable.LinkedHashMap]] for pairs of string based partition domain and [[widebase.db.table.Table]].
 *
 * @author myst3r10n
 */
class PartitionMap {

  protected val map = LinkedHashMap[String, Table]()

  /** Filters all tables of this partition which satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered table
   */
  def filter(predicate: Record => Boolean) = {

    val partialFiltered: Buffer[Table] =
      (for(i <- 0 to tables.size - 1)
        yield(null)).toBuffer

    var i = 0
    val pending = new CountDownLatch(tables.size)

    tables.foreach { table =>

      val index = Int.box(i)

      future {

        try{

          partialFiltered(index) = table.filter(predicate)

        } finally {

          pending.countDown

        }
      }

      i += 1

    }

    pending.await

    val table = partialFiltered.head
    partialFiltered.drop(1).foreach(table ++= _)

    table

  }

  /** Filters all tables of this partition which do not satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered table
   */
  def filterNot(predicate: Record => Boolean) = filter(!predicate(_))

  /** Applies a function to all tables of this partition.
   *
   * @param function apply to all tables
   */
  def foreach[U](function: ((String, Table)) =>  U) = map.foreach(function)

  /** A serie of partition domains. */
  def parts = map.keys

  def +=(pair: (String, Table)) = map += pair._1 -> pair._2

  /** A serie of tables. */
  def tables = map.values

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

