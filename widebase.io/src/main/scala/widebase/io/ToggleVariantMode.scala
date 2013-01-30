package widebase.io

import java.nio.ByteBuffer
import java.nio.charset.Charset

/** Implements toggle mode for read and write variant types.
 *
 * @author myst3r10n
 */
trait ToggleVariantMode extends ToggleTypedMode {

  import widebase.data
  import widebase.data.Datatype
  import widebase.data.Datatype.Datatype

  override protected def reposition {

    mode match {

      case mode if
        mode == Datatype.Month ||
        mode == Datatype.Symbol ||
        mode == Datatype.String =>

      case mode if
        mode == Datatype.Date ||
        mode == Datatype.Minute ||
        mode == Datatype.Second ||
        mode == Datatype.Time => buffer.position(buffer.position +
          (intBuffer.position * data.sizeOf.int))

      case mode if
        mode == Datatype.DateTime ||
        mode == Datatype.Timestamp => buffer.position(buffer.position +
          (longBuffer.position * data.sizeOf.long))

      case _ => super.reposition

    }
  }

  override protected def review(replace: Datatype) {

    replace match {

      case replace if
        replace == Datatype.Month ||
        replace == Datatype.Symbol ||
        replace == Datatype.String =>

      case replace if
        replace == Datatype.Date ||
        replace == Datatype.Minute ||
        replace == Datatype.Second ||
        replace == Datatype.Time => intBuffer = buffer.asIntBuffer

      case replace if
        replace == Datatype.DateTime ||
        replace == Datatype.Timestamp => longBuffer = buffer.asLongBuffer

      case _ => super.review(replace)

    }
  }
}

