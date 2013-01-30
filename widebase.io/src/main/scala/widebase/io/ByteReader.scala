package widebase.io

import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.channels. { Channels, ReadableByteChannel }
import java.util.zip. { GZIPInputStream, ZipInputStream }

import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Read [[scala.Byte]]s from [[java.nio.channels.ReadableByteChannel]].
 *
 * @param channel @see [[java.nio.channels.ReadableByteChannel]]
 * @param filter of reader, default is [[widebase.io.filter.StreamFilter.None]]
 *
 * @author myst3r10n
 */
class ByteReader(
  protected val channel: ReadableByteChannel,
  filter: StreamFilter = StreamFilter.None)
  extends ByteBufferLike {

  /** Gzip channel. */
  protected var zip: ReadableByteChannel = _

  {

    zip =
      if(filter == StreamFilter.Gzip)
        Channels.newChannel(
          new GZIPInputStream(Channels.newInputStream(channel)))
      else if(filter == StreamFilter.Zlib) {

        val zlibStream = new ZipInputStream(Channels.newInputStream(channel))

        zlibStream.getNextEntry

        Channels.newChannel(zlibStream)

      } else
        null

    read(buffer)
    buffer.flip

  }

  /** Closes all associated channels. */
  def close {

    if(zip != null)
      zip.close

    channel.close

  }

  /** Tells whether [[java.lang.channels.WritableByteChannel]] is open or not. */
  def isOpen = channel.isOpen

  /** Read [[scala.Byte]] from buffer */
  def read: Byte = {

    if(!buffer.hasRemaining)
      next

    buffer.get

  }

  /** Read array of [[scala.Byte]]s from buffer.
   *
   * @param length of bytes to be read
  */
  def read(length: Int): Array[Byte] = {

    if(!buffer.hasRemaining)
      next

    val bytes = Array.ofDim[Byte](length)

    if(bytes.length < buffer.remaining)
      buffer.get(bytes)
    else {

      var i = 0

      while(i < bytes.length) {

        if(!buffer.hasRemaining)
          next

        var length = bytes.length - i

        if(length >= buffer.remaining)
          length = buffer.remaining

        buffer.get(bytes, i, length)

        i += length

      }
    }

    bytes

  }

  protected def next {

    buffer.clear

    if(read(buffer) <= 0)
      throw new EOFException

    buffer.flip

  }

  /** Read bytes from [[java.nio.channels.ReadableByteChannel]].
   *
   * @param buffer to read
   *
   * @return number of bytes read or -1 by end-of-file
  */
  protected def read(buffer: ByteBuffer) =
    filter match {

      case StreamFilter.None => channel.read(buffer)
      case filter if
        filter == StreamFilter.Gzip ||
        filter == StreamFilter.Zlib => zip.read(buffer)

    }
}

