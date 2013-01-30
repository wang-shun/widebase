package widebase.io

import java.nio. {

  CharBuffer,
  DoubleBuffer,
  FloatBuffer,
  IntBuffer,
  LongBuffer,
  ShortBuffer

}

/** Contains various datatype buffers.
 *
 * @author myst3r10n
 */
trait TypedBufferLike {

  protected var charBuffer: CharBuffer = _
  protected var doubleBuffer: DoubleBuffer = _
  protected var floatBuffer: FloatBuffer = _
  protected var intBuffer: IntBuffer = _
  protected var longBuffer: LongBuffer = _
  protected var shortBuffer: ShortBuffer = _

}

