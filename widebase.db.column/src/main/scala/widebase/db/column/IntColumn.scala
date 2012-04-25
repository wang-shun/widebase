package widebase.db.column

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
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Int](Datatype.Int) {

  import vario.data

  protected val sizeOf = data.sizeOf.int

  protected def read = mapper.readInt
  protected def write(value: Int) {

    mapper.write(value)

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

