package widebase.stream.handler.rq

import java.io. { PrintWriter, StringWriter }

import net.liftweb.common.Logger

import org.jboss.netty.channel. {

  Channel,
  ChannelHandlerContext,
  ChannelLocal,
  ChannelStateEvent,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import scala.concurrent.Lock
import scala.collection.mutable. { ArrayBuffer, Map }

import widebase.stream.codec. { BadMessage, DoneMessage }

import widebase.stream.codec.rq. {

  MessageType,
  RollbackMessage,
  SubscribeMessage,
  TableMessage,
  UnsubscribeMessage

}

import widebase.stream.handler.AuthHandler

/** Handles subscriptions.
 *
 * @author myst3r10n
 */
class SubscribingHandler(
  protected val lock: Lock,
  protected val persistences: Map[String, PersistenceWriter],
  protected val subscriptions: Map[String, ArrayBuffer[ConsumerWriter]])
  extends SimpleChannelUpstreamHandler
  with Logger {

  override def channelClosed(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    if(SubscribingHandler.table.get(evt.getChannel) != null)
      unsubscribe(evt.getChannel)

  }

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

      case message: SubscribeMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.SubscribeMessage.toString))
          return

        val table = message.name
        val selector = message.selector

        if(SubscribingHandler.table.get(evt.getChannel) != null)
          unsubscribe(evt.getChannel)

        SubscribingHandler.table.set(evt.getChannel, table)

        val writer = new ConsumerWriter(evt.getChannel, selector)
        writer.start

        SubscribingHandler.writer.set(evt.getChannel, writer)

        try {

          lock.acquire

          if(!subscriptions.contains(table))
            subscriptions += table -> ArrayBuffer[ConsumerWriter]()

          subscriptions(table) += writer

          if(persistences.contains(table) && persistences(table).records > 0)
            writer ! new RollbackMessage(
              persistences(table).records,
              persistences(table).partition)

        } finally {

          lock.release

        }

        evt.getChannel.write(new DoneMessage)

        debug("Subscribed table: " + table + " by " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

      case message: UnsubscribeMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.UnsubscribeMessage.toString))
          return

        val table = SubscribingHandler.table.get(evt.getChannel)

        if(table != null)
          unsubscribe(evt.getChannel)

        debug("Unsubscribed table: " + table + " by " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

      case message =>
        error("Unfamiliar with message: " + message)
        evt.getChannel.write(new BadMessage)

    }
  }

  protected def unsubscribe(channel: Channel) {

    val table = SubscribingHandler.table.get(channel)
    val writer = SubscribingHandler.writer.get(channel)

    try {

      lock.acquire

      subscriptions(table) -= writer

      if(subscriptions(table).isEmpty)
        subscriptions -= table

    } finally {

      lock.release

    }

    writer ! Abort
    SubscribingHandler.writer.remove(channel)
    SubscribingHandler.table.remove(channel)

  }
}

/** Companion.
 *
 * @author myst3r10n
 */
object SubscribingHandler {

  val table = new ChannelLocal[String]
  val writer = new ChannelLocal[ConsumerWriter]

}

