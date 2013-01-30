package widebase.io.file

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import widebase.io.filter. { MapFilter, ByteOrder }
import widebase.io.filter.MapFilter.MapFilter

/** Map a [[java.lang.channels.FileChannel]] for [[scala.Byte]]s.
 *
 * @param channel @see [[java.lang.channels.FileChannel]]
 * @param offset within the file at which the mapped region is to start; must be non-negative
 * @param size of the region to be mapped; must be non-negative and no greater than [[java.lang.Integer.MAX_VALUE]]
 * @param filter of mapper, default is [[widebase.io.filter.MapFilter.ReadOnly]]
 *
 * @author myst3r10n
 */
class FileByteMapper(
  channel: FileChannel,
  offset: Long = 0L,
  size: Long = -1L)
  (implicit filter: MapFilter = MapFilter.ReadOnly) {

  /** @see [[java.nio.MappedByteBuffer]]. */
  protected var buffer: MappedByteBuffer = _

  /** Overwritable byte order, default is [[widebase.io.fitler.ByteOrder.Native]]. */
  def order = ByteOrder.Native

  {

    val region =
      if(size == -1L)
        channel.size - offset
      else
        size

    buffer = channel.map(widebase.io.filter.asJavaMapMode(filter), offset, region)
    buffer.order(widebase.io.filter.asJavaByteOrder(order))

  }

  /** Capacity of [[java.nio.MappedByteBuffer]].
   *
   * @return like: size - offset
   */
  def capacity = buffer.capacity

  /** Closes [[java.nio.channels.FileChannel]].
   *
   * @note Use after initialization if channel not more needed.
   */
  def close = channel.close

  /** Tells whether or not [[java.nio.channels.FileChannel]] is open.
   *
   * @return true if, and only if, [[java.nio.channels.FileChannel]] is open
   */
  def isOpen = channel.isOpen

  /** Position of [[java.nio.MappedByteBuffer]] in [[java.lang.Byte]]s. */
  def position: Int = {

    buffer.position

  }

  /** Sets position of [[java.nio.MappedByteBuffer]] in [[java.lang.Byte]]s.
   *
   * @param replace position
   */
  def position_=(replace: Int) {

    buffer.position(replace)

  }

  /** Read [[scala.Byte]] from buffer. */
  def read: Byte = buffer.get

  /** Read array of [[scala.Byte]]s from buffer
   *
   * @param length of bytes to be read
   */
  def read(length: Int): Array[Byte] = {

    var bytes = Array.ofDim[Byte](length)

    buffer.get(bytes)

    bytes

  }

  /** Write [[scala.Byte]] into buffer.
   *
   * @param value to write
  */
  def write(value: Byte) {

    buffer.put(value)

  }

  /** Write array of [[scala.Byte]] into buffer.
   *
   * @param values to write
  */
  def write(values: Array[Byte]) {

    buffer.put(values)

  }
}

