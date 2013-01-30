package widebase.stream.handler.rq

import org.jboss.netty.channel.Channel

import scala.actors. { Actor, TIMEOUT }

import widebase.stream.codec.rq. {

  EventMessage,
  NotifyMessage,
  PublishMessage,
  RollbackMessage,
  TableMessage

}

/** Actor to write consumer.
 *
 * @author myst3r10n
 */
class ConsumerWriter(channel: Channel, selector: String) extends Actor {

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
        if(!selector.isEmpty) {

          // selector code to filter subscribed data...

        }

        channel.write(new TableMessage(message.bytes))

      case message: RollbackMessage => channel.write(message)

    }
  }
}

