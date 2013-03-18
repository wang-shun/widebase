package widebase.ui.ide.chart

import de.sciss.scalainterpreter.CodePane

import java.awt.BorderLayout

import scala.swing. {

  BorderPanel,
  Button,
  ComboBox,
  FlowPanel,
  Window

}

import scala.swing.event.ButtonClicked
/*
object Dialog {

  def showChart(parent: Component) {

    val dialog = new scala.swing.Dialog(parent)

    dialog.modal = true

    val editor = CodePane(CodePane.Config())

    dialog.contents = new BorderPanel {

      val charts = Seq("Candle", "Highlow", "Plot", "Scatter")

      add(new ComboBox(charts), BorderPanel.Position.North)

      peer.add(editor.component, BorderLayout.CENTER)

      val ok = new Button("OK") {

        listenTo(this)

        reactions += {

          case ButtonClicked(_) => ChartDialog.this.dispose

        }
      }

      val cancel = new Button("Cancel") {

        listenTo(this)

        reactions += {

          case ButtonClicked(_) => ChartDialog.this.dispose

        }
      }

      add(new FlowPanel(ok, cancel), BorderPanel.Position.South)

    }

    dialog.pack

    dialog.location = new Point(
      owner.location.x + (owner.size.width - dialog.size.width) / 2,
      owner.location.y + (owner.size.height - dialog.size.height) / 2)

    dialog.visible = true

  }

  val editor = CodePane(CodePane.Config())

  modal = true

}
*/

