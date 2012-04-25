package widebase.db.column

import org.joda.time.LocalDateTime

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.LocalDateTime]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class DateTimeColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[LocalDateTime](Datatype.DateTime) {

  import vario.data

  protected val sizeOf = data.sizeOf.dateTime

  protected def read = mapper.readDateTime
  protected def write(value: LocalDateTime) {

    mapper.write(value)

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

