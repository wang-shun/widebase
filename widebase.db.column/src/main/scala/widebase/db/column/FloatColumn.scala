package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Float]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class FloatColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Float](Datatype.Float) {

  import vario.data

  protected val sizeOf = data.sizeOf.float

  protected def read = mapper.readFloat
  protected def write(value: Float) {

    mapper.write(value)

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

