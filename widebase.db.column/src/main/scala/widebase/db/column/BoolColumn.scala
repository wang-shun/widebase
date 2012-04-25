package widebase.db.column

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
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Boolean](Datatype.Bool) {

  import vario.data

  protected val sizeOf = data.sizeOf.bool

  protected def read = mapper.readBool

  protected def write(value: Boolean) {

    mapper.write(value)

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

