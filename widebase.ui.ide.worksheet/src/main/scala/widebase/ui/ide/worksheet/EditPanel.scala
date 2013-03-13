package widebase.ui.ide.worksheet

import de.sciss.scalainterpreter. { CodePane, Interpreter }

import java.awt.BorderLayout
import java.awt.event. { InputEvent, KeyEvent }

import java.io. {

  BufferedReader,
  BufferedWriter,
  DataInputStream,
  DataOutputStream,
  File,
  FileInputStream,
  FileOutputStream,
  InputStreamReader,
  OutputStreamWriter

}

import javax.swing.KeyStroke
import javax.swing.filechooser.FileNameExtensionFilter

import moreswing.swing.i18n.LFrame

import scala.swing. {

  BorderPanel,
  Component,
  Dialog,
  Dimension,
  FileChooser,
  Publisher,
  ScrollPane

}

import widebase.ui.ide.event._

/** Pane of edit.
 * 
 * @author myst3r10n
 */
class EditPanel extends BorderPanel with Publisher {

  var currentFile: File = null

  val toolBar = new EditToolBar
  val codeCfg = CodePane.Config()

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_O,
    InputEvent.CTRL_MASK) -> { () =>

    EditPanel.this.publish(FileOpen)

  }

  for(i <- 0 to 8)
    codeCfg.keyMap += KeyStroke.getKeyStroke(
      KeyEvent.VK_1 + i,
      InputEvent.ALT_MASK) -> { () =>

      EditPanel.this.publish(SelectPage(i))

    }

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_0,
    InputEvent.ALT_MASK) -> { () =>

    EditPanel.this.publish(SelectPage(9))

  }

  codeCfg.keyMap += KeyStroke.getKeyStroke(
    KeyEvent.VK_ENTER,
    InputEvent.SHIFT_MASK) -> { () =>

    codePane.getSelectedTextOrCurrentLine.foreach(interpret)

  }

  codeCfg.keyMap += KeyStroke.getKeyStroke("F9") -> { () =>

    Some(codePane.editor.getText).foreach(interpret)

  }

  val codePane = CodePane(codeCfg)

  def interpret(code: String) {


    EditPanel.interpreter.interpret(code)

  }

  peer.add(toolBar, BorderLayout.NORTH)

  add(
    new Component { override lazy val peer = codePane.component },
    BorderPanel.Position.Center)

  val chooser = new FileChooser(new File(System.getProperty("user.dir"))) {

    fileFilter = new FileNameExtensionFilter("Scala (*.scala)", "scala")

  }

  listenTo(this, toolBar)

  reactions += {

    case EditCopy => codePane.editor.copy
    case EditCut => codePane.editor.cut
    case EditPaste => codePane.editor.paste
    case FileOpen =>

      val result = chooser.showOpenDialog(this)

      if(result == FileChooser.Result.Approve) {

        val code = load(chooser.selectedFile)

        if(code != null) {

          codePane.editor.setText(code)
          codePane.editor.setCaretPosition(0)
          currentFile = chooser.selectedFile
          EditPanel.this.publish(RenameTab(currentFile.getName))

        }
      }

    case FileSave =>

      if(currentFile == null)
        saveAs
      else
        save(currentFile)

    case FileSaveAs => saveAs

    case InterpretContent => Some(codePane.editor.getText).foreach(interpret)
    case InterpretSelection => codePane.getSelectedTextOrCurrentLine.foreach(interpret)

  }

  protected def load(file: File): String = {

    var code = ""
    var reader: BufferedReader = null

    try {

      reader = new BufferedReader(new InputStreamReader(
        new DataInputStream(new FileInputStream(file))))

      var line = reader.readLine

      while(line != null) {

        if(!code.isEmpty)
          code += System.getProperty("line.separator")
        code += line
        line = reader.readLine

      }

      return code

    } catch {

      case e: Exception =>
        Dialog.showMessage(this, "Open failed\n" + e.getMessage)

    } finally {

      if(reader != null)
        reader.close

    }

    null

  }

  protected def save(file: File) {

    var writer: BufferedWriter = null

    try {

      writer = new BufferedWriter(new OutputStreamWriter(
        new DataOutputStream(new FileOutputStream(file))))

      writer.write(codePane.editor.getText)
      currentFile = file
      EditPanel.this.publish(RenameTab(currentFile.getName))

    } catch {

      case e: Exception =>
        Dialog.showMessage(this, "Save failed\n" + e.getMessage)

    } finally {

      if(writer != null)
        writer.close

    }
  }

  protected def saveAs {

    val result = chooser.showSaveDialog(this)

    if(result == FileChooser.Result.Approve) {

      val file =
        if(chooser.fileFilter.getDescription.contains("*.scala") &&
          !chooser.selectedFile.getPath.endsWith(".scala"))
          new File(chooser.selectedFile.getPath + ".scala")
        else
          chooser.selectedFile

      val confResult =
        if(file.exists)
          Dialog.showConfirmation(
            this,
            file.getName + " override?",
            "File",
            Dialog.Options.YesNo)
        else
          Dialog.Result.Yes

      if(confResult == Dialog.Result.Yes)
        save(file)

    }
  }
}

object EditPanel {

  var interpreter: Interpreter = null

  def load(file: File): String = {

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

      return code

    } catch {

      case e: Exception =>
        Dialog.showMessage(null, "Open failed\n" + e.getMessage)

    } finally {

      if(reader != null)
        reader.close

    }

    null

  }
}

