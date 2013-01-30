package widebase.stream.codec

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

import org.jboss.netty.handler.codec.frame. {

  CorruptedFrameException,
  FrameDecoder

}

import vario.data.sizeOf

/** Decode response.
 *
 * @author myst3r10n
 */
class ResponseDecoder extends FrameDecoder with MessageTypeDecoder {

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

      case MessageType.BadMessage => new BadMessage
      case MessageType.DoneMessage => new DoneMessage
      case MessageType.ForbiddenMessage => new ForbiddenMessage
      case MessageType.LoginFailedMessage => new LoginFailedMessage
      case MessageType.LoginGrantedMessage => new LoginGrantedMessage
      case MessageType.LoginRequiredMessage => new LoginRequiredMessage
      case MessageType.UnauthorizedMessage => new UnauthorizedMessage
      case _ =>
        buffer.resetReaderIndex
        null

    }
  }
}

