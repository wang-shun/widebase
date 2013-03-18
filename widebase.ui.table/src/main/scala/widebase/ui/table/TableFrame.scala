package widebase.ui.table

import event. { TableAddRecord, TableInsertRecord, TableRemoveRecord }

import java.awt.BorderLayout
import java.sql.Timestamp

import javax.swing.JOptionPane
import javax.swing.table.DefaultTableModel

import moreswing.swing.i18n. { LFrame, LocaleManager }

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.swing. { BorderPanel, Component, Publisher, ScrollPane }

import widebase.db.column. {

  BoolColumn,
  ByteColumn,
  CharColumn,
  DoubleColumn,
  FloatColumn,
  IntColumn,
  LongColumn,
  ShortColumn,
  MonthColumn,
  DateColumn,
  MinuteColumn,
  SecondColumn,
  TimeColumn,
  DateTimeColumn,
  TimestampColumn,
  SymbolColumn,
  StringColumn

}

/** Frame of table.
 * 
 * @param panel0 of table
 *
 * @author myst3r10n
 */
class TableFrame(protected var panel0: TablePanel = null) extends LFrame {

  val toolBar = new TableToolBar
  val scrollPane = new ScrollPane

  if(panel0 != null)
    panel = panel0

  def panel = panel0

  def panel_=(panel: TablePanel) {

    scrollPane.contents = new Component {

      deafTo(panel)
      listenTo(panel)

      override lazy val peer = panel.peer

    }

    panel0 = panel

  }

  contents = new scala.swing.BorderPanel {

    peer.add(toolBar, BorderLayout.NORTH)
    add(scrollPane, BorderPanel.Position.Center)

    listenTo(toolBar)

    reactions += {

      case TableAddRecord =>

        if(panel.model.isInstanceOf[TableModel])
          panel.model.asInstanceOf[TableModel].addRow(blankRecord)
        else
          panel.model.asInstanceOf[TableModelParted].addRow(blankRecord)

      case TableInsertRecord =>

        if(panel.model.isInstanceOf[TableModel]) {

          if(panel.peer.getSelectedRow != -1)
            try {

              panel.model.asInstanceOf[TableModel].insertRow(
                panel.peer.getSelectedRow, blankRecord)

            } catch {

              case e: UnsupportedOperationException =>
                JOptionPane.showMessageDialog(
                  peer,
                  LocaleManager.text("Mapped_table_not_support_insert_record"),
                  LocaleManager.text("Exception"),
                  JOptionPane.ERROR_MESSAGE);

            }
          else
            JOptionPane.showMessageDialog(
              peer,
              LocaleManager.text("Table_not_selected"),
              LocaleManager.text("Insert"),
              JOptionPane.ERROR_MESSAGE);

        } else
          JOptionPane.showMessageDialog(
            peer,
            LocaleManager.text("Mapped_table_not_support_insert_record"),
            LocaleManager.text("Exception"),
            JOptionPane.ERROR_MESSAGE);

      case TableRemoveRecord =>

        if(panel.model.isInstanceOf[TableModel])
          try {

            for(i <- 0 to panel.peer.getSelectedRowCount - 1)
              panel.model.asInstanceOf[DefaultTableModel]
                .removeRow(panel.peer.getSelectedRow)

          } catch {

            case e: UnsupportedOperationException =>
              JOptionPane.showMessageDialog(
                peer,
                LocaleManager.text("Mapped_table_not_support_removable_records"),
                LocaleManager.text("Exception"),
                JOptionPane.ERROR_MESSAGE);

          }
        else
          JOptionPane.showMessageDialog(
            peer,
            LocaleManager.text("Mapped_table_not_support_removable_records"),
            LocaleManager.text("Exception"),
            JOptionPane.ERROR_MESSAGE);

    }
  }

  /** Blank record build on head record.
   *
   * @return blank record
   */
  protected def blankRecord = {

    val columns =
      if(panel.model.isInstanceOf[TableModel])
        panel.model.asInstanceOf[TableModel].table.columns.toBuffer
      else
        panel.model.asInstanceOf[TableModelParted].tables.head.columns.toBuffer

    val record =
      for(i <- 0 to columns.size - 1)
        yield {

          columns(i) match {

            case column: BoolColumn => false
            case column: ByteColumn => 0: Byte
            case column: CharColumn => 0: Char
            case column: DoubleColumn => 0d
            case column: FloatColumn => 0f
            case column: IntColumn => 0
            case column: LongColumn => 0L
            case column: ShortColumn => 0: Short
            case column: MonthColumn => new YearMonth
            case column: DateColumn => new LocalDate
            case column: MinuteColumn => Minutes.ZERO
            case column: SecondColumn => Seconds.ZERO
            case column: TimeColumn => new LocalTime
            case column: DateTimeColumn => new LocalDateTime
            case column: TimestampColumn => new Timestamp(0)
            case column: SymbolColumn => Symbol("")
            case column: StringColumn => ""

          }
        }

    record.toArray.asInstanceOf[Array[Object]]

  }
}

