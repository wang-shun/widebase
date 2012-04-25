package widebase.db.column

import org.joda.time.LocalTime

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.LocalTime]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class TimeColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[LocalTime](Datatype.Time) {

  import vario.data

  protected val sizeOf = data.sizeOf.time

  protected def read = mapper.readTime
  protected def write(value: LocalTime) {

    mapper.write(value)

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

