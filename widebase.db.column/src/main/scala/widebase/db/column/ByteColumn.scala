package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Byte]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class ByteColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Byte](Datatype.Byte) {

  import vario.data

  protected val sizeOf = data.sizeOf.byte

  protected def read = mapper.read
  protected def write(value: Byte) {

    mapper.write(value)

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

