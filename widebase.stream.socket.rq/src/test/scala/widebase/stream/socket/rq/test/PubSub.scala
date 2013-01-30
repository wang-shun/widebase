package widebase.stream.socket.rq.test

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

import scala.util.control.Breaks. { break, breakable }

import widebase.data.Datatype

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
import widebase.io.csv. { FileEmptyException, TypeMismatchException }
import widebase.io.csv.filter.ZipFilter
import widebase.io.filter.StreamFilter
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener

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
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  object listener extends RecordListener {

    def react = {

      case event: String => println("event: " + event)
      case (records: Int, partition: String) => println("rollback: " + records + " @ " + partition)
      case chunk: Table => println("update: " + chunk)

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

    val broker = rq.broker

    broker.auths = auths

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-f" =>
          i += 1
          broker.filter(args(i))

        case "-i" =>
          i += 1
          broker.interval = args(i).toInt

        case "-p" =>
          i += 1
          broker.port = args(i).toInt

        case path: String =>
          broker.path = path

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    try {

      broker.bind

      info("Listen on " + broker.port)

      pubsub

    } finally {

      broker.close

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

    val consumer = rq.consumer(listener)
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
                  table(table.labels(i)) = new BoolColumn

                if(value == "true")
                  table.columns.toBuffer(i).asInstanceOf[BoolColumn] += true
                else if(value == "false")
                  table.columns.toBuffer(i).asInstanceOf[BoolColumn] += false
                else {

                  val boolValue = value.getBytes()(0)

                  if(boolValue == '1')
                    table.columns.toBuffer(i).asInstanceOf[BoolColumn] += true
                  else if(boolValue == '0')
                    table.columns.toBuffer(i).asInstanceOf[BoolColumn] += false
                  else
                    throw TypeMismatchException(Datatype.Bool, boolValue.toString)

                }

              case Datatype.Byte =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new ByteColumn

                table.columns.toBuffer(i).asInstanceOf[ByteColumn] += java.lang.Byte.valueOf(value)

              case Datatype.Char =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new CharColumn

                table.columns.toBuffer(i).asInstanceOf[CharColumn] += value.toCharArray.head

              case Datatype.Double =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new DoubleColumn

                table.columns.toBuffer(i).asInstanceOf[DoubleColumn] += value.toDouble

              case Datatype.Float =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new FloatColumn

                table.columns.toBuffer(i).asInstanceOf[FloatColumn] += value.toFloat

              case Datatype.Int =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new IntColumn

                table.columns.toBuffer(i).asInstanceOf[IntColumn] += value.toInt

              case Datatype.Long =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new LongColumn

                table.columns.toBuffer(i).asInstanceOf[LongColumn] += value.toLong

              case Datatype.Short =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new ShortColumn

                table.columns.toBuffer(i).asInstanceOf[ShortColumn] += value.toShort

              case Datatype.Month =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new MonthColumn

                table.columns.toBuffer(i).asInstanceOf[MonthColumn] += new YearMonth(value.toLong)

              case Datatype.Date =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new DateColumn

                table.columns.toBuffer(i).asInstanceOf[DateColumn] += new LocalDate(value.toLong)

              case Datatype.Minute =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new MinuteColumn

                table.columns.toBuffer(i).asInstanceOf[MinuteColumn] += Minutes.minutes(value.toInt)

              case Datatype.Second =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new SecondColumn

                table.columns.toBuffer(i).asInstanceOf[SecondColumn] += Seconds.seconds(value.toInt)

              case Datatype.Time =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new TimeColumn

                table.columns.toBuffer(i).asInstanceOf[TimeColumn] += new LocalTime(value.toLong)

              case Datatype.DateTime =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new DateTimeColumn

                table.columns.toBuffer(i).asInstanceOf[DateTimeColumn] += new LocalDateTime(value.toLong)

              case Datatype.Timestamp =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new TimestampColumn

                table.columns.toBuffer(i).asInstanceOf[TimestampColumn] += new Timestamp(value.toLong)

              case Datatype.Symbol =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new SymbolColumn

                table.columns.toBuffer(i).asInstanceOf[SymbolColumn] += Symbol(value)

              case Datatype.String =>
                if(table.columns.toBuffer(i) == null)
                  table(table.labels(i)) = new StringColumn

                table.columns.toBuffer(i).asInstanceOf[StringColumn] += value

            }

            i += 1

          }

          // Debug
//        println(table)

          // Publish
          producer.publish("table", table)

          table.columns.foreach(column => column.clear)
/*
          // Debug
          counter -= 1
          if(counter == 0)
            break
*/
          line = reader.readLine

        }
      }
    } catch {

      case e => throw e

    } finally {

      producer.close
      consumer.close
      reader.close

    }
  }
}

