package widebase.stream.codec.rq

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

import widebase.stream.codec. { MessageTypeEncoder, RequestEncoder }

/** Encode producer message.
 *
 * @author myst3r10n
 */
class ProducerEncoder extends RequestEncoder with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: FlushMessage =>
        writeId(buffer, MessageType.FlushMessage.id)
        writeString(buffer, msg.name)

      case msg: NotifyMessage =>
        writeId(buffer, MessageType.NotifyMessage.id)
        writeString(buffer, msg.name)
        writeString(buffer, msg.text)

      case msg: PublishMessage =>
        writeId(buffer, MessageType.PublishMessage.id)
        writeString(buffer, msg.name)
        buffer.writeInt(msg.bytes.length)
        buffer.writeBytes(msg.bytes)

      case _ => return super.encode(ctx, channel, msg)

    }

    buffer

  }
}

