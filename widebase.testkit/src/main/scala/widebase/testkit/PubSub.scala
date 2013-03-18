package widebase.testkit

import java.io. {

  BufferedReader,
  FileInputStream,
  FileReader,
  InputStreamReader

}

import java.sql.Timestamp
import java.util.zip. { GZIPInputStream, ZipInputStream }

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.concurrent.Lock
import scala.util.control.Breaks. { break, breakable }

import widebase.data.Datatype

import widebase.db.table.Table
import widebase.io.csv. { FileEmptyException, TypeMismatchException }
import widebase.io.csv.filter.ZipFilter
import widebase.io.filter.StreamFilter
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener
import widebase.stream.socket.rq. { Broker, Consumer }

/* A publish-subscribe pattern test.
 *
 * Run:
 *
 * test:run-main widebase.stream.socket.rq.test.PubSub usr/sync/test/db
 *
 * @author myst3r10n
 */
object PubSub extends Logger with Loggable {

  import widebase.stream.socket.rq

  // Init IO
  import widebase.data

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  val lock = new Lock

  val pending = Table(
    string(
      "partition",
      "bool",
      "byte",
      "char",
      "double",
      "float",
      "int",
      "long",
      "short",
      "month",
      "date",
      "minute",
      "second",
      "time",
      "datetime",
      "timestamp",
      "symbol",
      "string"),
    date(),
    bool(),
    byte(),
    char(),
    double(),
    float(),
    int(),
    long(),
    short(),
    month(),
    date(),
    minute(),
    second(),
    time(),
    datetime(),
    timestamp(),
    symbol(),
    string())

  var broker: Broker = _
  var consumer: Consumer = _

  object listener extends RecordListener {

    def react = {

      case event: String => assert(event == "table", error("Value unexpected: " + event))
      case (records: Int, partition: String) => println("rollback: " + records + " @ " + partition)
      case chunk: Table =>

        try {

          lock.acquire

          for(r <- 0 to chunk.records.length - 1) {

            assert(chunk.records.head == pending.records.head, error("Value unexpected: " + chunk.records.head))

            chunk.columns.foreach(_.remove(0))
            pending.columns.foreach(_.remove(0))

          }

          if(pending.records.isEmpty) {

            consumer.close
            broker.close

          }

        } finally {

          lock.release

        }
    }
  }

  def main(args: Array[String]) {

    // Authorization map
    val auths = new AuthMap {

      jaas = "widebase-broker"
      this += "FlushMessage" -> Array("admins", "producers")
      this += "NotifyMessage" -> Array("admins", "producers")
      this += "PublishMessage" -> Array("admins", "producers")
      this += "RemoteShutdownMessage" -> Array("admins")
      this += "SubscribeMessage" -> Array("admins", "consumers")
      this += "UnsubscribeMessage" -> Array("admins", "consumers")

    }

    broker = rq.broker
    broker.auths = auths

    try {

      broker.bind

      info("Listen on " + broker.port)

      pubsub

      // Required only in association with All.scala regarding async
      var done = false
      do {

        try {

          lock.acquire
          done = pending.records.isEmpty

        } finally {

          lock.release

        }

        if(!done)
          Thread.sleep(1000)

      } while(!done)
    } finally {

      try {

        lock.acquire

        if(pending.records.isEmpty)
          broker.close

      } finally {

        lock.release

      }
    }
  }

  def pubsub {

    val valueTypes = data.by("DbxcdfilsMDUVTZPYS")
    val delimiter = ","
    val filename = "usr/csv/partitionedTable.csv.gz"
    val inFilter = ZipFilter.Gzip
    val inSocketFilter = StreamFilter.None
    val outSocketFilter = StreamFilter.None

    var reader: BufferedReader = null

    if(inFilter == ZipFilter.None)
      reader = new BufferedReader(new FileReader(filename))
    else if(inFilter == ZipFilter.Gzip)
      reader = new BufferedReader(new InputStreamReader(
        new GZIPInputStream(new FileInputStream(filename))))
    else if(inFilter == ZipFilter.Zlib)
      reader = new BufferedReader(new InputStreamReader(
        new ZipInputStream(new FileInputStream(filename))))

    consumer = rq.consumer(listener)
    val producer = rq.producer

    try {

      consumer.login("consumer", "password").subscribe("table")
      producer.login("producer", "password")

      var line = reader.readLine

      if(line == null)
        throw FileEmptyException(filename)

      val table = new Table
      line.split(delimiter).foreach(label => table ++= label -> null)

      line = reader.readLine

      breakable {

        var counter = 1
        var partition: String = null

        while(line != null) {

          var i = 0

          line.split(delimiter).foreach { value =>

            if(i == 0)
              if(partition == null)
                partition = value
              else if(partition != value) {

                producer.flush("table")
                partition = value

              }

            valueTypes(i) match {

              case Datatype.Bool => 
                if(table.columns.toBuffer.toBuffer(i) == null)
                  table(table.labels(i)) = bool()

                if(value == "true")
                  table.columns.toBuffer(i).b += true
                else if(value == "false")
                  table.columns.toBuffer(i).b += false
                else {

                  val boolValue = value.getBytes()(0)

                  if(boolValue == '1')
                    table.columns.toBuffer(i).b += true
                  else if(boolValue == '0')
                    table.columns.toBuffer(i).b += false
                  else
                    throw TypeMismatchException(Datatype.Bool, boolValue.toString)

                }

              case Datatype.Byte =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = byte()

                table.columns.toBuffer(i).x += java.lang.Byte.valueOf(value)

              case Datatype.Char =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = char()

                table.columns.toBuffer(i).c += value.toCharArray.head

              case Datatype.Double =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = double()

                table.columns.toBuffer(i).d += value.toDouble

              case Datatype.Float =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = float()

                table.columns.toBuffer(i).f += value.toFloat

              case Datatype.Int =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = int()

                table.columns.toBuffer(i).i += value.toInt

              case Datatype.Long =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = long()

                table.columns.toBuffer(i).l += value.toLong

              case Datatype.Short =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = short()

                table.columns.toBuffer(i).s += value.toShort

              case Datatype.Month =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = month()

                table.columns.toBuffer(i).M += new YearMonth(value.toLong)

              case Datatype.Date =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = date()

                table.columns.toBuffer(i).D += new LocalDate(value.toLong)

              case Datatype.Minute =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = minute()

                table.columns.toBuffer(i).U += Minutes.minutes(value.toInt)

              case Datatype.Second =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = second()

                table.columns.toBuffer(i).V += Seconds.seconds(value.toInt)

              case Datatype.Time =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = time()

                table.columns.toBuffer(i).T += new LocalTime(value.toLong)

              case Datatype.DateTime =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = datetime()

                table.columns.toBuffer(i).Z += new LocalDateTime(value.toLong)

              case Datatype.Timestamp =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = timestamp()

                table.columns.toBuffer(i).P += new Timestamp(value.toLong)

              case Datatype.Symbol =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = symbol()

                table.columns.toBuffer(i).Y += Symbol(value)

              case Datatype.String =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = string()

                table.columns.toBuffer(i).S += value

            }

            i += 1

          }

          // Publish
          try {

            lock.acquire
            pending ++= table

          } finally {

            lock.release

          }

          producer.publish("table", table)

          table.columns.foreach(column => column.clear)

          // Debug
//          counter -= 1
//          if(counter == 0)
//            break

          line = reader.readLine

        }
      }
    } catch {

      case e: Throwable => throw e

    } finally {

      producer.close
      reader.close

      try {

        lock.acquire

        if(pending.records.isEmpty)
          consumer.close

      } finally {

        lock.release

      }
    }
  }
}

