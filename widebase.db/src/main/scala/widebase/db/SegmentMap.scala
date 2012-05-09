package widebase.db

import java.io.File

import scala.collection.mutable.LinkedHashMap

/** A [[scala.collection.mutable.LinkedHashMap]] for pairs of segment key and segment path.
 *
 * @author myst3r10n
 */
class SegmentMap extends LinkedHashMap[String, File] {

  /** A serie of segment paths. */
  def paths = values

}

/** Companion of [[widebase.db.table.SegmentMap]].
 *
 * @author myst3r10n
 */
object SegmentMap {

  /** Creates [[widebase.db.table.PartitionMap]].
   *
   * @param pair of partition/table
   */
  def apply(pair: (String, File)) = {

    val segments = new SegmentMap
    segments += pair._1 -> pair._2
    segments

  }
}

