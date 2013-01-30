package widebase.stream.handler

import java.io. { PrintWriter, StringWriter }

import net.liftweb.common.Logger

import org.jboss.netty.channel. {

  Channels,
  ChannelHandlerContext,
  ChannelStateEvent,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.channel.group.DefaultChannelGroup

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import widebase.stream.codec. {

  DoneMessage,
  MessageType,
  RemoteShutdownMessage

}

/** Handles control.
 *
 * @param allChannels self-explanatory
 *
 * @author myst3r10n
 */
class ControlHandler(allChannels: DefaultChannelGroup)
  extends SimpleChannelUpstreamHandler
  with Logger {

  override def channelClosed(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    Channels.fireChannelClosed(ctx)

  }

  override def exceptionCaught(ctx: ChannelHandlerContext, evt: ExceptionEvent) {

    evt.getCause match {

      case _: ReadTimeoutException =>
        error("Read timeout: " + ctx.getChannel.getRemoteAddress)
        evt.getChannel.close.awaitUninterruptibly

      case _: WriteTimeoutException =>
        error("Write timeout: " + ctx.getChannel.getRemoteAddress)
        evt.getChannel.close.awaitUninterruptibly

      case _ =>
        val message = new StringWriter
        val printer = new PrintWriter(message)
        evt.getCause.printStackTrace(printer)
        error(message.toString)
        evt.getChannel.close.awaitUninterruptibly

    }
  }

  override def messageReceived(ctx: ChannelHandlerContext, evt: MessageEvent) {

    evt.getMessage match {

      case message: RemoteShutdownMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.RemoteShutdownMessage.toString))
          return

        info("Shutdown by remotely call: " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

        evt.getChannel.write(new DoneMessage)

        allChannels.close

      case message => Channels.fireMessageReceived(ctx, message)

    }
  }
}

