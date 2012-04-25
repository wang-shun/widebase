package widebase.db.column

import org.joda.time.Minutes

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.Minutes]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class MinuteColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Minutes](Datatype.Minute) {

  import vario.data

  protected val sizeOf = data.sizeOf.minute

  protected def read = mapper.readMinute
  protected def write(value: Minutes) {

    mapper.write(value)

  }
}

/** Companion of [[widebase.db.column.MinuteColumn]].
 *
 * @author myst3r10n
 */
object MinuteColumn {

  /** Creates [[widebase.db.column.MinuteColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Minutes*) = new MinuteColumn ++= values

}

