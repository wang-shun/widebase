package widebase.db.column

import org.joda.time.Seconds

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[org.joda.time.Seconds]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class SecondColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0)
  extends TypedColumn[Seconds](Datatype.Second) {

  import vario.data

  protected val sizeOf = data.sizeOf.second

  protected def read = mapper.readSecond
  protected def write(value: Seconds) {

    mapper.write(value)

  }
}

/** Companion of [[widebase.db.column.SecondColumn]].
 *
 * @author myst3r10n
 */
object SecondColumn {

  /** Creates [[widebase.db.column.SecongColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Seconds*) = new SecondColumn ++= values

}

