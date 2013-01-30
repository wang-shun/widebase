package widebase.db.column

import org.joda.time.Minutes

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[org.joda.time.Minutes]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class MinuteColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Minutes](Datatype.Minute) {

  import widebase.data

  protected val sizeOf = data.sizeOf.minute

  protected def read(region: Int) = mappers(region).readMinute
  protected def write(region: Int, value: Minutes) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.MinuteColumn]].
 *
 * @author myst3r10n
 */
object MinuteColumn {

  /** Creates [[widebase.db.column.MinuteColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Minutes*) = new MinuteColumn ++= values

}

