package widebase.stream.codec.cq

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.frame.CorruptedFrameException

import vario.data.sizeOf

/** Decode request.
 *
 * @author myst3r10n
 */
class RequestDecoder
  extends widebase.stream.codec.RequestDecoder
  with MessageTypeDecoder {

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

      case MessageType.FindMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        new FindMessage(name)

      case MessageType.LoadMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        new LoadMessage(name)

      case MessageType.QueryMessage =>
        val statement = readString(buffer)

        if(statement == null) {

          buffer.resetReaderIndex
          return null

        }

        new QueryMessage(statement)

      case MessageType.SaveMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        val table = readTable(buffer)

        if(table == null) {

          buffer.resetReaderIndex
          return null

        }

        new SaveMessage(name, table)

      case _ =>
        buffer.resetReaderIndex
        super.decode(ctx, channel, buffer)

    }
  }
}

