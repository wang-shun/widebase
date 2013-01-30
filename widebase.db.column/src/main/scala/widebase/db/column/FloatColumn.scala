package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[scala.Float]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class FloatColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Float](Datatype.Float) {

  import widebase.data

  protected val sizeOf = data.sizeOf.float

  protected def read(region: Int) = mappers(region).readFloat
  protected def write(region: Int, value: Float) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.FloatColumn]].
 *
 * @author myst3r10n
 */
object FloatColumn {

  /** Creates [[widebase.db.column.FloatColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Float*) = new FloatColumn ++= values

}

