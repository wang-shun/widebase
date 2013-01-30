package widebase.stream.codec.cq

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

/** Encode response.
 *
 * @author myst3r10n
 */
class ResponseEncoder
 extends widebase.stream.codec.ResponseEncoder
 with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: RejectMessage =>
        writeId(buffer, MessageType.RejectMessage.id)
        writeString(buffer, msg.reason)

      case _: TableFoundMessage =>
        writeId(buffer, MessageType.TableFoundMessage.id)

      case msg: TableMessage =>
        writeId(buffer, MessageType.TableMessage.id)
        writeTable(buffer, msg.table)

      case _: TableNotFoundMessage => writeId(buffer, MessageType.TableNotFoundMessage.id)
      case _ => return super.encode(ctx, channel, msg)

    }

    buffer

  }
}

