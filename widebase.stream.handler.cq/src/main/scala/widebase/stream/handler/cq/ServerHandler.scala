package widebase.stream.handler.cq

import java.io. { PrintWriter, StringWriter }

import net.liftweb.common.Logger

import org.jboss.netty.channel. {

  ChannelHandlerContext,
  Channels,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import scala.concurrent.Lock

import widebase.stream.codec. { BadMessage, DoneMessage }

import widebase.stream.codec.cq. {

  FindMessage,
  LoadMessage,
  MessageType,
  QueryMessage,
  SaveMessage,
  TableMessage,
  TableFoundMessage,
  TableNotFoundMessage

}

import widebase.stream.handler.AuthHandler

/** Handles server.
 *
 * @param cache of database
 * @param readLock of cache
 * @param writeLock of cache
 *
 * @author myst3r10n
 */
class ServerHandler(
  cache: TableMap,
  readLock: CountLock,
  writeLock: BlockLock)
  extends SimpleChannelUpstreamHandler
  with Logger {

  override def exceptionCaught(ctx: ChannelHandlerContext, evt: ExceptionEvent) {

    evt.getCause match {

      case _: ReadTimeoutException =>
        error("Read timeout")
        evt.getChannel.close

      case _: WriteTimeoutException =>
        error("Write timeout")
        evt.getChannel.close

      case _ =>
        val message = new StringWriter
        val printer = new PrintWriter(message)
        evt.getCause.printStackTrace(printer)
        error(message.toString)
        evt.getChannel.close

    }
  }

  override def messageReceived(ctx: ChannelHandlerContext, evt: MessageEvent) {

    evt.getMessage match {

      case message: FindMessage =>

        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.FindMessage.toString))
          return

        writeLock.waitOfUnlock

        try {

          readLock++

          if(cache.contains(message.name))
            evt.getChannel.write(new TableFoundMessage)
          else
            evt.getChannel.write(new TableNotFoundMessage)

        } finally {

          readLock--

        }

      case message: LoadMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.LoadMessage.toString))
          return

        writeLock.waitOfUnlock

        try {

          readLock++

          if(cache.contains(message.name)) {

            evt.getChannel.write(new TableMessage(cache(message.name)))

            debug("Loaded table: " + message.name + " by " +
              AuthHandler.username.get(evt.getChannel) + " @ " +
              evt.getRemoteAddress)

          } else {

            debug("Table not found: " + message.name + " by " +
              AuthHandler.username.get(evt.getChannel) + " @ " +
              evt.getRemoteAddress)

            evt.getChannel.write(new TableNotFoundMessage)

          }
        } finally {

          readLock--

        }

      case message: QueryMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.QueryMessage.toString))
          return

        Channels.fireMessageReceived(ctx, message)

      case message: SaveMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.SaveMessage.toString))
          return

        try {

          writeLock.acquire
          readLock.waitOfUnlock

          if(cache.contains(message.name))
            cache(message.name) = message.table
          else
            cache += message.name -> message.table

        } finally {

          writeLock.release

        }

        debug("Saved table: " + message.name + " by " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

        evt.getChannel.write(new DoneMessage)

      case message =>
        error("Unfamiliar with message: " + message)
        evt.getChannel.write(new BadMessage)

    }
  }
}

