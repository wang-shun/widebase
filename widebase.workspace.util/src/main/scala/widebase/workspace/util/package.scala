package widebase.workspace

import java.awt.event.ActionEvent

import javax.swing. { AbstractAction, JComponent, KeyStroke }

import scala.swing.Component

/** Util package.
 *
 * @author myst3r10n
 */
package object util {

  def bind(component: Component, keys: KeyStroke, id: String, f: () => Unit) {

    component.peer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keys, id)

    component.peer.getActionMap.put(
      id,
      new AbstractAction(id) {

        def actionPerformed(event: ActionEvent) {

          f()

        }
      }
    )
  }
}

