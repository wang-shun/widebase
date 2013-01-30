package widebase.db.column

import java.nio.channels.FileChannel

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype
import widebase.io.file.FileVariantMapper
import widebase.io.filter.MapFilter

/** Implements a [[scala.Boolean]] column.
 *
 * @param mapper of file
 * @param records of mapper
 * @param channel of companion
 *
 * @author myst3r10n
 */
class StringColumn(
  protected val mappers: ArrayBuffer[FileVariantMapper] = null,
  protected val records: Int = 0,
  protected val channel: FileChannel = null)
  extends TypedColumn[String](Datatype.String) {

  import widebase.data

  protected val sizeOf = data.sizeOf.long

  if(channel != null)
    mappers.foreach(mapper =>
      if(mapper != null && mapper.mode != Datatype.Long)
        mapper.mode = Datatype.Long)

  override protected def get(index: Int) = {

    val offset =
      if(index == 0)
        0L
      else {

        val position = (index.toLong - 1) * sizeOf

        val region = (position / Int.MaxValue).toInt

        if(region == 0)
          mappers(region).position = position.toInt
        else
          mappers(region).position = (position / region).toInt

        mappers(region).readLong

      }

    val position = index.toLong * sizeOf

    val region = (position / Int.MaxValue).toInt

    if(region == 0)
      mappers(region).position = position.toInt
    else
      mappers(region).position = (position / region).toInt

    val size = mappers(region).readLong - offset

    val stringMapper = new FileVariantMapper(
      channel,
      offset,
      size)(MapFilter.Private) {

      override val charset = props.charsets.strings

      override def order = props.orders.strings

    }

    if(stringMapper.mode != typeOf)
      stringMapper.mode = typeOf

    stringMapper.readString(size.toInt)

  }

  protected def read(region: Int) = mappers(region).readString
  protected def write(region: Int, value: String) {

    mappers(region).write(value)

  }
}

/** Companion of [[widebase.db.column.StringColumn]].
 *
 * @author myst3r10n
 */
object StringColumn {

  /** Creates [[widebase.db.column.StringColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: String*) = new StringColumn ++= values

}

