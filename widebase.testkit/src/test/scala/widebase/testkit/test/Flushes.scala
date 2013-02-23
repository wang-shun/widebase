package widebase.testkit.test

import java.io. { File, RandomAccessFile }
import java.text.SimpleDateFormat
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

/* Test of flush operation.
 *
 * @note Writes one to file and read one from file (incl. memory mapped file).
 * @note Consums many times regarding many flushes, but good for debugging of read and write operations.
 *
 * @author myst3r10n
 */
object Flushes extends Logger with Loggable {

  import widebase.io.filter.StreamFilter.StreamFilter

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  protected var amount: Int = _

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    amount = 2500

    val dir = new File("usr")

    if(!dir.exists)
      dir.mkdir

    val file = new File(dir.getPath + "/flushes")

    variant(file, amount)
    println("")
    fileVariant(file, amount)
    println("")
    fileVariantMapper(file, amount) // Call first writeRead to initialize buffer size!

  }

  def variant(file: File, amount: Int) {

    if(file.exists)
      file.delete

    val writerChannel = new RandomAccessFile(file.getPath, "rw").getChannel
    val readerChannel = new RandomAccessFile(file.getPath, "r").getChannel

    writerChannel.tryLock

    val writer = new VariantWriter(writerChannel)
    val reader = new VariantReader(readerChannel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(true)
      writer.write(false)

      writer.flush

      reader.mode = Datatype.Bool
      val boolTrueValue = reader.readBool
      val boolFalseValue = reader.readBool
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.flush

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.flush

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.flush

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.flush

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.flush

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.flush

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.flush

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.flush

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.flush

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.flush

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(12), error("Value unexpected: " + minute))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.flush

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(34), error("Value unexpected: " + second))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.flush

       reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.flush

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      writer.flush

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.flush

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hello, error("Value unexpected: " + symbol))

      writer.mode = Datatype.String
      writer.write("World!")

      writer.flush

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "World!", error("Value unexpected: " + string))

    }

    writer.close
    reader.close

    println("Variant written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariant(file: File, amount: Int) {

    if(file.exists)
      file.delete

    val writerChannel = new RandomAccessFile(file.getPath, "rw").getChannel
    val readerChannel = new RandomAccessFile(file.getPath, "r").getChannel

    writerChannel.tryLock

    val writer = new FileVariantWriter(writerChannel)
    val reader = new FileVariantReader(readerChannel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(true)
      writer.write(false)

      writer.flush

      reader.mode = Datatype.Bool
      val boolTrueValue = reader.readBool
      val boolFalseValue = reader.readBool
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.flush

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.flush

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.flush

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.flush

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.flush

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.flush

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.flush

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.flush

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.flush

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.flush

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(12), error("Value unexpected: " + minute))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.flush

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(34), error("Value unexpected: " + second))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.flush

       reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.flush

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      writer.flush

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.flush

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hello, error("Value unexpected: " + symbol))

      writer.mode = Datatype.String
      writer.write("World!")

      writer.flush

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "World!", error("Value unexpected: " + string))

    }

    writer.close
    reader.close

    println("FileVariant written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapper(file: File, amount: Int) {

    val writerChannel = new RandomAccessFile(file.getPath, "rw").getChannel
    val readerChannel = new RandomAccessFile(file.getPath, "r").getChannel

    writerChannel.tryLock

    val writer = new FileVariantMapper(writerChannel)(MapFilter.ReadWrite)
    val reader = new FileVariantMapper(readerChannel)(MapFilter.ReadOnly)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(false)
      writer.write(true)

      reader.mode = Datatype.Bool
      val boolFalseValue = reader.readBool
      val boolTrueValue = reader.readBool
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))

      writer.mode = Datatype.Byte
      writer.write(Byte.MaxValue)
      writer.write(Byte.MinValue)

      reader.mode = Datatype.Byte
      val byteMaxValue = reader.read
      val byteMinValue = reader.read
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))

      writer.mode = Datatype.Char
      writer.write(Char.MaxValue)
      writer.write(Char.MinValue)

      reader.mode = Datatype.Char
      val charMaxValue = reader.readChar
      val charMinValue = reader.readChar
      val charMaxValueAsInt = charMaxValue.toInt
      val charMinValueAsInt = charMinValue.toInt
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))

      writer.mode = Datatype.Double
      writer.write(Double.MaxValue)
      writer.write(Double.MinValue)

      reader.mode = Datatype.Double
      val doubleMaxValue = reader.readDouble
      val doubleMinValue = reader.readDouble
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))

      writer.mode = Datatype.Float
      writer.write(Float.MaxValue)
      writer.write(Float.MinValue)

      reader.mode = Datatype.Float
      val floatMaxValue = reader.readFloat
      val floatMinValue = reader.readFloat
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))

      writer.mode = Datatype.Int
      writer.write(Int.MaxValue)
      writer.write(Int.MinValue)

      reader.mode = Datatype.Int
      val intMaxValue = reader.readInt
      val intMinValue = reader.readInt
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))

      writer.mode = Datatype.Long
      writer.write(Long.MaxValue)
      writer.write(Long.MinValue)

      reader.mode = Datatype.Long
      val longMaxValue = reader.readLong
      val longMinValue = reader.readLong
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))

      writer.mode = Datatype.Short
      writer.write(Short.MaxValue)
      writer.write(Short.MinValue)

      reader.mode = Datatype.Short
      val shortMaxValue = reader.readShort
      val shortMinValue = reader.readShort
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(24))

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(24), error("Value unexpected: " + minute))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(56))

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(56), error("Value unexpected: " + second))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      writer.mode = Datatype.Symbol
      writer.write('Hoi, true)

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hoi, error("Value unexpected: " + symbol))

      writer.mode = Datatype.String
      writer.write("Wält!")

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "Wält!", error("Value unexpected: " + string))

    }

    writer.close
    reader.close

    println("FileVariantMapper written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

