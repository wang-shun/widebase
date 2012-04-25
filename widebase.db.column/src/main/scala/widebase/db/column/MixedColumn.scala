package widebase.db.column

import vario.data.Datatype
import vario.data.Datatype.Datatype

/** Implements a mixed column.
 *
 * @param t type of column
 * @param r records of mapper
 *
 * @author myst3r10n
 */
abstract class MixedColumn[A](t: Datatype) extends TypedColumn[A](t) {

  override def apply(idx: Int) =
    if(mapper == null || idx > records)
      buffer.apply(idx)
    else {

      val backupMode = mapper.mode
      mapper.mode = Datatype.Byte
      mapper.position = idx * sizeOf
      mapper.mode = backupMode

      read

    }

  override def last =
    if(mapper == null || records == 0)
      buffer.last
    else {

      val backupMode = mapper.mode
      mapper.mode = Datatype.Byte
      mapper.position = (records - 1) * sizeOf
      mapper.mode = backupMode

      read

    }

  override def update(idx: Int, value: A) {

    if(mapper != null && idx < records) {

      val backupMode = mapper.mode
      mapper.mode = Datatype.Byte
      mapper.position = idx * sizeOf
      mapper.mode = backupMode

      write(value)

    } else
      buffer(idx - records) = value

  }
}

