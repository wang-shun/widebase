package widebase.stream.codec.cq

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

/** Encode request.
 *
 * @author myst3r10n
 */
class RequestEncoder
  extends widebase.stream.codec.RequestEncoder
  with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: FindMessage =>
        writeId(buffer, MessageType.FindMessage.id)
        writeString(buffer, msg.name)

      case msg: LoadMessage =>
        writeId(buffer, MessageType.LoadMessage.id)
        writeString(buffer, msg.name)

      case msg: QueryMessage =>
        writeId(buffer, MessageType.QueryMessage.id)
        writeString(buffer, msg.statement)

      case msg: SaveMessage =>
        writeId(buffer, MessageType.SaveMessage.id)
        writeString(buffer, msg.name)
        writeTable(buffer, msg.table)

      case _ => return super.encode(ctx, channel, msg)

    }

    buffer

  }
}

