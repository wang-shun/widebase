package widebase.io

import java.nio.ByteBuffer
import java.nio.channels. { Channels, WritableByteChannel }
import java.util.zip. { GZIPOutputStream, ZipEntry, ZipOutputStream }

import widebase.io.filter. { CompressionLevel, StreamFilter }
import widebase.io.filter.StreamFilter.StreamFilter

/** Write [[scala.Byte]]s into [[java.lang.channels.WritableByteChannel]].
 *
 * @param channel @see [[java.lang.channels.WritableByteChannel]]
 * @param filter of writer, default is [[widebase.io.filter.StreamFilter.None]]
 * @param level of compression 0-9, default is [[CompressionLevel.Default]]
 *
 * @author myst3r10n
 */
class ByteWriter(
  protected val channel: WritableByteChannel,
  filter: StreamFilter = StreamFilter.None,
  level: Int = CompressionLevel.Default)
  extends ByteBufferLike {

  private var zlibStream: ZipOutputStream = _

  /** Gzip channel. */
  protected var zip: WritableByteChannel = _

  {

    zip =
      if(filter == StreamFilter.Gzip) {

        val gzipStream =
          new GZIPOutputStream(Channels.newOutputStream(channel)) {

            `def`.setLevel(level)

          }

        Channels.newChannel(gzipStream)

      } else if(filter == StreamFilter.Zlib) {

        zlibStream = new ZipOutputStream(Channels.newOutputStream(channel))

        zlibStream.setLevel(level)
        zlibStream.putNextEntry(new ZipEntry(""))

        Channels.newChannel(zlibStream)

      } else
        null

  }

  /** Flushes and closes all associated channels. */
  def close {

    flush

    if(zip != null) {

      if(zlibStream != null)
        zlibStream.closeEntry

      zip.close

    }

    channel.close

  }

  /** Flushes and forces any buffered output bytes to be written out. */
  def flush {

    buffer.flip
    write(buffer)
    buffer.clear

  }

  /** Tells whether [[java.lang.channels.WritableByteChannel]] is open or not. */
  def isOpen = channel.isOpen

  /** Write [[scala.Byte]] into buffer
   *
   * @param value to write
  */
  def write(value: Byte) {

    if(!buffer.hasRemaining)
      flush

    buffer.put(value)

  }

  /** Write array of [[scala.Byte]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Byte]) {

    if(!buffer.hasRemaining)
      flush

    if(values.length < buffer.remaining)
      buffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= buffer.remaining)
          length = buffer.remaining

        buffer.put(values, i, length)

        if(!buffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write byte into file
   *
   * @param buffer to write
   *
   * @return number of bytes written
  */
  protected def write(buffer: ByteBuffer) {

    filter match {

      case StreamFilter.None => channel.write(buffer)
      case filter if
        filter == StreamFilter.Gzip ||
        filter == StreamFilter.Zlib => zip.write(buffer)

    }
  }
}

