package widebase.db.column

import org.joda.time.LocalTime

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[org.joda.time.LocalTime]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class TimeColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[LocalTime](Datatype.Time) {

  import widebase.data

  protected val sizeOf = data.sizeOf.time

  protected def read(region: Int) = mappers(region).readTime
  protected def write(region: Int, value: LocalTime) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.TimeColumn]].
 *
 * @author myst3r10n
 */
object TimeColumn {

  /** Creates [[widebase.db.column.TimeColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: LocalTime*) = new TimeColumn ++= values

}

