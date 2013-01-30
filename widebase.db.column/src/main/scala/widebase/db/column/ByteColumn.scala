package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[scala.Byte]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class ByteColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Byte](Datatype.Byte) {

  import widebase.data

  protected val sizeOf = data.sizeOf.byte

  protected def read(region: Int) = mappers(region).read
  protected def write(region: Int, value: Byte) {

    mappers(region).write(value)

  }

  override protected def get(index: Int) = {

    mappers.head.position = index
    read(0)

  }

  override protected def set(index: Int, element: Byte) = {

    mappers.head.position = index
    write(0, element)

  }
}

/** Companion of [[widebase.db.column.ByteColumn]].
 *
 * @author myst3r10n
 */
object ByteColumn {

  /** Creates [[widebase.db.column.ByteColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Byte*) = new ByteColumn ++= values

}

