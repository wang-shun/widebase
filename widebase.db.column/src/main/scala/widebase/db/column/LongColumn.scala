package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Long]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class LongColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Long](Datatype.Long) {

  import vario.data

  protected val sizeOf = data.sizeOf.long

  protected def read = mapper.readLong
  protected def write(value: Long) {

    mapper.write(value)

  }
}

/** Companion of [[widebase.db.column.LongColumn]].
 *
 * @author myst3r10n
 */
object LongColumn {

  /** Creates [[widebase.db.column.LongColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Long*) = new LongColumn ++= values

}

