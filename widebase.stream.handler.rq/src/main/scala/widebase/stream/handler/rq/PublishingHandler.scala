package widebase.stream.handler.rq

import java.io. { PrintWriter, StringWriter }

import net.liftweb.common.Logger

import org.jboss.netty.channel. {

  Channel,
  ChannelHandlerContext,
  ChannelLocal,
  Channels,
  ChannelStateEvent,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import scala.actors. { Actor, TIMEOUT }
import scala.collection.mutable. { ArrayBuffer, Map }
import scala.concurrent.Lock

import widebase.db.table.Table
import widebase.stream.codec.DoneMessage

import widebase.stream.codec.rq. {

  FlushMessage,
  MessageType,
  NotifyMessage,
  PublishMessage

}

import widebase.stream.handler.AuthHandler

/** Handles publishers.
 *
 * @author myst3r10n
 */
class PublishingHandler(
  val path: String,
  val interval: Long,
  protected val lock: Lock,
  protected val persistences: Map[String, PersistenceWriter],
  protected val subscriptions: Map[String, ArrayBuffer[ConsumerWriter]])
  extends SimpleChannelUpstreamHandler
  with Logger
  with PersistenceHandler {

  override def channelOpen(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    Channels.fireChannelOpen(ctx)

  }

  override def channelClosed(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    val broadcaster = PublishingHandler.broadcaster.get(evt.getChannel)

    if(broadcaster != null)
      broadcaster ! Abort

    Channels.fireChannelClosed(ctx)

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

      case message: FlushMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.FlushMessage.toString))
          return

        var broadcaster = PublishingHandler.broadcaster.get(evt.getChannel)

        if(broadcaster == null) {

          broadcaster = new Broadcaster
          broadcaster.start
          PublishingHandler.broadcaster.set(evt.getChannel, broadcaster)

        }

        broadcaster ! message

        evt.getChannel.write(new DoneMessage)

      case message: NotifyMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.NotifyMessage.toString))
          return

        var broadcaster = PublishingHandler.broadcaster.get(evt.getChannel)

        if(broadcaster == null) {

          broadcaster = new Broadcaster
          broadcaster.start
          PublishingHandler.broadcaster.set(evt.getChannel, broadcaster)

        }

        broadcaster ! message

        evt.getChannel.write(new DoneMessage)

      case message: PublishMessage =>
        if(!AuthHandler.hasAuthorization(
          evt.getChannel,
          MessageType.PublishMessage.toString))
          return

        var broadcaster = PublishingHandler.broadcaster.get(evt.getChannel)

        if(broadcaster == null) {

          broadcaster = new Broadcaster
          broadcaster.start
          PublishingHandler.broadcaster.set(evt.getChannel, broadcaster)

        }

        broadcaster ! message

        evt.getChannel.write(new DoneMessage)

      case message => Channels.fireMessageReceived(ctx, message)

    }
  }

  protected class Broadcaster extends Actor {

    def act {

      loop {

        reactWithin(0) {

          case Abort => action(Abort)
          case TIMEOUT => react { case msg => action(msg) }

        }
      }
    }

    protected def action(msg: Any) {

      msg match {

        case Abort => exit
        case message: FlushMessage =>
          val table = message.name

          try {

            lock.acquire

            if(isPersistence)
              flush(table)

          } finally {

            lock.release

          }

        case message: NotifyMessage =>
          val table = message.name

          try {

            lock.acquire

            if(subscriptions.contains(table))
              subscriptions(table).foreach(consumer => consumer ! message)

          } finally {

            lock.release

          }

        case message: PublishMessage =>
          val table = message.name
          val bytes = message.bytes

          try {

            lock.acquire

            if(path != null)
              upsert(table, Table.fromBytes(bytes))

            if(subscriptions.contains(table))
              subscriptions(table).foreach(consumer => consumer ! message)

          } finally {

            lock.release

          }

          if(interval > 0)
            Thread.sleep(interval)

      }
    }
  }

  protected def isPersistence = path != null

}

/** Companion.
 *
 * @author myst3r10n
 */
object PublishingHandler {

  /** A thread to publish data. */
  val broadcaster = new ChannelLocal[Actor]

}

