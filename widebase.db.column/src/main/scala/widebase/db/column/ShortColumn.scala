package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Short]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class ShortColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Short](Datatype.Short) {

  import vario.data

  protected val sizeOf = data.sizeOf.short

  protected def read(region: Int) = mappers(region).readShort
  protected def write(region: Int, value: Short) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.ShortColumn]].
 *
 * @author myst3r10n
 */
object ShortColumn {

  /** Creates [[widebase.db.column.ShortColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Short*) = new ShortColumn ++= values

}

