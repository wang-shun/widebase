package widebase.stream.codec.cq

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.frame.CorruptedFrameException

import widebase.data.sizeOf

/** Decode response.
 *
 * @author myst3r10n
 */
class ResponseDecoder 
  extends widebase.stream.codec.ResponseDecoder
  with MessageTypeDecoder {

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

      case MessageType.TableFoundMessage => new TableFoundMessage

      case MessageType.RejectMessage =>
        val reason = readString(buffer)

        if(reason == null) {

          buffer.resetReaderIndex
          return null

        }

        new RejectMessage(reason)

      case MessageType.TableMessage =>
        val table = readTable(buffer)

        if(table == null) {

          buffer.resetReaderIndex
          return null

        }

        new TableMessage(table)

      case MessageType.TableNotFoundMessage => new TableNotFoundMessage
      case _ =>
        buffer.resetReaderIndex
        super.decode(ctx, channel, buffer)

    }
  }
}

