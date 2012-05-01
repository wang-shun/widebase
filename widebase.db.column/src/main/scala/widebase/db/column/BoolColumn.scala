package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Boolean]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class BoolColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Boolean](Datatype.Bool) {

  import vario.data

  protected val sizeOf = data.sizeOf.bool

  protected def read(region: Int) = mappers(region).readBool

  protected def write(region: Int, value: Boolean) {

    mappers(region).write(value)

  }

  override protected def get(index: Int) = {

    mappers.head.position = index
    read(0)

  }

  override protected def set(index: Int, element: Boolean) = {

    mappers.head.position = index
    write(0, element)

  }
}

/** Companion of [[widebase.db.column.BoolColumn]].
 *
 * @author myst3r10n
 */
object BoolColumn {

  /** Creates [[widebase.db.column.BoolColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Boolean*) = new BoolColumn ++= values

}

