package widebase.stream.codec

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder

/** Encode requests.
 *
 * @author myst3r10n
 */
class RequestEncoder extends OneToOneEncoder with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case msg: LoginMessage =>
        writeId(buffer, MessageType.LoginMessage.id)
        writeString(buffer, msg.username)
        writeString(buffer, msg.password)

      case _: RemoteShutdownMessage => writeId(buffer, MessageType.RemoteShutdownMessage.id)
      case _ => return msg

    }

    buffer

  }
}

