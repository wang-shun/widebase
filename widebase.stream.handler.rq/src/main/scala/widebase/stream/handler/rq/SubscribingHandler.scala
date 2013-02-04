package widebase.stream.handler.rq

import com.twitter.util.Eval

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

import scala.actors.Actor. { actor, react, reply }
import scala.concurrent.Lock
import scala.collection.mutable. { ArrayBuffer, Map }

import widebase.stream.codec. { BadMessage, DoneMessage }

import widebase.stream.codec.rq. {

  MessageType,
  RollbackMessage,
  SubscribeMessage,
  TableMessage,
  UnparsableMessage,
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

  private val eval = new Eval

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

        var selector: Selector = null

        if(!message.selector.isEmpty) {

          if(!AuthHandler.hasAuthorization(
            evt.getChannel,
            "SelectorSupport"))
            return

          val originPolicy = System.getSecurityManager

          try {

            val parser = actor {

              System.setSecurityManager(new SelectorPolicy)

              react {

                case Evaluation =>

                  try {

                    selector = eval[Selector](
                      "import widebase.db.table.Table\n" +
                      "import widebase.stream.handler.rq.Selector\n" +
                      "\n" +
                      "new Selector {\n" +
                      "\n" +
                      "  override def apply(table: Table) = table.filter(record => " +
                        message.selector/*.replace("{", "").replace("}", "")*/ + ")\n" +
                      "\n" +
                      "}")

                    reply(true)

                  } catch {

                    case e: Exception => reply(e)

                  }
              }
            }

            val parsed = parser !? (5000, Evaluation)

            if(parsed == None)
              throw new Exception("Evaluation timeout: Selector")
            else if(parsed.get.isInstanceOf[Exception])
              throw parsed.get.asInstanceOf[Exception]

          } catch {

            case e: Exception =>
              error("Unparsable selector: " + e)
              evt.getChannel.write(new UnparsableMessage(e.getMessage))
              return

          } finally { System.setSecurityManager(originPolicy) }
        }

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

