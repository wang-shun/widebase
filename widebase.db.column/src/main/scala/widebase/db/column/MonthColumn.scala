package widebase.db.column

import org.joda.time.YearMonth

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.YearMonth]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class MonthColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends MixedColumn[YearMonth](Datatype.Month) {

  import vario.data

  protected val sizeOf = data.sizeOf.month

  protected def read = mapper.readMonth
  protected def write(value: YearMonth) {

    mapper.write(value)

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

