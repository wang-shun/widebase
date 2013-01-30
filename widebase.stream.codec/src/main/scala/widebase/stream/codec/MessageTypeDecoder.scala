package widebase.stream.codec

import org.jboss.netty.buffer.ChannelBuffer

/** Decode messages.
 *
 * @author myst3r10n
 */
trait MessageTypeDecoder {

  import widebase.data.sizeOf

  /** Read message id from buffer.
   *
   * @param buffer of channel
   *
   * @return message id or [[scala.None]]
   */
  protected def readId(buffer: ChannelBuffer): Option[Int] =
    if(buffer.readableBytes < sizeOf.byte) {

      buffer.resetReaderIndex
      return None

    } else
      Some(buffer.readByte.toInt)

  /** Read string from buffer.
   *
   * @param buffer of channel
   *
   * @return text of string or `null`
   */
  protected def readString(buffer: ChannelBuffer): String = {

    if(buffer.readableBytes < sizeOf.int) {

      buffer.resetReaderIndex
      return null

    }

    val length = buffer.readInt

    if(buffer.readableBytes < length) {

      buffer.resetReaderIndex
      return null

    }

    val decodedText = Array.ofDim[Byte](length)

    buffer.readBytes(decodedText)

    new String(decodedText, props.charsets.decoder)

  }
}

