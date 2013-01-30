package widebase.io.file

import java.nio.channels.FileChannel

import widebase.data.Datatype
import widebase.io.VariantReader
import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Read variant types from [[java.lang.channels.FileChannel]].
 *
 * @param channel @see [[java.lang.channels.FileChannel]]
 *
 * @author myst3r10n
 */
class FileVariantReader(override protected val channel: FileChannel)
  extends VariantReader(channel, StreamFilter.None)
  with FileChannelLike {

  private object Continue extends Throwable

  override def readChar: Char = {

    if(charBuffer.hasRemaining)
      return charBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    charBuffer.get

  }

  override def readChar(length: Int): Array[Char] = {

    val values = Array.ofDim[Char](length)

    if(values.length <= charBuffer.remaining)
      charBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!charBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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

  override def readDouble: Double = {

    if(doubleBuffer.hasRemaining)
      return doubleBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    doubleBuffer.get

  }

  override def readDouble(length: Int): Array[Double] = {

    val values = Array.ofDim[Double](length)

    if(values.length <= doubleBuffer.remaining)
      doubleBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!doubleBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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

  override def readFloat: Float = {

    if(floatBuffer.hasRemaining)
      return floatBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    floatBuffer.get

  }

  override def readFloat(length: Int): Array[Float] = {

    val values = Array.ofDim[Float](length)

    if(values.length <= floatBuffer.remaining)
      floatBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!floatBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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

  override def readInt: Int = {

    if(intBuffer.hasRemaining)
      return intBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    intBuffer.get

  }

  override def readInt(length: Int): Array[Int] = {

    val values = Array.ofDim[Int](length)

    if(values.length <= intBuffer.remaining)
      intBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!intBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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

  override def readLong: Long = {

    if(longBuffer.hasRemaining)
      return longBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    longBuffer.get

  }

  override def readLong(length: Int): Array[Long] = {

    val values = Array.ofDim[Long](length)

    if(values.length <= longBuffer.remaining)
      longBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!longBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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

  override def readShort: Short = {

    if(shortBuffer.hasRemaining)
      return shortBuffer.get

    val originMode = mode
    mode = Datatype.Byte

    if(buffer.hasRemaining)
      position = position - buffer.remaining

    next
    mode = originMode
    shortBuffer.get

  }

  override def readShort(length: Int): Array[Short] = {

    val values = Array.ofDim[Short](length)

    if(values.length <= shortBuffer.remaining)
      shortBuffer.get(values)
    else {

      var i = 0

      while(i < values.length)
        try {

          if(!shortBuffer.hasRemaining) {

            val originMode = mode
            mode = Datatype.Byte

            if(buffer.hasRemaining)
              position = position - buffer.remaining

            next
            mode = originMode

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
}

