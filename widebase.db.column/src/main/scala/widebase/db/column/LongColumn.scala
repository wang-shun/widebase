package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[scala.Long]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class LongColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Long](Datatype.Long) {

  import widebase.data

  protected val sizeOf = data.sizeOf.long

  protected def read(region: Int) = mappers(region).readLong
  protected def write(region: Int, value: Long) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.LongColumn]].
 *
 * @author myst3r10n
 */
object LongColumn {

  /** Creates [[widebase.db.column.LongColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Long*) = new LongColumn ++= values

}

