package widebase.io.file.test

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

  protected var debug: Boolean = _
  protected var amount: Int = _
  protected var filter: StreamFilter = _
  protected var length: Int = _
  protected var level: Int = _

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    amount = 250
    filter = StreamFilter.None
    length = 100
    level = CompressionLevel.Default

    var i = 0

    while(i < args.size) {

      args(i) match {

        case "-d" => debug = true

        case "-a" =>
          i += 1
          amount = args(i).toInt

        case "-c" =>
          i += 1
          level = args(i).toInt

        case "-f" =>
          i += 1

          args(i) match {

            case "g" => filter = StreamFilter.Gzip
            case "z" => filter = StreamFilter.Zlib
          
          }

        case "-l" =>
          i += 1
          length = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

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

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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

    info("VariantWriter written " + amount + " records in " +
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
      val boolTrueValue = reader.readBool(length).last
      val boolFalseValue = reader.readBool(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read(length).last
      val byteMaxValue = reader.read(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar(length).last
      val charMaxValue = reader.readChar(length).last
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble(length).last
      val doubleMaxValue = reader.readDouble(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat(length).last
      val floatMaxValue = reader.readFloat(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt(length).last
      val intMaxValue = reader.readInt(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong(length).last
      val longMaxValue = reader.readLong(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort(length).last
      val shortMaxValue = reader.readShort(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)


      reader.mode = Datatype.Month
      for(i <- 1 to length - 1)
        reader.readMonth
      val monthValue = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + monthValue)

      reader.mode = Datatype.Date
      val dateValue = reader.readDate(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateValue)

      reader.mode = Datatype.Minute
      val minuteValue = reader.readMinute(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minuteValue)

      reader.mode = Datatype.Second
      val secondValue = reader.readMinute(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + secondValue)

      reader.mode = Datatype.Time
      val timeValue = reader.readTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timeValue)

      reader.mode = Datatype.DateTime
      val dateTimeValue = reader.readDateTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTimeValue)

      reader.mode = Datatype.Timestamp
      for(i <- 1 to length - 1)
        reader.readTimestamp
      val timestampValue = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestampValue)

      reader.mode = Datatype.Symbol
      for(i <- 1 to length - 1)
        reader.readSymbol
      val symbolValue = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbolValue)

      reader.mode = Datatype.String
      for(i <- 1 to length - 1)
        reader.readString("World!".getBytes(reader.charset).size)
      val stringValue = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + stringValue)

    }

    reader.close

    info("VariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantWriter(file: File, amount: Int, length: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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

    info("FileVariantWriter written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantReader(file: File, amount: Int, length: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantReader(channel)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      val boolTrueValue = reader.readBool(length).last
      val boolFalseValue = reader.readBool(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read(length).last
      val byteMaxValue = reader.read(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar(length).last
      val charMaxValue = reader.readChar(length).last
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble(length).last
      val doubleMaxValue = reader.readDouble(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat(length).last
      val floatMaxValue = reader.readFloat(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt(length).last
      val intMaxValue = reader.readInt(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong(length).last
      val longMaxValue = reader.readLong(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort(length).last
      val shortMaxValue = reader.readShort(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)

      reader.mode = Datatype.Month
      for(i <- 1 to length - 1)
        reader.readMonth
      val monthValue = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + monthValue)

      reader.mode = Datatype.Date
      val dateValue = reader.readDate(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateValue)

      reader.mode = Datatype.Minute
      val minuteValue = reader.readMinute(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minuteValue)

      reader.mode = Datatype.Second
      val secondValue = reader.readSecond(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + secondValue)

      reader.mode = Datatype.Time
      val timeValue = reader.readTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timeValue)

      reader.mode = Datatype.DateTime
      val dateTimeValue = reader.readDateTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTimeValue)

      reader.mode = Datatype.Timestamp
      for(i <- 1 to length - 1)
        reader.readTimestamp
      val timestampValue = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestampValue)

      reader.mode = Datatype.Symbol
      for(i <- 1 to length - 1)
        reader.readSymbol
      val symbolValue = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbolValue)

      reader.mode = Datatype.String
      for(i <- 1 to length - 1)
        reader.readString("World!".getBytes(reader.charset).size)
      val stringValue = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + stringValue)

    }

    reader.close

    info("FileVariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperWriter(file: File, amount: Int, length: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-02-01 23:45:01.234").getTime

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

    info("FileVariantMapper written " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperReader(file: File, amount: Int, length: Int) {

    val channel = new RandomAccessFile(file.getPath, "r").getChannel
    val reader = new FileVariantMapper(channel)(MapFilter.ReadOnly)

    val started = System.currentTimeMillis

    for(i <- 1 to amount) {

      reader.mode = Datatype.Bool
      val boolFalseValue = reader.readBool(length).last
      val boolTrueValue = reader.readBool(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolFalseValue + " != " +
          boolTrueValue)

      reader.mode = Datatype.Byte
      val byteMaxValue = reader.read(length).last
      val byteMinValue = reader.read(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMaxValue + " > " +
          byteMinValue)

      reader.mode = Datatype.Char
      val charMaxValue = reader.readChar(length).last
      val charMinValue = reader.readChar(length).last
      val charMaxValueAsInt = charMaxValue.toInt
      val charMinValueAsInt = charMinValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMaxValueAsInt + " > " +
          charMinValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMaxValue = reader.readDouble(length).last
      val doubleMinValue = reader.readDouble(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMaxValue + " > " +
          doubleMinValue)

      reader.mode = Datatype.Float
      val floatMaxValue = reader.readFloat(length).last
      val floatMinValue = reader.readFloat(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMaxValue + " > " +
          floatMinValue)

      reader.mode = Datatype.Int
      val intMaxValue = reader.readInt(length).last
      val intMinValue = reader.readInt(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMaxValue + " > " + intMinValue)

      reader.mode = Datatype.Long
      val longMaxValue = reader.readLong(length).last
      val longMinValue = reader.readLong(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMaxValue + " > " +
          longMinValue)

      reader.mode = Datatype.Short
      val shortMaxValue = reader.readShort(length).last
      val shortMinValue = reader.readShort(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMaxValue + " > " +
          shortMinValue)

      reader.mode = Datatype.Month
      for(i <- 1 to length - 1)
        reader.readMonth
      val monthValue = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + monthValue)

      reader.mode = Datatype.Date
      val dateValue = reader.readDate(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateValue)

      reader.mode = Datatype.Minute
      val minuteValue = reader.readMinute(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minuteValue)

      reader.mode = Datatype.Second
      val secondValue = reader.readSecond(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + secondValue)

      reader.mode = Datatype.Time
      val timeValue = reader.readTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timeValue)

      reader.mode = Datatype.DateTime
      val dateTimeValue = reader.readDateTime(length).last
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTimeValue)

      reader.mode = Datatype.Timestamp
      for(i <- 1 to length - 1)
        reader.readTimestamp
      val timestampValue = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestampValue)

      reader.mode = Datatype.Symbol
      for(i <- 1 to length - 1)
        reader.readSymbol
      val symbolValue = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbolValue)

      reader.mode = Datatype.String
      for(i <- 1 to length - 1)
        reader.readString("World!".getBytes(reader.charset).size)
      val stringValue = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + stringValue)

    }

    reader.close

    info("FileVariantMapper read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

