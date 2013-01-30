package widebase.io

import java.nio.ByteBuffer

import widebase.io.filter.ByteOrder

/** A common trait for [[widebase.io.ByteReader]] and [[widebase.io.ByteWriter]].
 *
 * @author myst3r10n
 */
trait ByteBufferLike {

  /** Buffer. */
  protected var buffer: ByteBuffer = _

  /** Overwritable capacity to adjust read/write performance, default is 4 KiB. */
  def capacity = 4096

  /** Overwritable byte order, default is [[widebase.fitler.ByteOrder.Native]]. */
  def order = ByteOrder.Native

  {

    buffer = ByteBuffer.allocateDirect(capacity)
    buffer.order(widebase.io.filter.asJavaByteOrder(order))

  }
}

