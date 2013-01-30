package widebase.stream.codec.rq

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.frame.CorruptedFrameException

import widebase.stream.codec. { MessageTypeDecoder, ResponseDecoder }

/** Decode broker message.
 *
 * @author myst3r10n
 */
class BrokerDecoder extends ResponseDecoder with MessageTypeDecoder {

  import vario.data.sizeOf

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

      case MessageType.EventMessage =>
        val text = readString(buffer)

        if(text == null) {

          buffer.resetReaderIndex
          return null

        }

        new EventMessage(text)

      case MessageType.RollbackMessage =>
        if(buffer.readableBytes < sizeOf.int) {

          buffer.resetReaderIndex
          return null

        }

        val records = buffer.readInt

        val partition = readString(buffer)

        if(partition == null) {

          buffer.resetReaderIndex
          return null

        }

        new RollbackMessage(records, partition)

      case MessageType.TableMessage =>
        if(buffer.readableBytes < sizeOf.int) {

          buffer.resetReaderIndex
          return null

        }

        val length = buffer.readInt

        if(buffer.readableBytes < length) {

          buffer.resetReaderIndex
          return null

        }

        val decodedBytes = Array.ofDim[Byte](length)

        buffer.readBytes(decodedBytes)

        new TableMessage(decodedBytes)

      case MessageType.UnsubscribeMessage => new UnsubscribeMessage
      case _ =>
        buffer.resetReaderIndex
        super.decode(ctx, channel, buffer)

    }
  }
}

