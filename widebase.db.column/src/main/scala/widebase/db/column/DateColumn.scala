package widebase.db.column

import org.joda.time.LocalDate

import scala.collection.mutable.ArrayBuffer

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.LocalDate]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class DateColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[LocalDate](Datatype.Date) {

  import vario.data

  protected val sizeOf = data.sizeOf.date

  protected def read(region: Int) = mappers(region).readDate
  protected def write(region: Int, value: LocalDate) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.DateColumn]].
 *
 * @author myst3r10n
 */
object DateColumn {

  /** Creates [[widebase.db.column.DateColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: LocalDate*) = new DateColumn ++= values

}

