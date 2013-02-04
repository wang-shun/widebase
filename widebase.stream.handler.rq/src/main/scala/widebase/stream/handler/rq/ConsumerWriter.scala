package widebase.stream.handler.rq

import net.liftweb.common.Logger

import org.jboss.netty.channel.Channel

import scala.actors. { Actor, TIMEOUT }

import widebase.db.table.Table

import widebase.stream.codec.rq. {

  EventMessage,
  NotifyMessage,
  PublishMessage,
  RollbackMessage,
  TableMessage,
  UnparsableMessage

}

/** Actor to write consumer.
 *
 * @author myst3r10n
 */
class ConsumerWriter(
  channel: Channel,
  selector: Selector = null)
  extends Actor
  with Logger {

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

      case message: NotifyMessage => channel.write(new EventMessage(message.text))
      case message: PublishMessage =>

        if(selector == null)
          channel.write(new TableMessage(message.bytes))
        else {

          val table = Table.fromBytes(message.bytes)

          var filteredTable: Table = null

          val filter = scala.actors.Actor.actor {

            System.setSecurityManager(new SelectorPolicy)

            scala.actors.Actor.react {

              case Filter =>

                try {

                  filteredTable = selector(table)

                  scala.actors.Actor.reply(true)

                } catch {

                  case e: Exception => scala.actors.Actor.reply(e)

                }

            }
          }

          val filtered = filter !? (Filter)

          if(filtered.isInstanceOf[Exception]) {

            val e = filtered.asInstanceOf[Exception]

            channel.write(new UnparsableMessage(e.toString))
            throw e

          } else if(!filteredTable.records.isEmpty)
            channel.write(new TableMessage(filteredTable.toBytes()))

        }

      case message: RollbackMessage => channel.write(message)

    }
  }
}

