package widebase.workspace

import java.awt.event.ActionEvent

import java.io. {

  BufferedReader,
  DataInputStream,
  File,
  FileInputStream,
  InputStreamReader

}

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

  def load(file: File): Option[String] = {

    var reader: BufferedReader = null

    try {

      reader = new BufferedReader(new InputStreamReader(
        new DataInputStream(new FileInputStream(file))))

      var code = ""
      var line = reader.readLine

      while(line != null) {

        if(!code.isEmpty)
          code += System.getProperty("line.separator")
        code += line
        line = reader.readLine

      }

      Some(code)

    } finally {

      if(reader != null)
        reader.close

    }
  }}

