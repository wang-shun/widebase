package widebase.db.column

import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.file.FileVariantMapper
import vario.filter.MapFilter

/** Implements a [[scala.Boolean]] column.
 *
 * @param mapper of file
 * @param records of mapper
 * @param channel of companion
 *
 * @author myst3r10n
 */
class StringColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0,
  protected val channel: FileChannel = null)
  extends VariableColumn[String](Datatype.String) {

  import vario.data

  protected val sizeOf = data.sizeOf.long

  if(
    mapper != null &&
    channel != null &&
    mapper.mode != Datatype.Long)
    mapper.mode = Datatype.Long

  protected def get(idx: Int) = {

    val position =
      if(idx == 0)
        0L
      else {

        mapper.position = (idx - 1) * data.sizeOf.long
        mapper.readLong

      }

    mapper.position = idx * data.sizeOf.long
    val size = mapper.readLong - position

    val stringMapper = new FileVariantMapper(
      channel,
      position,
      size)(MapFilter.Private) {

      override val charset = props.charsets.strings

      override def order = props.orders.strings

    }

    if(stringMapper.mode != typeOf)
      stringMapper.mode = typeOf

    stringMapper.readString(size.toInt)

  }

  protected def read = mapper.readString
  protected def write(value: String) {

    mapper.write(value)

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

