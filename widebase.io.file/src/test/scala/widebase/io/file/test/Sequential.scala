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

/* Test of read and write operation.
 *
 * @note Writes all to file and read all from file (incl. memory mapped file).
 * @note It's fast and good for mixed mode validation of read and write operations.
 *
 * @author myst3r10n
 */
object Sequential extends Logger with Loggable {

  import widebase.io.filter.StreamFilter.StreamFilter

  protected var debug: Boolean = _
  protected var amount: Int = _
  protected var filter: StreamFilter = _
  protected var level: Int = _

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    amount = 25000
    filter = StreamFilter.None
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

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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

    info("VariantWriter written " + amount + " records in " +
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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    reader.close

    info("VariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantWriter(file: File, amount: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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

    info("FileVariantWriter written " + amount + " records in " +
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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    reader.close

    info("FileVariantReader read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapperWriter(file: File, amount: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-02-01 23:45:01.234").getTime

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

    info("FileVariantMapper written " + amount + " records in " +
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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolFalseValue + " != " +
          boolTrueValue)

      reader.mode = Datatype.Byte
      val byteMaxValue = reader.read
      val byteMinValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMaxValue + " > " +
          byteMinValue)

      reader.mode = Datatype.Char
      val charMaxValue = reader.readChar
      val charMinValue = reader.readChar
      val charMaxValueAsInt = charMaxValue.toInt
      val charMinValueAsInt = charMinValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMaxValueAsInt + " > " +
          charMinValueAsInt + " (both as Int)")

      reader.mode = Datatype.Double
      val doubleMaxValue = reader.readDouble
      val doubleMinValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMaxValue + " > " +
          doubleMinValue)

      reader.mode = Datatype.Float
      val floatMaxValue = reader.readFloat
      val floatMinValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMaxValue + " > " +
          floatMinValue)

      reader.mode = Datatype.Int
      val intMaxValue = reader.readInt
      val intMinValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMaxValue + " > " + intMinValue)

      reader.mode = Datatype.Long
      val longMaxValue = reader.readLong
      val longMinValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMaxValue + " > " +
          longMinValue)

      reader.mode = Datatype.Short
      val shortMaxValue = reader.readShort
      val shortMinValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMaxValue + " > " +
          shortMinValue)

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      reader.mode = Datatype.String
      val string = reader.readString("Wält!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    reader.close

    info("FileVariantMapper read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

