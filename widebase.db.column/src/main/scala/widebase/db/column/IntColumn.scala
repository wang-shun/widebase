package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Int]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class IntColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Int](Datatype.Int) {

  import vario.data

  protected val sizeOf = data.sizeOf.int

  protected def read(region: Int) = mappers(region).readInt
  protected def write(region: Int, value: Int) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.IntColumn]].
 *
 * @author myst3r10n
 */
object IntColumn {

  /** Creates [[widebase.db.column.IntColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Int*) = new IntColumn ++= values

}

