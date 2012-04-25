package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Double]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class DoubleColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Double](Datatype.Double) {

  import vario.data

  protected val sizeOf = data.sizeOf.double

  protected def read = mapper.readDouble
  protected def write(value: Double) {

    mapper.write(value)

  }
}

/** Companion of [[widebase.db.column.DoubleColumn]].
 *
 * @author myst3r10n
 */
object DoubleColumn {

  /** Creates [[widebase.db.column.DoubleColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Double*) = new DoubleColumn ++= values

}

