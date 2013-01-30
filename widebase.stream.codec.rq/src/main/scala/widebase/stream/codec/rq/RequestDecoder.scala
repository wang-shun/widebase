package widebase.stream.codec.rq

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.frame.CorruptedFrameException

import widebase.stream.codec.MessageTypeDecoder

/** Decode request.
 *
 * @author myst3r10n
 */
class RequestDecoder
  extends widebase.stream.codec.RequestDecoder
  with MessageTypeDecoder {

  import MessageType.MessageType

  import widebase.data.sizeOf

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

      case MessageType.FlushMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        new FlushMessage(name)

      case MessageType.NotifyMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        val text = readString(buffer)

        if(text == null) {

          buffer.resetReaderIndex
          return null

        }

        new NotifyMessage(name, text)

      case MessageType.PublishMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

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

        new PublishMessage(name, decodedBytes)

      case MessageType.SubscribeMessage =>
        val name = readString(buffer)

        if(name == null) {

          buffer.resetReaderIndex
          return null

        }

        val selector = readString(buffer)

        if(selector == null) {

          buffer.resetReaderIndex
          return null

        }

        new SubscribeMessage(name, selector)

      case _ =>
        buffer.resetReaderIndex
        super.decode(ctx, channel, buffer)

    }
  }
}

