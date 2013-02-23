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

/* Test of read and write operation.
 *
 * @note Writes all to file and read all from file (incl. memory mapped file).
 * @note It's fast and good for mixed mode validation of read and write operations.
 *
 * @author myst3r10n
 */
object Sequential extends Logger with Loggable {

  import widebase.io.filter.StreamFilter.StreamFilter

  // Init Testkit
  import widebase.testkit._

  protected var amount: Int = _
  protected var filter: StreamFilter = _
  protected var level: Int = _

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  def main(args: Array[String]) {

    amount = 25000
    filter = StreamFilter.None
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

    val file = new File(dir.getPath + "/sequential")

    variantWriter(new File(file.getPath + extension), amount, filter, level)
    variantReader(new File(file.getPath + extension), amount, filter)
    println("")
    fileVariantWriter(file, amount)
    fileVariantReader(file, amount)
    println("")
    fileVariantMapperWriter(file, amount) // Call first variantWriter to initialize buffer size!
    fileVariantMapperReader(file, amount)

  }

  def variantWriter(
    file: File,
    amount: Int,
    filter: StreamFilter = StreamFilter.None,
    level: Int = CompressionLevel.Default) {

    if(file.exists)
      file.delete

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new VariantWriter(channel, filter, level)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(true)
      writer.write(false)

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp(millis))

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.mode = Datatype.String
      writer.write("World!")

    }

    writer.close

    println("VariantWriter written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def variantReader(
    file: File,
    amount: Int,
    filter: StreamFilter = StreamFilter.None) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new VariantReader(channel, filter)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      val boolTrueValue = reader.readBool
      val boolFalseValue = reader.readBool
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(12), error("Value unexpected: " + minute))

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(34), error("Value unexpected: " + second))

      reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hello, error("Value unexpected: " + symbol))

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "World!", error("Value unexpected: " + string))

    }

    reader.close

    println("VariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantWriter(file: File, amount: Int) {

    if(file.exists)
      file.delete

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new FileVariantWriter(channel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(true)
      writer.write(false)

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp(millis))

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.mode = Datatype.String
      writer.write("World!")

    }

    writer.close

    println("FileVariantWriter written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantReader(file: File, amount: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantReader(channel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      val boolTrueValue = reader.readBool
      val boolFalseValue = reader.readBool
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(12), error("Value unexpected: " + minute))

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(34), error("Value unexpected: " + second))

      reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hello, error("Value unexpected: " + symbol))

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "World!", error("Value unexpected: " + string))

    }

    reader.close

    println("FileVariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperWriter(file: File, amount: Int) {

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock
    val writer = new FileVariantMapper(channel)(MapFilter.ReadWrite)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      writer.mode = Datatype.Bool
      writer.write(false)
      writer.write(true)

      writer.mode = Datatype.Byte
      writer.write(Byte.MaxValue)
      writer.write(Byte.MinValue)

      writer.mode = Datatype.Char
      writer.write(Char.MaxValue)
      writer.write(Char.MinValue)

      writer.mode = Datatype.Double
      writer.write(Double.MaxValue)
      writer.write(Double.MinValue)

      writer.mode = Datatype.Float
      writer.write(Float.MaxValue)
      writer.write(Float.MinValue)

      writer.mode = Datatype.Int
      writer.write(Int.MaxValue)
      writer.write(Int.MinValue)

      writer.mode = Datatype.Long
      writer.write(Long.MaxValue)
      writer.write(Long.MinValue)

      writer.mode = Datatype.Short
      writer.write(Short.MaxValue)
      writer.write(Short.MinValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(24))

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(56))

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp(millis))

      writer.mode = Datatype.Symbol
      writer.write('Hoi, true)

      writer.mode = Datatype.String
      writer.write("Wält!")

    }

    writer.close

    println("FileVariantMapper written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperReader(file: File, amount: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantMapper(channel)(MapFilter.ReadOnly)
    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      val boolFalseValue = reader.readBool
      val boolTrueValue = reader.readBool
      assert(boolFalseValue == false, error("Value unexpected: " + boolFalseValue))
      assert(boolTrueValue == true, error("Value unexpected: " + boolTrueValue))

      reader.mode = Datatype.Byte
      val byteMaxValue = reader.read
      val byteMinValue = reader.read
      assert(byteMaxValue == Byte.MaxValue, error("Value unexpected: " + byteMaxValue))
      assert(byteMinValue == Byte.MinValue, error("Value unexpected: " + byteMinValue))

      reader.mode = Datatype.Char
      val charMaxValue = reader.readChar
      val charMinValue = reader.readChar
      val charMaxValueAsInt = charMaxValue.toInt
      val charMinValueAsInt = charMinValue.toInt
      assert(charMaxValueAsInt == Char.MaxValue.toInt, error("Value unexpected: " + charMaxValueAsInt))
      assert(charMinValueAsInt == Char.MinValue.toInt, error("Value unexpected: " + charMinValueAsInt))

      reader.mode = Datatype.Double
      val doubleMaxValue = reader.readDouble
      val doubleMinValue = reader.readDouble
      assert(doubleMaxValue == Double.MaxValue, error("Value unexpected: " + doubleMaxValue))
      assert(doubleMinValue == Double.MinValue, error("Value unexpected: " + doubleMinValue))

      reader.mode = Datatype.Float
      val floatMaxValue = reader.readFloat
      val floatMinValue = reader.readFloat
      assert(floatMaxValue == Float.MaxValue, error("Value unexpected: " + floatMaxValue))
      assert(floatMinValue == Float.MinValue, error("Value unexpected: " + floatMinValue))

      reader.mode = Datatype.Int
      val intMaxValue = reader.readInt
      val intMinValue = reader.readInt
      assert(intMaxValue == Int.MaxValue, error("Value unexpected: " + intMaxValue))
      assert(intMinValue == Int.MinValue, error("Value unexpected: " + intMinValue))

      reader.mode = Datatype.Long
      val longMaxValue = reader.readLong
      val longMinValue = reader.readLong
      assert(longMaxValue == Long.MaxValue, error("Value unexpected: " + longMaxValue))
      assert(longMinValue == Long.MinValue, error("Value unexpected: " + longMinValue))

      reader.mode = Datatype.Short
      val shortMaxValue = reader.readShort
      val shortMinValue = reader.readShort
      assert(shortMaxValue == Short.MaxValue, error("Value unexpected: " + shortMaxValue))
      assert(shortMinValue == Short.MinValue, error("Value unexpected: " + shortMinValue))

      reader.mode = Datatype.Month
      val month = reader.readMonth
      assert(month == new YearMonth(millis), error("Value unexpected: " + month))

      reader.mode = Datatype.Date
      val date = reader.readDate
      assert(date == new LocalDate(millis), error("Value unexpected: " + date))

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      assert(minute == Minutes.minutes(24), error("Value unexpected: " + minute))

      reader.mode = Datatype.Second
      val second = reader.readSecond
      assert(second == Seconds.seconds(56), error("Value unexpected: " + second))

      reader.mode = Datatype.Time
      val time = reader.readTime
      assert(time == new LocalTime(millis), error("Value unexpected: " + time))

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      assert(dateTime == new LocalDateTime(millis), error("Value unexpected: " + dateTime))

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      assert(timestamp == new Timestamp(millis), error("Value unexpected: " + timestamp))

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      assert(symbol == 'Hoi, error("Value unexpected: " + symbol))

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      assert(string == "Wält!", error("Value unexpected: " + string))

    }

    reader.close

    println("FileVariantMapper read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

