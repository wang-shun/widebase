package widebase.io.file

import java.nio.channels.FileChannel

import widebase.data.Datatype
import widebase.io.ToggleVariantMode

/** A common trait for [[widebase.io.file.FileVariantReader]] and [[widebase.io.file.FileVariantWriter]].
 *
 * @author myst3r10n
 */
trait FileChannelLike extends ToggleVariantMode {

  import widebase.data

  /** Channel. */
  protected val channel: FileChannel

  /** Position of [[java.nio.channels.FileChannel]] in [[java.lang.Byte]]s. */
  def position: Long = channel.position

  /** Sets position of [[java.nio.channels.FileChannel]] in [[java.lang.Byte]]s.
   *
   * @note Buffer loses data (on writers, use flush to avert)
   * @note Do not set if filter is [[widebase.io.filter.StreamFilter.Gzip]]
   *
   * @param replace position
   */
  def position_=(replace: Long) {

    buffer.clear
    channel.position(replace)

    mode match {

      case Datatype.None =>
      case Datatype.Bool =>
      case Datatype.Byte =>
      case Datatype.Char => charBuffer = buffer.asCharBuffer
      case Datatype.Double => doubleBuffer = buffer.asDoubleBuffer
      case Datatype.Float => floatBuffer = buffer.asFloatBuffer
      case Datatype.Int => intBuffer = buffer.asIntBuffer
      case Datatype.Long => longBuffer = buffer.asLongBuffer
      case Datatype.Short => shortBuffer = buffer.asShortBuffer
      case Datatype.Month =>
      case Datatype.Date => shortBuffer = buffer.asShortBuffer
      case Datatype.Minute => shortBuffer = buffer.asShortBuffer
      case Datatype.Second => shortBuffer = buffer.asShortBuffer
      case Datatype.Time => shortBuffer = buffer.asShortBuffer
      case Datatype.DateTime => longBuffer = buffer.asLongBuffer
      case Datatype.Timestamp => longBuffer = buffer.asLongBuffer
      case Datatype.Symbol =>
      case Datatype.String =>

    }
  }

  /** Current size of [[java.lang.channels.FileChannel]]. */
  def size = channel.size

}

