package widebase.io.csv.test

import java.io.File

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

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

object Import extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._
  import widebase.io.csv
  import widebase.io.csv.filter

  val debug = false

  def main(args: Array[String]) {

    columns
    println("")
    table
    println("")
    tableTo

  }

  def columns {

    val table = Table(
      StringColumn(
        "Bool",
        "Byte",
        "Char",
        "Double",
        "Float",
        "Int",
        "Long",
        "Short",
        "Month",
        "Date",
        "Minute",
        "Second",
        "Time",
        "DateTime",
        "Timestamp",
        "Symbol",
        "String"))

    val columns = csv.columns(
      "bxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      System.getProperty("user.dir") + "/usr/csv/columns.csv.gz",
      "g")

    var i = 0

    table.labels.foreach { label =>

      table(label) = columns(i)

      i += 1

    }

    save("columns", table)

    columns.foreach(column => column.clear)
    table.columns.foreach(column => column.clear)

    val loaded = load("columns")

    var r = 0

    loaded.records.foreach { record =>

      r += 1

      if(debug || r > loaded.records.length - 4)
        println(record)
      else
        record

    }

    loaded.columns.foreach(column => column.clear)

  }

  def table {

    val table = csv.table(
      "bxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      System.getProperty("user.dir") + "/usr/csv/table.csv.gz",
      "g")

    save("table", table)

    table.columns.foreach(column => column.clear)

    val loaded = load("table")

    var r = 0

    loaded.records.foreach { record =>

      r += 1

      if(debug || r > loaded.records.length - 4)
        println(record)
      else
        record

    }

    loaded.columns.foreach(column => column.clear)

  }

  def tableTo {

    csv.table.to(
      dbi.path,
      "table",
      "DbxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      System.getProperty("user.dir") + "/usr/csv/partitionedTable.csv.gz",
      "g")('daily)

    val partitions = map.dates(
      "table",
      LocalDate.parse("2012-01-24", DateTimeFormat.forPattern("yyyy-MM-dd")),
      LocalDate.parse("2012-01-31", DateTimeFormat.forPattern("yyyy-MM-dd")))

    partitions.tables.foreach { table =>

      var r = 0

      table.records.foreach { record =>

        r += 1

        if(debug || r > table.records.length - 4)
          println(record)
        else
          record

      }
    }
  }
}

