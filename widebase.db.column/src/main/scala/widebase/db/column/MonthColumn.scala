package widebase.db.column

import org.joda.time.YearMonth

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[org.joda.time.YearMonth]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class MonthColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[YearMonth](Datatype.Month) {

  import widebase.data

  protected val sizeOf = data.sizeOf.month

  protected def read(region: Int) = mappers(region).readMonth
  protected def write(region: Int, value: YearMonth) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.MonthColumn]].
 *
 * @author myst3r10n
 */
object MonthColumn {

  /** Creates [[widebase.db.column.MonthColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: YearMonth*) = new MonthColumn ++= values

}

