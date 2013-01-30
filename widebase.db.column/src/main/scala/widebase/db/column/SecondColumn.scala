package widebase.db.column

import org.joda.time.Seconds

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper

/** Implements a [[org.joda.time.Seconds]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class SecondColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0)
  extends TypedColumn[Seconds](Datatype.Second) {

  import widebase.data

  protected val sizeOf = data.sizeOf.second

  protected def read(region: Int) = mappers(region).readSecond
  protected def write(region: Int, value: Seconds) {

    mappers(region).write(value)

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

