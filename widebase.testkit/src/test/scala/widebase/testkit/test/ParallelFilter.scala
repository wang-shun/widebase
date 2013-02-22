package widebase.testkit.test

import java.util.concurrent.CountDownLatch

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. {

  DateTimeConstants,
  Days,
  LocalDate,
  LocalDateTime,
  LocalTime

}

import scala.collection.mutable.ArrayBuffer

import widebase.db.table. { Table, TemplateTable }

/* Test of filter and filterNot with DSL in parallel mode.
 *
 * @author myst3r10n
 */
object ParallelFilter extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  case class Log(val time: LocalDateTime, val user: String, val value: Int)

  case class LogTable(
    table: Table = Table(
      string("time", "user", "value"),
      dateTime(),
      string(),
      int()))
    extends TemplateTable[Log] {

    val time = table("time").Z
    val user = table("user").S
    val value = table("value").i

    def +=(log: Log) = {

      time += log.time
      user += log.user
      value += log.value

      this

    }

    def +=(
      time: LocalDateTime,
      user: String,
      value: Int): LogTable =
      this += Log(time, user, value)

    def ++=(table: LogTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) = Log(time(index), user(index), value(index))

    def filter(predicate: Log => Boolean) = {

      val filteredTable = new LogTable

      for(r <- 0 to records.length - 1)
        if(predicate(this(r)))
          filteredTable += this(r)

      filteredTable

    }

    def peer = table

  }

  val debug = false
  val users = Array("Alice", "Bob", "Mallory")
  var from = new LocalDateTime(2012, 12, 30, 0, 0, 0, 0)
  val till = new LocalDateTime(2013, 1, 4, 0, 0, 0, 0)

  def main(args: Array[String]) {

    // Fill parted table
    fill

    // Single processing
    single

    // Parallel processing
    parallel

  }

  def fill {

    var started = 0L

    val table = new LogTable

    var records = 0

    var move = from

    while(move.compareTo(till) == -1) {

      try {

        move.toDateTime // time zone offset transition violation?

        table.time += move
        table.user += users(records % 3)

        records += 1
        table.value += records

      } catch {

        case e: IllegalArgumentException => // http://joda-time.sourceforge.net/faq.html#illegalinstant

      }

      val next = move.plusMillis(500)

      if(move.getDayOfWeek != next.getDayOfWeek) {

        started = System.currentTimeMillis
        save.dir("parallelFilter", table.peer)('daily)
        info("Table saved " + records + " records in " +
          diff(started, System.currentTimeMillis))

        table.peer.columns.foreach(column => column.clear)

      }

      move = next

    }
  }

  def single {

    var started = 0L

    started = System.currentTimeMillis
    val mapped = map.dates("parallelFilter", from.toLocalDate, till.toLocalDate).tables
    info("Tables mapped in " + diff(started, System.currentTimeMillis))

    val filteredTable = new LogTable
    started = System.currentTimeMillis
    mapped.foreach { table =>

      filteredTable ++= LogTable(table).filter(record => record.user == "Alice")

    }
    info("Single filtered " + filteredTable.records.length + " records in " +
      diff(started, System.currentTimeMillis))

    filteredTable.foreach(record =>
      assert(
        record.user == "Alice",
        error("Value unexpected: " + record.user)))

    filteredTable.peer.columns.foreach(column => column.clear)

  }

  def parallel {

    var records = 0
    var started = 0L

    started = System.currentTimeMillis
    val mapped = map.dates("parallelFilter", from.toLocalDate, till.toLocalDate)
    info("Tables mapped in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val filteredTable = LogTable(mapped.filter(record => record("user") == "Alice"))
    info("Parallel filtered " + filteredTable.records.length + " records in " +
      diff(started, System.currentTimeMillis))

    filteredTable.foreach(record =>
      assert(
        record.user == "Alice",
        error("Value unexpected: " + record.user)))

    filteredTable.peer.columns.foreach(column => column.clear)

  }
}

