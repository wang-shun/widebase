package widebase.stream.handler.rq

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

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import scala.actors. { Actor, TIMEOUT }

import widebase.db.table.Table

import widebase.stream.codec. {

  DoneMessage,
  ForbiddenMessage,
  LoginGrantedMessage,
  LoginFailedMessage,
  LoginRequiredMessage,
  UnauthorizedMessage
}

import widebase.stream.codec.rq. {

  EventMessage,
  RollbackMessage,
  TableMessage,
  UnparsableMessage

}

/** Handles consumer.
 *
 * @param listener of consumer
 *
 * @author myst3r10n
 */
class ConsumerHandler(listener: RecordListener)
  extends SimpleChannelUpstreamHandler
  with Logger {

  /** Read messages. */
  protected var reader: Reader = _

  override def channelClosed(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    if(reader != null)
      reader ! Abort

  }

  override def channelOpen(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    reader = new Reader
    reader.start

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

      case message: DoneMessage => Channels.fireMessageReceived(ctx, message)
      case message: ForbiddenMessage => Channels.fireMessageReceived(ctx, message)
      case message: LoginGrantedMessage => Channels.fireMessageReceived(ctx, message)
      case message: LoginFailedMessage => Channels.fireMessageReceived(ctx, message)
      case message: LoginRequiredMessage => Channels.fireMessageReceived(ctx, message)
      case message: EventMessage => reader! message
      case message: RollbackMessage => reader ! message
      case message: TableMessage => reader ! message
      case message: UnparsableMessage => reader ! message
      case message: UnauthorizedMessage =>  Channels.fireMessageReceived(ctx, message)
      case message => error("Unfamiliar with message: " + message)

    }
  }

  protected class Reader extends Actor {

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
        case message: EventMessage =>
          try {

            listener.react(message.text)

          } catch {

            case e: Throwable => e.printStackTrace

          }

        case message: RollbackMessage =>
          try {

            listener.react(message.records, message.partition)

          } catch {

            case e: Throwable => e.printStackTrace

          }

        case message: TableMessage =>
          try {

            listener.react(Table.fromBytes(message.bytes))

          } catch {

            case e: Throwable => e.printStackTrace

          }

        case message: UnparsableMessage =>
          try {

            listener.react(message)

          } catch {

            case e: Throwable => e.printStackTrace

          }
      }
    }
  }
}

