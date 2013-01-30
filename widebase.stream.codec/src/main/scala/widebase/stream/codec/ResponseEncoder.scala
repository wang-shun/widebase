package widebase.stream.codec

import org.jboss.netty.buffer. { ChannelBuffer, ChannelBuffers }
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder

/** Encode requests.
 *
 * @author myst3r10n
 */
class ResponseEncoder extends OneToOneEncoder with MessageTypeEncoder {

  @throws(classOf[Exception])
  override def encode(
    ctx: ChannelHandlerContext,
    channel: Channel,
    msg: Object): Object = {

    val buffer = ChannelBuffers.dynamicBuffer

    msg match {

      case _: BadMessage => writeId(buffer, MessageType.BadMessage.id)
      case _: DoneMessage => writeId(buffer, MessageType.DoneMessage.id)
      case _: ForbiddenMessage => writeId(buffer, MessageType.ForbiddenMessage.id)
      case _: LoginFailedMessage => writeId(buffer, MessageType.LoginFailedMessage.id)
      case _: LoginGrantedMessage => writeId(buffer, MessageType.LoginGrantedMessage.id)
      case _: LoginRequiredMessage => writeId(buffer, MessageType.LoginRequiredMessage.id)
      case _: UnauthorizedMessage => writeId(buffer, MessageType.UnauthorizedMessage.id)
      case _ => return msg

    }

    buffer

  }
}

