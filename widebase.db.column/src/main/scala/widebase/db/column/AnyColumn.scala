package widebase.db.column

import vario.data.Datatype
import vario.file.FileVariantMapper

/** Implements a [[scala.Any]] column.
 *
 * @param mapper of file
 * @param records of mapper
 *
 * @author myst3r10n
 */
class AnyColumn extends TypedColumn[Any](Datatype.None) {

  protected val mapper: FileVariantMapper = null
  protected val records: Int = 0
  protected val sizeOf = 0

  protected def read = null

  protected def write(value: Any) {}

}

