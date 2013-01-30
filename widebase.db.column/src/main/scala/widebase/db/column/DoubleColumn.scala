package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[scala.Double]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class DoubleColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Double](Datatype.Double) {

  import widebase.data

  protected val sizeOf = data.sizeOf.double

  protected def read(region: Int) = mappers(region).readDouble
  protected def write(region: Int, value: Double) {

    mappers(region: Int).write(value)

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

