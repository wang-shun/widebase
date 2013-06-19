package widebase.ui.swing

import event. {

  TableAddRecord,
  TableInsertRecord,
  TableRemoveRecord

}

import java.sql.Timestamp

import javax.swing.JOptionPane
import javax.swing.table.DefaultTableModel

import moreswing.swing.i18n.LocaleManager

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.swing.ScrollPane

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

import widebase.db.table.Table

/** Pane of table.
 *
 * @param model0 of table
 *
 * @author myst3r10n
 */
case class TablePane(model0: TableModelLike = null) extends ScrollPane {

  def this(table: Table) = this(TableModel(table))
  def this(tables: Array[Table]) = this(TableModelParted(tables))

  val table = new scala.swing.Table

  if(model0 != null)
    table.model = model0

  table.peer.setColumnSelectionAllowed(true)

  contents = table

  reactions += {

    case TableAddRecord =>

      if(table.model.isInstanceOf[TableModel])
        table.model.asInstanceOf[TableModel].addRow(blankRecord)
      else
        table.model.asInstanceOf[TableModelParted].addRow(blankRecord)

    case TableInsertRecord =>

      if(table.model.isInstanceOf[TableModel]) {

        if(table.peer.getSelectedRow != -1)
          try {

            table.model.asInstanceOf[TableModel].insertRow(
              table.peer.getSelectedRow, blankRecord)

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

      if(table.model.isInstanceOf[TableModel])
        try {

          for(i <- 0 to table.peer.getSelectedRowCount - 1)
            table.model.asInstanceOf[DefaultTableModel]
              .removeRow(table.peer.getSelectedRow)

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

  /** Blank record build on head record.
   *
   * @return blank record
   */
  protected def blankRecord = {

    val columns =
      if(table.model.isInstanceOf[TableModel])
        table.model.asInstanceOf[TableModel].table.columns.toBuffer
      else
        table.model.asInstanceOf[TableModelParted].tables.head.columns.toBuffer

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

