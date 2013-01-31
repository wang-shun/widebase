package widebase.db.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { DateTimeConstants, LocalDate }

import widebase.db.column. { DateColumn, IntColumn, StringColumn }
import widebase.db.table. { Table, TemplateTable }

/* Test of filter and filterNot.
 *
 * @author myst3r10n
 */
object Filter extends Logger with Loggable {

  val debug = false
  val records = 250000

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    columnFilter

    recordFilter

    templateFilter

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
      filteredTable.records.foreach(println(_))

  }

  def templateFilter {

    case class MyData(val date: LocalDate, val price: Int)

    case class MyTable(
      table: Table = Table(
        StringColumn(
          "date",
          "price"),
        DateColumn(),
        IntColumn()))
      extends TemplateTable[MyData] {

      val date = table("date").asInstanceOf[DateColumn]
      val price = table("price").asInstanceOf[IntColumn]

      def +=(data: MyData) = {

        date += data.date
        price += data.price

        this

      }

      def +=(
        date: LocalDate,
        price: Int): MyTable =
        this += MyData(date, price)

      def ++=(table: MyTable) = {

        for(r <- 0 to table.records.length - 1)
          this += table(r)

        this

      }

      def apply(index: Int) = MyData(date(index), price(index))

      def filter(predicate: MyData => Boolean) = {

        val filteredTable = new MyTable

        for(r <- 0 to records.length - 1)
          if(predicate(this(r)))
            filteredTable += this(r)

        filteredTable

      }

      def peer = table

    }

    var started = 0L

    val today = LocalDate.now

    val table = new MyTable

    started = System.currentTimeMillis
    for(i <- 0 to records - 1) {

      table.date += today.minusDays(records - 1 - i)
      table.price += i + 1

    }
    info("Temaplte filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    val days = Array(
      DateTimeConstants.MONDAY,
      DateTimeConstants.TUESDAY,
      DateTimeConstants.WEDNESDAY,
      DateTimeConstants.THURSDAY,
      DateTimeConstants.FRIDAY)

    started = System.currentTimeMillis
    val filteredTemplate = table.filter(data =>
      days.contains(data.date.getDayOfWeek))
    info("Template filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    if(debug)
      filteredTemplate.records.foreach(println(_))

  }
}

