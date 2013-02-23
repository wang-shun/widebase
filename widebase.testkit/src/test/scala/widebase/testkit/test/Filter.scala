package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { DateTimeConstants, LocalDate }

import widebase.db.table. { Table, TemplateTable }

/* Test of filter and filterNot.
 *
 * @author myst3r10n
 */
object Filter extends Logger with Loggable {

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

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

    val symbolCol = string()
    val symbols = Array("A", "B", "C")

    started = System.currentTimeMillis
    for(i <- 0 to records - 1)
      symbolCol += symbols(i % symbols.size)
    println("Column filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val filteredCol = symbolCol.filter(value => value == "A")
    println("Column filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    filteredCol.foreach(value =>
      assert(value == "A", error("Value unexpected: " + value)))

  }

  def recordFilter {

    var started = 0L

    val today = LocalDate.now

    val table = Table(string("date", "price"), date(), int())

    val dateCol = table("date").D
    val priceCol = table("price").i

    started = System.currentTimeMillis
    for(i <- 0 to records - 1) {

      dateCol += today.minusDays(records - 1 - i)
      priceCol += i + 1

    }
    println("Table filled " + records + " records in " +
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
    println("Table filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    filteredTable.records.foreach(record =>
      assert(
        days.contains(record("date").asInstanceOf[LocalDate].getDayOfWeek),
        error("Value unexpected: " + record("date").asInstanceOf[LocalDate].getDayOfWeek)))

  }

  def templateFilter {

    case class MyData(val date: LocalDate, val price: Int)

    case class MyTable(
      table: Table = Table(string("date", "price"), date(), int()))
      extends TemplateTable[MyData] {

      val date = table("date").D
      val price = table("price").i

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
    println("Temaplte filled " + records + " records in " +
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
    println("Template filtered " + records + " records in " +
      diff(started, System.currentTimeMillis))

    filteredTemplate.foreach(record =>
      assert(
        days.contains(record.date.getDayOfWeek),
        error("Value unexpected: " + record.date.getDayOfWeek)))

  }
}

