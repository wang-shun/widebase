package widebase.ui.swing

import java.sql.Timestamp
import java.util.Vector

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

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

/** A table file and directory table compatible `DefaultTableModel`.
 *
 * @param table of model
 *
 * @author myst3r10n
 */
case class TableModel(val table: Table) extends TableModelLike {

  import scala.collection.JavaConverters._

  val tables = null

  protected val columns = table.columns.toBuffer // Performance purpose

  override def getColumnCount = table.columns.size
  override def getColumnName(column: Int) = table.labels(column).toString
  override def getRowCount = table.records.length

  override def getValueAt(row: Int, column: Int) = {

    val value = columns.toBuffer(column)(row)

    if(value.isInstanceOf[Symbol])
      value.asInstanceOf[Symbol].toString.drop(1).asInstanceOf[Object]
    else
      value.asInstanceOf[Object]

  }

  override def insertRow(row: Int, rowData: Vector[_]) {

    if(row < getRowCount)
      table.insert(row, rowData.asScala:_*)
    else
      table += (rowData.asScala:_*)

    fireTableRowsInserted(row, row)

  }

  override def removeRow(row: Int) {

    columns.foreach(_.remove(row))
    fireTableRowsDeleted(row, row)

  }

  override def setValueAt(value: Object, row: Int, column: Int) {

    columns(column) match {

      case column: BoolColumn => column(row) = java.lang.Boolean.valueOf(value.toString)
      case column: ByteColumn => column(row) = java.lang.Byte.valueOf(value.toString)
      case column: CharColumn => column(row) = value.asInstanceOf[String].head
      case column: DoubleColumn => column(row) = java.lang.Double.parseDouble(value.asInstanceOf[String])
      case column: FloatColumn => column(row) = java.lang.Float.parseFloat(value.asInstanceOf[String])
      case column: IntColumn => column(row) = java.lang.Integer.parseInt(value.asInstanceOf[String])
      case column: LongColumn => column(row) = java.lang.Long.parseLong(value.asInstanceOf[String])
      case column: ShortColumn => column(row) = java.lang.Short.parseShort(value.asInstanceOf[String])
      case column: MonthColumn => column(row) = YearMonth.parse(value.asInstanceOf[String])
      case column: DateColumn => column(row) = LocalDate.parse(value.asInstanceOf[String])
      case column: MinuteColumn => column(row) = Minutes.parseMinutes(value.asInstanceOf[String])
      case column: SecondColumn => column(row) = Seconds.parseSeconds(value.asInstanceOf[String])
      case column: TimeColumn => column(row) = LocalTime.parse(value.asInstanceOf[String])
      case column: DateTimeColumn => column(row) = LocalDateTime.parse(value.asInstanceOf[String])
      case column: TimestampColumn => column(row) = Timestamp.valueOf(value.asInstanceOf[String])
      case column: SymbolColumn => column(row) = Symbol(value.asInstanceOf[String])
      case column: StringColumn => column(row) = value.asInstanceOf[String]

    }
  }
}

