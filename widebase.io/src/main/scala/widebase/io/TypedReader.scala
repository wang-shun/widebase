package widebase.io

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

import widebase.data.Datatype
import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Read datatypes from [[java.nio.channels.ReadableByteChannel]].
 *
 * @param channel @see [[java.lang.channels.ReadableByteChannel]]
 * @param filter of reader, default is [[widebase.io.filter.StreamFilter.None]]
 *
 * @author myst3r10n
 */
class TypedReader(
  channel: ReadableByteChannel,
  filter: StreamFilter = StreamFilter.None)
  extends ByteReader(channel, filter)
  with ToggleTypedMode {

  import widebase.data

  private object Continue extends Throwable

  /** Read [[scala.Boolean]] from buffer. */
  def readBool: Boolean = {

    val bool = read

    if(bool == (1: Byte))
      return true

    if(bool == (0: Byte))
      return false

    throw TypeMismatchException(mode, bool.toString)

  }

  /** Read array of [[scala.Boolean]]s from buffer.
   *
   * @param length of [[scala.Boolean]] array to be read
  */
  def readBool(length: Int): Array[Boolean] = {

    val values =
      for(value <- read(length))
        yield {

          if(value == (1: Byte))
            true
          else if(value == (0: Byte))
            false
          else
            throw TypeMismatchException(mode, value.toString)

        }

    values

  }

  /** Read [[scala.Char]] from buffer. */
  def readChar: Char = {

    if(charBuffer.hasRemaining)
      return charBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.char).asCharBuffer.get

      next

    } finally {

      mode = originMode

    }

    charBuffer.get

  }

  /** Read array of [[scala.Char]]s from buffer.
   *
   * @param length of [[scala.Char]] array to be read
  */
  def readChar(length: Int): Array[Char] = {

    val values = Array.ofDim[Char](length)

    if(values.length <= charBuffer.remaining)
      charBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!charBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.char).asCharBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= charBuffer.remaining)
            length = charBuffer.remaining

          charBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Read [[scala.Double]] from buffer. */
  def readDouble: Double = {

    if(doubleBuffer.hasRemaining)
      return doubleBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.double).asDoubleBuffer.get

      next

    } finally {

      mode = originMode

    }

    doubleBuffer.get

  }

  /** Read array of [[scala.Double]]s from buffer.
   *
   * @param length of [[scala.Double]] array to be read
  */
  def readDouble(length: Int): Array[Double] = {

    val values = Array.ofDim[Double](length)

    if(values.length <= doubleBuffer.remaining)
      doubleBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!doubleBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.double).asDoubleBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= doubleBuffer.remaining)
            length = doubleBuffer.remaining

          doubleBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Read [[scala.Float]] from buffer. */
  def readFloat: Float = {

    if(floatBuffer.hasRemaining)
      return floatBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.float).asFloatBuffer.get

      next

    } finally {

      mode = originMode

    }

    floatBuffer.get

  }

  /** Read array of [[scala.Float]]s from buffer.
   *
   * @param length of [[scala.Float]] array to be read
  */
  def readFloat(length: Int): Array[Float] = {

    val values = Array.ofDim[Float](length)

    if(values.length <= floatBuffer.remaining)
      floatBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!floatBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.float).asFloatBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= floatBuffer.remaining)
            length = floatBuffer.remaining

          floatBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Read [[scala.Int]] from buffer. */
  def readInt: Int = {

    if(intBuffer.hasRemaining)
      return intBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.int).asIntBuffer.get

      next

    } finally {

      mode = originMode

    }

    intBuffer.get

  }

  /** Read array of [[scala.Int]]s from buffer.
   *
   * @param length of [[scala.Int]] array to be read
  */
  def readInt(length: Int): Array[Int] = {

    val values = Array.ofDim[Int](length)

    if(values.length <= intBuffer.remaining)
      intBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!intBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.int).asIntBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= intBuffer.remaining)
            length = intBuffer.remaining

          intBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Read [[scala.Long]] from buffer. */
  def readLong: Long = {

    if(longBuffer.hasRemaining)
      return longBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.long).asLongBuffer.get

      next

    } finally {

      mode = originMode

    }

    longBuffer.get

  }

  /** Read array of [[scala.Long]]s from buffer.
   *
   * @param length of [[scala.Long]] array to be read
  */
  def readLong(length: Int): Array[Long] = {

    val values = Array.ofDim[Long](length)

    if(values.length <= longBuffer.remaining)
      longBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!longBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.long).asLongBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= longBuffer.remaining)
            length = longBuffer.remaining

          longBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Read [[scala.Short]] from buffer. */
  def readShort: Short = {

    if(shortBuffer.hasRemaining)
      return shortBuffer.get

    val originMode = mode

    try {

      mode = Datatype.Byte

      if(buffer.hasRemaining)
        return shift(data.sizeOf.short).asShortBuffer.get

      next

    } finally {

      mode = originMode

    }

    shortBuffer.get

  }

  /** Read array of [[scala.Short]]s from buffer.
   *
   * @param length of [[scala.Short]] array to be read
  */
  def readShort(length: Int): Array[Short] = {

    val values = Array.ofDim[Short](length)

    if(values.length <= shortBuffer.remaining)
      shortBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!shortBuffer.hasRemaining) {

            val originMode = mode

            try {

              mode = Datatype.Byte

              if(buffer.hasRemaining) {

                values(i) = shift(data.sizeOf.short).asShortBuffer.get

                i += 1
                throw Continue

              } else
                next

            } finally {

              mode = originMode

            }
          }

          var length = values.length - i

          if(length >= shortBuffer.remaining)
            length = shortBuffer.remaining

          shortBuffer.get(values, i, length)

          i += length

        } catch {

          case Continue => // Continue loop

        }
      }

    values

  }

  /** Shifts remaining bytes.
   *
   * @param sizeOf type in bytes to round out
   *
   * @return type in bytes
   **/
  protected def shift(sizeOf: Int) = {

    var offset = buffer.remaining
    var remaining = Array.ofDim[Byte](sizeOf)

    buffer.get(remaining, 0, buffer.remaining)

    next // read next bytes.

    val length = sizeOf - offset

    if(length > 0 && buffer.hasRemaining)
      buffer.get(remaining, offset, length)

    ByteBuffer.wrap(remaining).order(buffer.order)

  }
}

