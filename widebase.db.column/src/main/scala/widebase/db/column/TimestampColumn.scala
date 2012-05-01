package widebase.db.column

import java.sql.Timestamp

import scala.collection.mutable.ArrayBuffer

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[java.sql.Timestamp]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class TimestampColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Timestamp](Datatype.Timestamp) {

  import vario.data

  protected val sizeOf = data.sizeOf.timestamp

  protected def read(region: Int) = mappers(region).readTimestamp
  protected def write(region: Int, value: Timestamp) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.TimestampColumn]].
 *
 * @author myst3r10n
 */
object TimestampColumn {

  /** Creates [[widebase.db.column.TimestampColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Timestamp*) = new TimestampColumn ++= values

}

