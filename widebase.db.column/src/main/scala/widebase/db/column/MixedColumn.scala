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
/*
  override def apply(index: Int) =
    if(mappers == null || index > records)
      buffer.apply(index)
    else
      get(index)

  override def last =
    if(mappers == null || records == 0)
      buffer.last
    else
      get(records - 1)

  override def update(index: Int, value: A) {

    if(mappers != null && index < records)
      set(index, value)
    else
      buffer(index - records) = value

  }
*/
  override protected def get(index: Int) = {

    val position = index.toLong * sizeOf

    val region = (position / Int.MaxValue).toInt

    val backupMode = mappers(region).mode
    mappers(region).mode = Datatype.Byte

    if(region == 0)
      mappers(region).position = position.toInt
    else
      mappers(region).position = (position / region).toInt

    mappers(region).mode = backupMode

    read(region)

  }

  override protected def set(index: Int, element: A) = {

    val position = index.toLong * sizeOf

    val region = (position / Int.MaxValue).toInt

    val backupMode = mappers(region).mode
    mappers(region).mode = Datatype.Byte

    if(region == 0)
      mappers(region).position = position.toInt
    else
      mappers(region).position = (position / region).toInt

    mappers(region).mode = backupMode

    write(region, element)

  }
}

