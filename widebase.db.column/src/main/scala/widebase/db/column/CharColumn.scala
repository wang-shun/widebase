package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Char]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class CharColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Char](Datatype.Char) {

  import vario.data

  protected val sizeOf = data.sizeOf.char

  protected def read = mapper.readChar
  protected def write(value: Char) {

    mapper.write(value)

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

