package widebase.io

import java.nio.ByteBuffer
import java.nio.charset.Charset

/** Implements toggle mode for read and write datatypes.
 *
 * @author myst3r10n
 */
trait ToggleTypedMode extends ToggleModeLike with TypedBufferLike {

  import widebase.data
  import widebase.data.Datatype
  import widebase.data.Datatype.Datatype

  /** @see [[java.nio.ByteBuffer]]. */
  protected var buffer: ByteBuffer

  override protected def reposition {

    mode match {

      case mode if
        mode == Datatype.None ||
        mode == Datatype.Bool ||
        mode == Datatype.Byte =>

      case Datatype.Char => buffer.position(buffer.position +
        (charBuffer.position * data.sizeOf.char))

      case Datatype.Double => buffer.position(buffer.position +
        (doubleBuffer.position * data.sizeOf.double))

      case Datatype.Float => buffer.position(buffer.position +
        (floatBuffer.position * data.sizeOf.float))

      case Datatype.Int => buffer.position(buffer.position +
        (intBuffer.position * data.sizeOf.int))

      case Datatype.Long => buffer.position(buffer.position +
        (longBuffer.position * data.sizeOf.long))

      case Datatype.Short => buffer.position(buffer.position +
        (shortBuffer.position * data.sizeOf.short))

    }
  }

  override protected def review(replace: Datatype) {

    replace match {

      case replace if
        replace == Datatype.None ||
        replace == Datatype.Bool ||
        replace == Datatype.Byte =>

      case Datatype.Char => charBuffer = buffer.asCharBuffer
      case Datatype.Double => doubleBuffer = buffer.asDoubleBuffer
      case Datatype.Float => floatBuffer = buffer.asFloatBuffer
      case Datatype.Int => intBuffer = buffer.asIntBuffer
      case Datatype.Long => longBuffer = buffer.asLongBuffer
      case Datatype.Short => shortBuffer = buffer.asShortBuffer

    }
  }
}

