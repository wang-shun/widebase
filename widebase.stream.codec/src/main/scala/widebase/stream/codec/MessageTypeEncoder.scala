package widebase.stream.codec

import org.jboss.netty.buffer.ChannelBuffer

/** Encode message.
 *
 * @author myst3r10n
 */
trait MessageTypeEncoder {

  import widebase.data.sizeOf

  /** Write message id into buffer.
   *
   * @param buffer of channel
   * @param id of message
   */
  protected def writeId(buffer: ChannelBuffer, id: Int) {

    buffer.writeByte(id.toByte)

  }

  /** Write string into buffer.
   *
   * @param buffer of channel
   * @param text of string
   */
  protected def writeString(buffer: ChannelBuffer, text: String) {

    val encodedText = text.getBytes(props.charsets.encoder)

    buffer.writeInt(encodedText.length)
    buffer.writeBytes(encodedText)

  }
}

