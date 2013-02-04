package widebase.stream.codec.rq

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext

import widebase.stream.codec. { MessageTypeEncoder, ResponseEncoder}

/** Encode broker message.
 *
 * @author myst3r10n
 */
class BrokerEncoder extends ResponseEncoder with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: EventMessage =>
        writeId(buffer, MessageType.EventMessage.id)
        writeString(buffer, msg.text)

      case msg: RollbackMessage =>
        writeId(buffer, MessageType.RollbackMessage.id)
        buffer.writeInt(msg.records)
        writeString(buffer, msg.partition)

      case msg: TableMessage =>
        writeId(buffer, MessageType.TableMessage.id)
        buffer.writeInt(msg.bytes.length)
        buffer.writeBytes(msg.bytes)

      case msg: UnparsableMessage =>
        writeId(buffer, MessageType.UnparsableMessage.id)
        writeString(buffer, msg.reason)

      case _: UnsubscribeMessage => writeId(buffer, MessageType.UnsubscribeMessage.id)
      case message => return super.encode(ctx, channel, msg)

    }

    buffer

  }
}

