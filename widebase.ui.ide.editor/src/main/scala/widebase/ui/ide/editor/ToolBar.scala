package widebase.ui.ide.editor

import event.EditRename

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

import java.util.UUID

import javax.swing.ImageIcon
import javax.swing.filechooser.FileNameExtensionFilter

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Dialog, FileChooser, Separator }

import widebase.ui.toolkit.Action

/** Tool bar of frame.
 *
 * @author myst3r10n
 */
class ToolBar(edit: EditPanel) extends widebase.ui.toolkit.ToolBar {

  import widebase.ui.toolkit.runtime

  peer.setFloatable(false)

  val chooser = new FileChooser(new File(System.getProperty("user.dir"))) {

    fileFilter = new FileNameExtensionFilter("Scala (*.scala)", "scala")

  }

  this += "Open" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/document-open.png"))
      toolTip = LocaleManager.text("File_Open")

      def apply {

        val result = chooser.showOpenDialog(edit)

        if(result == FileChooser.Result.Approve) {

          val code = load(chooser.selectedFile)

          if(code != null) {

            edit.codePane.editor.setText(code)
            edit.codePane.editor.setCaretPosition(0)
            edit.currentFile = chooser.selectedFile
            edit.publish(EditRename)

          }
        }
      }
    }
  )

  this += "Save" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/document-save.png"))
      toolTip = LocaleManager.text("File_save")

      def apply {

        if(edit.currentFile == null)
          saveAs
        else
          save(edit.currentFile)


      }
    }
  )

  this += "SaveAs" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/document-save-as.png"))
      toolTip = LocaleManager.text("File_save_as")

      def apply {

        saveAs

      }
    }
  )

  this += UUID.randomUUID.toString -> new Separator

  this += "Cut" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/edit-cut.png"))
      toolTip = LocaleManager.text("Edit_cut")

      def apply {

        edit.codePane.editor.cut

      }
    }
  )

  this += "Copy" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/edit-copy.png"))
      toolTip = LocaleManager.text("Edit_copy")

      def apply {

        edit.codePane.editor.copy

      }
    }
  )

  this += "Paste" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/edit-paste.png"))
      toolTip = LocaleManager.text("Edit_paste")

      def apply {

        edit.codePane.editor.paste

      }
    }
  )

  this += UUID.randomUUID.toString -> new Separator

  this += "Content" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/player_play.png"))
      toolTip = LocaleManager.text("Interpret_content")

      def apply {

        runtime.queue.add(Some(edit.codePane.editor.getText))

      }
    }
  )

  this += "Selection" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/player_playselection.png"))
      toolTip = LocaleManager.text("Interpret_selection")

      def apply {

        runtime.queue.add(edit.codePane.getSelectedTextOrCurrentLine)

      }
    }
  )

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

      writer.write(edit.codePane.editor.getText)
      edit.currentFile = file
      edit.publish(EditRename)

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

