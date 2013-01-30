package widebase.db.column

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[scala.Char]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class CharColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Char](Datatype.Char) {

  import widebase.data

  protected val sizeOf = data.sizeOf.char

  protected def read(region: Int) = mappers(region).readChar
  protected def write(region: Int, value: Char) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.CharColumn]].
 *
 * @author myst3r10n
 */
object CharColumn {

  /** Creates [[widebase.db.column.CharColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Char*) = new CharColumn ++= values

}

