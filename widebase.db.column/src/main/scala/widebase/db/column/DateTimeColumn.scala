package widebase.db.column

import org.joda.time.LocalDateTime

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[org.joda.time.LocalDateTime]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class DateTimeColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[LocalDateTime](Datatype.DateTime) {

  import widebase.data

  protected val sizeOf = data.sizeOf.dateTime

  protected def read(region: Int) = mappers(region).readDateTime
  protected def write(region: Int, value: LocalDateTime) {

    mappers(region: Int).write(value)

  }
}

/** Companion of [[widebase.db.column.DateTimeColumn]].
 *
 * @author myst3r10n
 */
object DateTimeColumn {

  /** Creates [[widebase.db.column.DateTimeColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: LocalDateTime*) = new DateTimeColumn ++= values

}

