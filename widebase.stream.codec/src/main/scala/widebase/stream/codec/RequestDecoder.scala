package widebase.stream.codec

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

import org.jboss.netty.handler.codec.frame. {

  CorruptedFrameException,
  FrameDecoder

}

import widebase.data.sizeOf

/** Decode requests.
 *
 * @author myst3r10n
 */
class RequestDecoder extends FrameDecoder with MessageTypeDecoder {

  import MessageType.MessageType

  @throws(classOf[Exception])
  override def decode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    buffer: ChannelBuffer): Object = {

    val id = readId(buffer)

    if(id.isEmpty)
      return null

    val messageType =
      try {

        MessageType(id.get)

      } catch {

        case e: NoSuchElementException =>
          buffer.resetReaderIndex
          throw new CorruptedFrameException("Unfamiliar with message id: " +
            id.get)

      }

    messageType match {

      case MessageType.LoginMessage =>
        val username = readString(buffer)

        if(username == null) {

          buffer.resetReaderIndex
          return null

        }

        val password = readString(buffer)

        if(password == null) {

          buffer.resetReaderIndex
          return null

        }

        new LoginMessage(username, password)

      case MessageType.RemoteShutdownMessage => new RemoteShutdownMessage
      case _ =>
        buffer.resetReaderIndex
        null

    }
  }
}

