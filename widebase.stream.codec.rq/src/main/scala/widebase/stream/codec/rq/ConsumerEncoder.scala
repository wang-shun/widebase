package widebase.stream.codec.rq

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

import widebase.stream.codec. { MessageTypeEncoder, RequestEncoder }

/** Encode consumer message.
 *
 * @author myst3r10n
 */
class ConsumerEncoder extends RequestEncoder with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: SubscribeMessage =>
        writeId(buffer, MessageType.SubscribeMessage.id)
        writeString(buffer, msg.name)
        writeString(buffer, msg.selector)

      case _ => return super.encode(ctx, channel, msg)

    }

    buffer

  }
}

