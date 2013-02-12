package widebase.ui.table

import java.sql.Timestamp
import java.util.Vector

import javax.swing.table.DefaultTableModel

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable.ArrayBuffer

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

/** A partitioned table compatible `DefaultTableModel`.
 *
 * @param tables of model
 *
 * @author myst3r10n
 */
case class TableModelParted(val tables: Array[Table]) extends DefaultTableModel {

  import scala.collection.JavaConverters._

  protected val columns = tables.head.columns.toArray
  protected val parts = ArrayBuffer[(Int, Int, Int)]()

  {

    var offset = 0

    for(i <- 0 to tables.size - 1) {

      if(i == 0)
        parts += ((0, tables(i).records.length - 1, 0))
      else
        parts += ((offset, offset + tables(i).records.length - 1, i))

      offset += tables(i).records.length

    }
  }

  override def getColumnCount = tables.head.columns.size
  override def getColumnName(column: Int) = tables.head.labels(column).toString
  override def getRowCount = {

    var records = 0

    tables.foreach(records += _.records.length)

    records

  }

  override def getValueAt(row: Int, column: Int) = {

    val part = parts.indexWhere {
      case (min, max, record) => min <= row && row <= max }

    val value = tables(part).columns.toBuffer(column)(row - parts(part)._1)

    if(value.isInstanceOf[Symbol])
      value.asInstanceOf[Symbol].toString.drop(1).asInstanceOf[Object]
    else
      value.asInstanceOf[Object]

  }

   override def insertRow(row: Int, rowData: Vector[_]) {

    if(row == getRowCount) {

      tables.last += (rowData.asScala:_*)
      parts(parts.size - 1) = ((parts.last._1, parts.last._2 + 1, parts.last._3))

      fireTableRowsInserted(row, row)

    }
  }

  override def setValueAt(value: Object, row: Int, column: Int) {

    val part = parts.indexWhere {
      case (min, max, record) => min <= row && row <= max }

    val record = row - parts(part)._1

    tables(part).columns.toBuffer(column) match {

      case column: BoolColumn => column(record) = java.lang.Boolean.valueOf(value.toString)
      case column: ByteColumn => column(record) = java.lang.Byte.valueOf(value.toString)
      case column: CharColumn => column(record) = value.asInstanceOf[String].head
      case column: DoubleColumn => column(record) = java.lang.Double.parseDouble(value.asInstanceOf[String])
      case column: FloatColumn => column(record) = java.lang.Float.parseFloat(value.asInstanceOf[String])
      case column: IntColumn => column(record) = java.lang.Integer.parseInt(value.asInstanceOf[String])
      case column: LongColumn => column(record) = java.lang.Long.parseLong(value.asInstanceOf[String])
      case column: ShortColumn => column(record) = java.lang.Short.parseShort(value.asInstanceOf[String])
      case column: MonthColumn => column(record) = YearMonth.parse(value.asInstanceOf[String])
      case column: DateColumn => column(record) = LocalDate.parse(value.asInstanceOf[String])
      case column: MinuteColumn => column(record) = Minutes.parseMinutes(value.asInstanceOf[String])
      case column: SecondColumn => column(record) = Seconds.parseSeconds(value.asInstanceOf[String])
      case column: TimeColumn => column(record) = LocalTime.parse(value.asInstanceOf[String])

      case column: DateTimeColumn => column(record) = LocalDateTime.parse(value.asInstanceOf[String])

      case column: TimestampColumn => column(record) = Timestamp.valueOf(value.asInstanceOf[String])
      case column: SymbolColumn => column(record) = Symbol(value.asInstanceOf[String])
      case column: StringColumn => column(record) = value.asInstanceOf[String]

    }
  }
}

