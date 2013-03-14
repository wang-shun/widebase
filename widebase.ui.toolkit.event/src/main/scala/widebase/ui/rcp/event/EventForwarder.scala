package widebase.ui.toolkit.event

import scala.collection.mutable.LinkedHashMap
import scala.swing. { Button, Publisher, Separator }
import scala.swing.event.ButtonClicked

/** A event forwarder.
 *
 * @param publisher forward events
 * @param items of `Publisher` compatible components
 *
 * @author myst3r10n
 */
object EventForwarder {

  def apply(publisher: Publisher, items: LinkedHashMap[String, Any]) {

    items.values.foreach { item =>

      if(item.isInstanceOf[Button]) {

        val button = item.asInstanceOf[Button with EventForwarding]

        button.listenTo(button)

        button.reactions += { case ButtonClicked(_) =>

          publisher.publish(button.publishEvent)

        }
      }
    }
  }
}

