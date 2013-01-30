package widebase.db.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { DateTimeConstants, LocalDate }

import widebase.db.column. { DateColumn, IntColumn, StringColumn }

object Filter extends Logger with Loggable {

  // Init API
  import widebase.db.table.Table

  val debug = false
  val records = 250000

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    columnFilter

    recordFilter

  }

  def columnFilter {

    var started = 0L

    val symbolCol = new StringColumn
    val symbols = Array("A", "B", "C")

    started = System.currentTimeMillis
    for(i <- 0 to records - 1)
      symbolCol += symbols(i % symbols.size)
    info("Column filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val filteredCol = symbolCol.filter(value => value == "A")
    info("Column filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    if(debug)
      filteredCol.foreach(println(_))

  }

  def recordFilter {

    var started = 0L

    val today = LocalDate.now

    val table = Table(
      StringColumn("date", "price"),
      new DateColumn,
      new IntColumn)

    val dateCol = table("date").asInstanceOf[DateColumn]
    val priceCol = table("price").asInstanceOf[IntColumn]

    started = System.currentTimeMillis
    for(i <- 0 to records - 1) {

      dateCol += today.minusDays(records - 1 - i)
      priceCol += i + 1

    }
    info("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    val days = Array(
      DateTimeConstants.MONDAY,
      DateTimeConstants.TUESDAY,
      DateTimeConstants.WEDNESDAY,
      DateTimeConstants.THURSDAY,
      DateTimeConstants.FRIDAY)

    started = System.currentTimeMillis
    val filteredTable = table.filter(record =>
      days.contains(record("date").asInstanceOf[LocalDate].getDayOfWeek))
    info("Table filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    if(debug)
      table.records.foreach(println(_))

  }
}

