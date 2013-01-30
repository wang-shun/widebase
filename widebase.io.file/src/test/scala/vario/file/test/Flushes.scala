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

/* Test of flush operation.
 *
 * @note Writes one to file and read one from file (incl. memory mapped file).
 * @note Consums many times regarding many flushes, but good for debugging of read and write operations.
 *
 * @author myst3r10n
 */
object Flushes extends Logger with Loggable {

  import widebase.io.filter.StreamFilter.StreamFilter

  protected var debug: Boolean = _
  protected var amount: Int = _

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    amount = 2500

    var i = 0

    while(i < args.size) {

      args(i) match {

        case "-d" => debug = true

        case "-a" =>
          i += 1
          amount = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

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

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.flush

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.flush

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.flush

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.flush

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.flush

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.flush

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.flush

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.flush

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.flush

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.flush

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.flush

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.flush

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.flush

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      writer.flush

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.flush

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      writer.mode = Datatype.String
      writer.write("World!")

      writer.flush

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    writer.close
    reader.close

    println("Variant written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariant(file: File, amount: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-01-23 12:34:56.789").getTime

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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolTrueValue + " != " +
          boolFalseValue)

      writer.mode = Datatype.Byte
      writer.write(Byte.MinValue)
      writer.write(Byte.MaxValue)

      writer.flush

      reader.mode = Datatype.Byte
      val byteMinValue = reader.read
      val byteMaxValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMinValue + " < " +
          byteMaxValue)

      writer.mode = Datatype.Char
      writer.write(Char.MinValue)
      writer.write(Char.MaxValue)

      writer.flush

      reader.mode = Datatype.Char
      val charMinValue = reader.readChar
      val charMaxValue = reader.readChar
      val charMinValueAsInt = charMinValue.toInt
      val charMaxValueAsInt = charMaxValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMinValueAsInt + " < " +
          charMaxValueAsInt + " (both as Int)")

      writer.mode = Datatype.Double
      writer.write(Double.MinValue)
      writer.write(Double.MaxValue)

      writer.flush

      reader.mode = Datatype.Double
      val doubleMinValue = reader.readDouble
      val doubleMaxValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMinValue + " < " +
          doubleMaxValue)

      writer.mode = Datatype.Float
      writer.write(Float.MinValue)
      writer.write(Float.MaxValue)

      writer.flush

      reader.mode = Datatype.Float
      val floatMinValue = reader.readFloat
      val floatMaxValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMinValue + " < " +
          floatMaxValue)

      writer.mode = Datatype.Int
      writer.write(Int.MinValue)
      writer.write(Int.MaxValue)

      writer.flush

      reader.mode = Datatype.Int
      val intMinValue = reader.readInt
      val intMaxValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMinValue + " < " + intMaxValue)

      writer.mode = Datatype.Long
      writer.write(Long.MinValue)
      writer.write(Long.MaxValue)

      writer.flush

      reader.mode = Datatype.Long
      val longMinValue = reader.readLong
      val longMaxValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMinValue + " < " +
          longMaxValue)

      writer.mode = Datatype.Short
      writer.write(Short.MinValue)
      writer.write(Short.MaxValue)

      writer.flush

      reader.mode = Datatype.Short
      val shortMinValue = reader.readShort
      val shortMaxValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMinValue + " < " +
          shortMaxValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      writer.flush

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      writer.flush

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(12))

      writer.flush

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(34))

      writer.flush

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      writer.flush

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      writer.flush

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      writer.flush

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      writer.mode = Datatype.Symbol
      writer.write('Hello, true)

      writer.flush

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      writer.mode = Datatype.String
      writer.write("World!")

      writer.flush

      reader.mode = Datatype.String
      val string = reader.readString("World!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    writer.close
    reader.close

    println("FileVariant written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def fileVariantMapper(file: File, amount: Int) {

    val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
      .parse("2012-02-01 23:45:01.234").getTime

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
      if(debug || i == amount)
        println(reader.mode.toString + ": " + boolFalseValue + " != " +
          boolTrueValue)

      writer.mode = Datatype.Byte
      writer.write(Byte.MaxValue)
      writer.write(Byte.MinValue)

      reader.mode = Datatype.Byte
      val byteMaxValue = reader.read
      val byteMinValue = reader.read
      if(debug || i == amount)
        println(reader.mode.toString + ": " + byteMaxValue + " > " +
          byteMinValue)

      writer.mode = Datatype.Char
      writer.write(Char.MaxValue)
      writer.write(Char.MinValue)

      reader.mode = Datatype.Char
      val charMaxValue = reader.readChar
      val charMinValue = reader.readChar
      val charMaxValueAsInt = charMaxValue.toInt
      val charMinValueAsInt = charMinValue.toInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + charMaxValueAsInt + " > " +
          charMinValueAsInt + " (both as Int)")

      writer.mode = Datatype.Double
      writer.write(Double.MaxValue)
      writer.write(Double.MinValue)

      reader.mode = Datatype.Double
      val doubleMaxValue = reader.readDouble
      val doubleMinValue = reader.readDouble
      if(debug || i == amount)
        println(reader.mode.toString + ": " + doubleMaxValue + " > " +
          doubleMinValue)

      writer.mode = Datatype.Float
      writer.write(Float.MaxValue)
      writer.write(Float.MinValue)

      reader.mode = Datatype.Float
      val floatMaxValue = reader.readFloat
      val floatMinValue = reader.readFloat
      if(debug || i == amount)
        println(reader.mode.toString + ": " + floatMaxValue + " > " +
          floatMinValue)

      writer.mode = Datatype.Int
      writer.write(Int.MaxValue)
      writer.write(Int.MinValue)

      reader.mode = Datatype.Int
      val intMaxValue = reader.readInt
      val intMinValue = reader.readInt
      if(debug || i == amount)
        println(reader.mode.toString + ": " + intMaxValue + " > " + intMinValue)

      writer.mode = Datatype.Long
      writer.write(Long.MaxValue)
      writer.write(Long.MinValue)

      reader.mode = Datatype.Long
      val longMaxValue = reader.readLong
      val longMinValue = reader.readLong
      if(debug || i == amount)
        println(reader.mode.toString + ": " + longMaxValue + " >" +
          longMinValue)

      writer.mode = Datatype.Short
      writer.write(Short.MaxValue)
      writer.write(Short.MinValue)

      reader.mode = Datatype.Short
      val shortMaxValue = reader.readShort
      val shortMinValue = reader.readShort
      if(debug || i == amount)
        println(reader.mode.toString + ": " + shortMaxValue + " > " +
          shortMinValue)

      writer.mode = Datatype.Month
      writer.write(new YearMonth(millis))

      reader.mode = Datatype.Month
      val month = reader.readMonth
      if(debug || i == amount)
        println(reader.mode.toString + ": " + month)

      writer.mode = Datatype.Date
      writer.write(new LocalDate(millis))

      reader.mode = Datatype.Date
      val date = reader.readDate
      if(debug || i == amount)
        println(reader.mode.toString + ": " + date)

      writer.mode = Datatype.Minute
      writer.write(Minutes.minutes(24))

      reader.mode = Datatype.Minute
      val minute = reader.readMinute
      if(debug || i == amount)
        println(reader.mode.toString + ": " + minute)

      writer.mode = Datatype.Second
      writer.write(Seconds.seconds(56))

      reader.mode = Datatype.Second
      val second = reader.readSecond
      if(debug || i == amount)
        println(reader.mode.toString + ": " + second)

      writer.mode = Datatype.Time
      writer.write(new LocalTime(millis))

      reader.mode = Datatype.Time
      val time = reader.readTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + time)

      writer.mode = Datatype.DateTime
      writer.write(new LocalDateTime(millis))

      reader.mode = Datatype.DateTime
      val dateTime = reader.readDateTime
      if(debug || i == amount)
        println(reader.mode.toString + ": " + dateTime)

      writer.mode = Datatype.Timestamp
      writer.write(new Timestamp((millis)))

      reader.mode = Datatype.Timestamp
      val timestamp = reader.readTimestamp
      if(debug || i == amount)
        println(reader.mode.toString + ": " + timestamp)

      writer.mode = Datatype.Symbol
      writer.write('Hoi, true)

      reader.mode = Datatype.Symbol
      val symbol = reader.readSymbol
      if(debug || i == amount)
        println(reader.mode.toString + ": " + symbol)

      writer.mode = Datatype.String
      writer.write("Wält!")

      reader.mode = Datatype.String
      val string = reader.readString("Wält!".getBytes(reader.charset).size)
      if(debug || i == amount)
        println(reader.mode.toString + ": " + string)

      if(debug && i < amount)
        println("")

    }

    writer.close
    reader.close

    println("FileVariantMapper written/read " + amount + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

