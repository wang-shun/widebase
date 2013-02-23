package widebase.testkit.test

import java.io. { File, RandomAccessFile }
import java.sql.Timestamp

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import org.joda.time.format.DateTimeFormat

import widebase.data.Datatype
import widebase.io.file. { FileVariantMapper, FileVariantReader, FileVariantWriter }
import widebase.io.filter. { CompressionLevel, MapFilter, StreamFilter }
import widebase.io. { VariantReader, VariantWriter }

/* Test of bulk read and bulk write operation.
 *
 * @note Writes all to file and read all from file (incl. memory mapped file).
 * @note It's fast and good for mixed mode validation of bulk read and bulk write operations.
 *
 * @author myst3r10n
 */
object Bulk extends Logger with Loggable {

  import widebase.io.filter.StreamFilter.StreamFilter

  // Init Testkit
  import widebase.testkit._

  protected var amount: Int = _
  protected var filter: StreamFilter = _
  protected var length: Int = _
  protected var level: Int = _

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  def main(args: Array[String]) {

    amount = 250
    filter = StreamFilter.None
    length = 100
    level = CompressionLevel.Default

    val dir = new File("usr")

    if(!dir.exists)
      dir.mkdir

    val extension =
      filter match {

        case StreamFilter.Gzip => ".gz"
        case StreamFilter.Zlib => ".zip"
        case _ => ""

      }

    val file = new File(dir.getPath + "/bulk")

    variantWriter(new File(file.getPath + extension), amount, length, filter, level)
    variantReader(new File(file.getPath + extension), amount, length, filter)
    println("")
    fileVariantWriter(file, amount, length)
    fileVariantReader(file, amount, length)
    println("")
    fileVariantMapperWriter(file, amount, length) // Call variantWriter write to initialize buffer size!
    fileVariantMapperReader(file, amount, length)

  }

  def variantWriter(
    file: File,
    amount: Int,
    length: Int,
    filter: StreamFilter = StreamFilter.None,
    level: Int = CompressionLevel.Default) {

    if(file.exists)
      file.delete

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new VariantWriter(channel, filter, level)

    val boolTrueValues = Array.fill(length)(true)
    val boolFalseValues = Array.fill(length)(false)

    val byteMinValues = Array.fill(length)(Byte.MinValue)
    val byteMaxValues = Array.fill(length)(Byte.MaxValue)

    val charMinValues = Array.fill(length)(Char.MinValue)
    val charMaxValues = Array.fill(length)(Char.MaxValue)

    val doubleMinValues = Array.fill(length)(Double.MinValue)
    val doubleMaxValues = Array.fill(length)(Double.MaxValue)

    val floatMinValues = Array.fill(length)(Float.MinValue)
    val floatMaxValues = Array.fill(length)(Float.MaxValue)

    val intMinValues = Array.fill(length)(Int.MinValue)
    val intMaxValues = Array.fill(length)(Int.MaxValue)

    val longMinValues = Array.fill(length)(Long.MinValue)
    val longMaxValues = Array.fill(length)(Long.MaxValue)

    val shortMinValues = Array.fill(length)(Short.MinValue)
    val shortMaxValues = Array.fill(length)(Short.MaxValue)

    val monthValues = Array.fill(length)(new YearMonth(millis))
    val dateValues = Array.fill(length)(new LocalDate(millis))
    val minuteValues = Array.fill(length)(Minutes.minutes(12))
    val secondValues = Array.fill(length)(Seconds.seconds(34))
    val timeValues = Array.fill(length)(new LocalTime(millis))
    val dateTimeValues = Array.fill(length)(new LocalDateTime(millis))
    val timestampValues = Array.fill(length)(new Timestamp(millis))
    val symbolValues = Array.fill(length)('Hello)
    val stringValues = Array.fill(length)("World!")

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(boolTrueValues)
      writer.write(boolFalseValues)

      writer.mode = Datatype.Byte
      writer.write(byteMinValues)
      writer.write(byteMaxValues)

      writer.mode = Datatype.Char
      writer.write(charMinValues)
      writer.write(charMaxValues)

      writer.mode = Datatype.Double
      writer.write(doubleMinValues)
      writer.write(doubleMaxValues)

      writer.mode = Datatype.Float
      writer.write(floatMinValues)
      writer.write(floatMaxValues)

      writer.mode = Datatype.Int
      writer.write(intMinValues)
      writer.write(intMaxValues)

      writer.mode = Datatype.Long
      writer.write(longMinValues)
      writer.write(longMaxValues)

      writer.mode = Datatype.Short
      writer.write(shortMinValues)
      writer.write(shortMaxValues)

      writer.mode = Datatype.Month
      monthValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Date
      writer.write(dateValues)

      writer.mode = Datatype.Minute
      writer.write(minuteValues)

      writer.mode = Datatype.Second
      writer.write(secondValues)

      writer.mode = Datatype.Time
      writer.write(timeValues)

      writer.mode = Datatype.DateTime
      writer.write(dateTimeValues)

      writer.mode = Datatype.Timestamp
      timestampValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Symbol
      symbolValues.foreach(value => writer.write(value, true))

      writer.mode = Datatype.String
      stringValues.foreach(value => writer.write(value))

    }

    writer.close

    println("VariantWriter written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def variantReader(
    file: File,
    amount: Int,
    length: Int,
    filter: StreamFilter = StreamFilter.None) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new VariantReader(channel, filter)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      reader.readBool(length).foreach(value => assert(value == true, error("Value unexpected: " + value)))
      reader.readBool(length).foreach(value => assert(value == false, error("Value unexpected: " + value)))

      reader.mode = Datatype.Byte
      reader.read(length).foreach(value =>
        assert(value == Byte.MinValue, error("Value unexpected: " + value)))
      reader.read(length).foreach(value =>
        assert(value == Byte.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Char
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MinValue.toInt, error("Value unexpected: " + value.toInt)))
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MaxValue.toInt, error("Value unexpected: " + value.toInt)))

      reader.mode = Datatype.Double
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MinValue, error("Value unexpected: " + value)))
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Float
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MinValue, error("Value unexpected: " + value)))
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Int
      reader.readInt(length).foreach(value =>
        assert(value == Int.MinValue, error("Value unexpected: " + value)))
      reader.readInt(length).foreach(value =>
        assert(value == Int.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Long
      reader.readLong(length).foreach(value =>
        assert(value == Long.MinValue, error("Value unexpected: " + value)))
      reader.readLong(length).foreach(value =>
        assert(value == Long.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Short
      reader.readShort(length).foreach(value =>
        assert(value == Short.MinValue, error("Value unexpected: " + value)))
      reader.readShort(length).foreach(value =>
        assert(value == Short.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Month
      for(i <- 0 to length - 1) {

        val value = reader.readMonth
        assert(value == new YearMonth(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Date
      reader.readDate(length).foreach(value =>
        assert(value == new LocalDate(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Minute
      reader.readMinute(length).foreach(value =>
        assert(value == Minutes.minutes(12), error("Value unexpected: " + value)))

      reader.mode = Datatype.Second
      reader.readSecond(length).foreach(value =>
        assert(value == Seconds.seconds(34), error("Value unexpected: " + value)))

      reader.mode = Datatype.Time
      reader.readTime(length).foreach(value =>
        assert(value == new LocalTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.DateTime
      reader.readDateTime(length).foreach(value =>
        assert(value == new LocalDateTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Timestamp
      for(i <- 0 to length - 1) {

        val value = reader.readTimestamp
        assert(value == new Timestamp(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Symbol
      for(i <- 0 to length - 1) {

        val value = reader.readSymbol
        assert(value == 'Hello, error("Value unexpected: " + value))

      }

      reader.mode = Datatype.String
      for(i <- 0 to length - 1) {

        val value = reader.readString("World!".getBytes(reader.charset).size)
        assert(value == "World!", error("Value unexpected: " + value))

      }
    }

    reader.close

    println("VariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantWriter(file: File, amount: Int, length: Int) {

    if(file.exists)
      file.delete

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new FileVariantWriter(channel)

    val boolTrueValues = Array.fill(length)(true)
    val boolFalseValues = Array.fill(length)(false)

    val byteMinValues = Array.fill(length)(Byte.MinValue)
    val byteMaxValues = Array.fill(length)(Byte.MaxValue)

    val charMinValues = Array.fill(length)(Char.MinValue)
    val charMaxValues = Array.fill(length)(Char.MaxValue)

    val doubleMinValues = Array.fill(length)(Double.MinValue)
    val doubleMaxValues = Array.fill(length)(Double.MaxValue)

    val floatMinValues = Array.fill(length)(Float.MinValue)
    val floatMaxValues = Array.fill(length)(Float.MaxValue)

    val intMinValues = Array.fill(length)(Int.MinValue)
    val intMaxValues = Array.fill(length)(Int.MaxValue)

    val longMinValues = Array.fill(length)(Long.MinValue)
    val longMaxValues = Array.fill(length)(Long.MaxValue)

    val shortMinValues = Array.fill(length)(Short.MinValue)
    val shortMaxValues = Array.fill(length)(Short.MaxValue)

    val monthValues = Array.fill(length)(new YearMonth(millis))
    val dateValues = Array.fill(length)(new LocalDate(millis))
    val minuteValues = Array.fill(length)(Minutes.minutes(12))
    val secondValues = Array.fill(length)(Seconds.seconds(34))
    val timeValues = Array.fill(length)(new LocalTime(millis))
    val dateTimeValues = Array.fill(length)(new LocalDateTime(millis))
    val timestampValues = Array.fill(length)(new Timestamp(millis))
    val symbolValues = Array.fill(length)('Hello)
    val stringValues = Array.fill(length)("World!")

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(boolTrueValues)
      writer.write(boolFalseValues)

      writer.mode = Datatype.Byte
      writer.write(byteMinValues)
      writer.write(byteMaxValues)

      writer.mode = Datatype.Char
      writer.write(charMinValues)
      writer.write(charMaxValues)

      writer.mode = Datatype.Double
      writer.write(doubleMinValues)
      writer.write(doubleMaxValues)

      writer.mode = Datatype.Float
      writer.write(floatMinValues)
      writer.write(floatMaxValues)

      writer.mode = Datatype.Int
      writer.write(intMinValues)
      writer.write(intMaxValues)

      writer.mode = Datatype.Long
      writer.write(longMinValues)
      writer.write(longMaxValues)

      writer.mode = Datatype.Short
      writer.write(shortMinValues)
      writer.write(shortMaxValues)

      writer.mode = Datatype.Month
      monthValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Date
      writer.write(dateValues)

      writer.mode = Datatype.Minute
      writer.write(minuteValues)

      writer.mode = Datatype.Second
      writer.write(secondValues)

      writer.mode = Datatype.Time
      writer.write(timeValues)

      writer.mode = Datatype.DateTime
      writer.write(dateTimeValues)

      writer.mode = Datatype.Timestamp
      timestampValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Symbol
      symbolValues.foreach(value => writer.write(value, true))

      writer.mode = Datatype.String
      stringValues.foreach(value => writer.write(value))

    }

    writer.close

    println("FileVariantWriter written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantReader(file: File, amount: Int, length: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantReader(channel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      reader.readBool(length).foreach(value => assert(value == true, error("Value unexpected: " + value)))
      reader.readBool(length).foreach(value => assert(value == false, error("Value unexpected: " + value)))

      reader.mode = Datatype.Byte
      reader.read(length).foreach(value =>
        assert(value == Byte.MinValue, error("Value unexpected: " + value)))
      reader.read(length).foreach(value =>
        assert(value == Byte.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Char
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MinValue.toInt, error("Value unexpected: " + value.toInt)))
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MaxValue.toInt, error("Value unexpected: " + value.toInt)))

      reader.mode = Datatype.Double
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MinValue, error("Value unexpected: " + value)))
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Float
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MinValue, error("Value unexpected: " + value)))
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Int
      reader.readInt(length).foreach(value =>
        assert(value == Int.MinValue, error("Value unexpected: " + value)))
      reader.readInt(length).foreach(value =>
        assert(value == Int.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Long
      reader.readLong(length).foreach(value =>
        assert(value == Long.MinValue, error("Value unexpected: " + value)))
      reader.readLong(length).foreach(value =>
        assert(value == Long.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Short
      reader.readShort(length).foreach(value =>
        assert(value == Short.MinValue, error("Value unexpected: " + value)))
      reader.readShort(length).foreach(value =>
        assert(value == Short.MaxValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Month
      for(i <- 0 to length - 1) {

        val value = reader.readMonth
        assert(value == new YearMonth(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Date
      reader.readDate(length).foreach(value =>
        assert(value == new LocalDate(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Minute
      reader.readMinute(length).foreach(value =>
        assert(value == Minutes.minutes(12), error("Value unexpected: " + value)))

      reader.mode = Datatype.Second
      reader.readSecond(length).foreach(value =>
        assert(value == Seconds.seconds(34), error("Value unexpected: " + value)))

      reader.mode = Datatype.Time
      reader.readTime(length).foreach(value =>
        assert(value == new LocalTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.DateTime
      reader.readDateTime(length).foreach(value =>
        assert(value == new LocalDateTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Timestamp
      for(i <- 0 to length - 1) {

        val value = reader.readTimestamp
        assert(value == new Timestamp(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Symbol
      for(i <- 0 to length - 1) {

        val value = reader.readSymbol
        assert(value == 'Hello, error("Value unexpected: " + value))

      }

      reader.mode = Datatype.String
      for(i <- 0 to length - 1) {

        val value = reader.readString("World!".getBytes(reader.charset).size)
        assert(value == "World!", error("Value unexpected: " + value))

      }
    }

    reader.close

    println("FileVariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperWriter(file: File, amount: Int, length: Int) {

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new FileVariantMapper(channel)(MapFilter.ReadWrite)

    val boolFalseValues = Array.fill(length)(false)
    val boolTrueValues = Array.fill(length)(true)

    val byteMaxValues = Array.fill(length)(Byte.MaxValue)
    val byteMinValues = Array.fill(length)(Byte.MinValue)

    val charMaxValues = Array.fill(length)(Char.MaxValue)
    val charMinValues = Array.fill(length)(Char.MinValue)

    val doubleMaxValues = Array.fill(length)(Double.MaxValue)
    val doubleMinValues = Array.fill(length)(Double.MinValue)

    val floatMaxValues = Array.fill(length)(Float.MaxValue)
    val floatMinValues = Array.fill(length)(Float.MinValue)

    val intMaxValues = Array.fill(length)(Int.MaxValue)
    val intMinValues = Array.fill(length)(Int.MinValue)

    val longMaxValues = Array.fill(length)(Long.MaxValue)
    val longMinValues = Array.fill(length)(Long.MinValue)

    val shortMaxValues = Array.fill(length)(Short.MaxValue)
    val shortMinValues = Array.fill(length)(Short.MinValue)

    val monthValues = Array.fill(length)(new YearMonth(millis))
    val dateValues = Array.fill(length)(new LocalDate(millis))
    val minuteValues = Array.fill(length)(Minutes.minutes(12))
    val secondValues = Array.fill(length)(Seconds.seconds(34))
    val timeValues = Array.fill(length)(new LocalTime(millis))
    val dateTimeValues = Array.fill(length)(new LocalDateTime(millis))
    val timestampValues = Array.fill(length)(new Timestamp(millis))
    val symbolValues = Array.fill(length)('Hello)
    val stringValues = Array.fill(length)("World!")

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(boolFalseValues)
      writer.write(boolTrueValues)

      writer.mode = Datatype.Byte
      writer.write(byteMaxValues)
      writer.write(byteMinValues)

      writer.mode = Datatype.Char
      writer.write(charMaxValues)
      writer.write(charMinValues)

      writer.mode = Datatype.Double
      writer.write(doubleMaxValues)
      writer.write(doubleMinValues)

      writer.mode = Datatype.Float
      writer.write(floatMaxValues)
      writer.write(floatMinValues)

      writer.mode = Datatype.Int
      writer.write(intMaxValues)
      writer.write(intMinValues)

      writer.mode = Datatype.Long
      writer.write(longMaxValues)
      writer.write(longMinValues)

      writer.mode = Datatype.Short
      writer.write(shortMaxValues)
      writer.write(shortMinValues)

      writer.mode = Datatype.Month
      monthValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Date
      writer.write(dateValues)

      writer.mode = Datatype.Minute
      writer.write(minuteValues)

      writer.mode = Datatype.Second
      writer.write(secondValues)

      writer.mode = Datatype.Time
      writer.write(timeValues)

      writer.mode = Datatype.DateTime
      writer.write(dateTimeValues)

      writer.mode = Datatype.Timestamp
      timestampValues.foreach(value => writer.write(value))

      writer.mode = Datatype.Symbol
      symbolValues.foreach(value => writer.write(value, true))

      writer.mode = Datatype.String
      stringValues.foreach(value => writer.write(value))

    }

    writer.close

    println("FileVariantMapper written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperReader(file: File, amount: Int, length: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantMapper(channel)(MapFilter.ReadOnly)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      reader.readBool(length).foreach(value => assert(value == false, error("Value unexpected: " + value)))
      reader.readBool(length).foreach(value => assert(value == true, error("Value unexpected: " + value)))

      reader.mode = Datatype.Byte
      reader.read(length).foreach(value =>
        assert(value == Byte.MaxValue, error("Value unexpected: " + value)))
      reader.read(length).foreach(value =>
        assert(value == Byte.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Char
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MaxValue.toInt, error("Value unexpected: " + value.toInt)))
      reader.readChar(length).foreach(value =>
        assert(value.toInt == Char.MinValue.toInt, error("Value unexpected: " + value.toInt)))

      reader.mode = Datatype.Double
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MaxValue, error("Value unexpected: " + value)))
      reader.readDouble(length).foreach(value =>
        assert(value == Double.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Float
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MaxValue, error("Value unexpected: " + value)))
      reader.readFloat(length).foreach(value =>
        assert(value == Float.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Int
      reader.readInt(length).foreach(value =>
        assert(value == Int.MaxValue, error("Value unexpected: " + value)))
      reader.readInt(length).foreach(value =>
        assert(value == Int.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Long
      reader.readLong(length).foreach(value =>
        assert(value == Long.MaxValue, error("Value unexpected: " + value)))
      reader.readLong(length).foreach(value =>
        assert(value == Long.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Short
      reader.readShort(length).foreach(value =>
        assert(value == Short.MaxValue, error("Value unexpected: " + value)))
      reader.readShort(length).foreach(value =>
        assert(value == Short.MinValue, error("Value unexpected: " + value)))

      reader.mode = Datatype.Month
      for(i <- 0 to length - 1) {

        val value = reader.readMonth
        assert(value == new YearMonth(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Date
      reader.readDate(length).foreach(value =>
        assert(value == new LocalDate(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Minute
      reader.readMinute(length).foreach(value =>
        assert(value == Minutes.minutes(12), error("Value unexpected: " + value)))

      reader.mode = Datatype.Second
      reader.readSecond(length).foreach(value =>
        assert(value == Seconds.seconds(34), error("Value unexpected: " + value)))

      reader.mode = Datatype.Time
      reader.readTime(length).foreach(value =>
        assert(value == new LocalTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.DateTime
      reader.readDateTime(length).foreach(value =>
        assert(value == new LocalDateTime(millis), error("Value unexpected: " + value)))

      reader.mode = Datatype.Timestamp
      for(i <- 0 to length - 1) {

        val value = reader.readTimestamp
        assert(value == new Timestamp(millis), error("Value unexpected: " + value))

      }

      reader.mode = Datatype.Symbol
      for(i <- 0 to length - 1) {

        val value = reader.readSymbol
        assert(value == 'Hello, error("Value unexpected: " + value))

      }

      reader.mode = Datatype.String
      for(i <- 0 to length - 1) {

        val value = reader.readString("World!".getBytes(reader.charset).size)
        assert(value == "World!", error("Value unexpected: " + value))

      }
    }

    reader.close

    println("FileVariantMapper read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

