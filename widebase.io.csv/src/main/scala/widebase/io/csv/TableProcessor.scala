package widebase.io.csv

import java.io. {

  BufferedReader,
  File,
  FileInputStream,
  FileReader,
  InputStreamReader,
  RandomAccessFile

}

import java.util.zip. { GZIPInputStream, ZipInputStream }
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

import scala.collection.mutable. { ArrayBuffer, LinkedHashMap, Map }

import vario.data.Datatype
import vario.file.FileVariantWriter
import vario.filter.StreamFilter

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
  StringColumn,

  TypedColumn

}

import widebase.db.table.Table
import widebase.io.csv.filter.ZipFilter
import widebase.io.filter.MagicId
import widebase.io.table.PartitionDomain

/** Predefined routines to processing CSV files.
 *
 * Example how to import trade data and return table:
 *
 * {{{
 * val trade = csv.table("ZddS", ",", filter.none, "trade.csv.gz", "g")
 * }}}
 *
 * Example how to import trade data into directory table:
 *
 * {{{
 * csv.table.to(cli.path, "trade", "ZddS", ",", filter.none, "trade.csv.gz", "g")
 * }}}
 *
 * Example how to import trade data partitioned by end of day into directory table:
 *
 * {{{
 * csv.table.to(cli.path, "trade", "ZddS", ",", filter.none, "trade.csv.gz", "g")('daily)
 * }}}
 *
 * @author myst3r10n
 */
trait TableProcessor extends Logger with Loggable {

  import vario.data
  import vario.data.Datatype.Datatype
  import vario.filter.StreamFilter.StreamFilter

  import widebase.db
  import widebase.io.csv.filter.ZipFilter.ZipFilter
  import widebase.io.table.PartitionDomain.PartitionDomain

  /** Reads a CSV file and return table.
   *
   * @param tokens tokenized types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename to CSV file
   * @param zipped self-explanatory
   *
   * @return processed [[widebase.db.Table]]
  */
  def apply(
    tokens: String,
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipped: String = ""): Table = {

    val zipFilter =
      if(zipped.contains("g"))
        ZipFilter.Gzip
      else if(zipped.contains("z"))
        ZipFilter.Zlib
      else
        ZipFilter.None

    table(data.by(tokens), delimiter, filter, filename, zipFilter)

  }

  /** Reads a CSV file and return table.
   *
   * @param valueTypes resolved types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename to CSV file
   * @param zipFilter self-explanatory
   *
   * @return processed [[widebase.db.Table]]
  */
  def apply(
    valueTypes: Array[Datatype],
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipFilter: ZipFilter): Table = {

    var reader: BufferedReader = null

    zipFilter match {

      case ZipFilter.None =>
        reader = new BufferedReader(new FileReader(filename))

      case ZipFilter.Gzip =>
        reader = new BufferedReader(new InputStreamReader(
          new GZIPInputStream(new FileInputStream(filename))))

      case ZipFilter.Zlib =>
        reader = new BufferedReader(new InputStreamReader(
          new ZipInputStream(new FileInputStream(filename))))

    }

    var line = reader.readLine

    val columns = LinkedHashMap[String, TypedColumn[_]]()

    if(line == null)
      throw FileEmptyException(filename)

    line.split(delimiter).foreach(
      label => columns += label -> null)

    line = reader.readLine

    while(line != null) {

      var i = 0

      filter(line.split(delimiter)).foreach { value =>

        valueTypes(i) match {
          case Datatype.Bool => 
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new BoolColumn

            if(value == "true")
              columns.values.toBuffer(i).asInstanceOf[BoolColumn] += true
            else if(value == "false")
              columns.values.toBuffer(i).asInstanceOf[BoolColumn] += false
            else {

              val boolValue = value.getBytes()(0)

              if(boolValue == '1')
                columns.values.toBuffer(i).asInstanceOf[BoolColumn] += true
              else if(boolValue == '0')
                columns.values.toBuffer(i).asInstanceOf[BoolColumn] += false
              else
                throw TypeMismatchException(Datatype.Bool, boolValue.toString)

            }

          case Datatype.Byte =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new ByteColumn

            columns.values.toBuffer(i).asInstanceOf[ByteColumn] += java.lang.Byte.valueOf(value)

          case Datatype.Char =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new CharColumn

            columns.values.toBuffer(i).asInstanceOf[CharColumn] += value.toCharArray.head

          case Datatype.Double =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new DoubleColumn

            columns.values.toBuffer(i).asInstanceOf[DoubleColumn] += value.toDouble

          case Datatype.Float =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new FloatColumn

            columns.values.toBuffer(i).asInstanceOf[FloatColumn] += value.toFloat

          case Datatype.Int =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new IntColumn

            columns.values.toBuffer(i).asInstanceOf[IntColumn] += value.toInt

          case Datatype.Long =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new LongColumn

            columns.values.toBuffer(i).asInstanceOf[LongColumn] += value.toLong

          case Datatype.Short =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new ShortColumn

            columns.values.toBuffer(i).asInstanceOf[ShortColumn] += value.toShort

          case Datatype.Month =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new MonthColumn

            columns.values.toBuffer(i).asInstanceOf[MonthColumn] += new YearMonth(value.toLong)

          case Datatype.Date =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new DateColumn

            columns.values.toBuffer(i).asInstanceOf[DateColumn] += new LocalDate(value.toLong)

          case Datatype.Minute =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new MinuteColumn

            columns.values.toBuffer(i).asInstanceOf[MinuteColumn] += Minutes.minutes(value.toInt)

          case Datatype.Second =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new SecondColumn

            columns.values.toBuffer(i).asInstanceOf[SecondColumn] += Seconds.seconds(value.toInt)

          case Datatype.Time =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new TimeColumn

            columns.values.toBuffer(i).asInstanceOf[TimeColumn] += new LocalTime(value.toLong)

          case Datatype.DateTime =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new DateTimeColumn

            columns.values.toBuffer(i).asInstanceOf[DateTimeColumn] += new LocalDateTime(value.toLong)

          case Datatype.Timestamp =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new TimestampColumn

            columns.values.toBuffer(i).asInstanceOf[TimestampColumn] += new Timestamp(value.toLong)

          case Datatype.Symbol =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new SymbolColumn

            columns.values.toBuffer(i).asInstanceOf[SymbolColumn] += Symbol(value)

          case Datatype.String =>
            if(columns.values.toBuffer(i) == null)
              columns(columns.keys.toBuffer(i)) = new StringColumn

            columns.values.toBuffer(i).asInstanceOf[StringColumn] += value

        }

        i += 1

      }

      line = reader.readLine

    }

    reader.close

    val table = new Table

    columns foreach { case (name, column) => table ++= (name, column) }

    table

  }

  /** Write a CSV file into database.
   *
   * @param path to database
   * @param table name of table
   * @param tokens tokenized value types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename to CSV file
   * @param zipped self-explanatory
   * @param parted partition symbol
   * @param segmented path to segment
  */
  def to(
    path: String,
    table: String,
    tokens: String,
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipped: String = "")
    (implicit parted: Symbol = null, segmented: File = null) {

    val zipFilter =
      if(zipped.contains("g"))
        ZipFilter.Gzip
      else if(zipped.contains("z"))
        ZipFilter.Zlib
      else
        ZipFilter.None

    val domain =
      if(parted == null)
        null
      else
        parted match {

        case parted if
          parted == 'd ||
          parted == 'daily => PartitionDomain.Daily
        case parted if
          parted == 'm ||
          parted == 'monthly => PartitionDomain.Monthly
        case parted if
          parted == 'y ||
          parted == 'yearly => PartitionDomain.Yearly

      }

    to(
      new File(path),
      table,
      data.by(tokens),
      delimiter,
      filter,
      filename,
      zipFilter)(domain, segmented)

  }

  /** Write a CSV file into database
   *
   * @param path to database
   * @param table name of table
   * @param valueTypes resolved value types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename the to CSV file
   * @param zipFilter self-explanatory
   * @param domain of partition
   * @param segmented path to segment
  */
  protected def to(
    path: File,
    table: String,
    valueTypes: Array[Datatype],
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipFilter: ZipFilter)
    (implicit domain: PartitionDomain, segmented: File) {

    var reader: BufferedReader = null

    zipFilter match {

      case ZipFilter.None =>
        reader = new BufferedReader(new FileReader(filename))

      case ZipFilter.Gzip =>
        reader = new BufferedReader(new InputStreamReader(
          new GZIPInputStream(new FileInputStream(filename))))

      case ZipFilter.Zlib =>
        reader = new BufferedReader(new InputStreamReader(
          new ZipInputStream(new FileInputStream(filename))))

    }

    var line = reader.readLine

    if(line == null)
      throw FileEmptyException(filename)

    val labels = new StringColumn
    line.split(delimiter).foreach(label => labels += label)

    class Companion(var writer: FileVariantWriter, var lastEnded: Long)

    var dir: File = null
    val writers = ArrayBuffer[FileVariantWriter]()
    var symbolCompanions = Map[Int, Companion]()
    var stringCompanions = Map[Int, Companion]()

    def initWriters {

      var valueType = valueTypes.toIterator

      labels.foreach { label =>

        val file = new File(dir.getPath + "/" + label)

        if(file.exists)
          file.delete

        val filename = dir.getPath + "/" + label

        val valueTypeOf = valueType.next

        val channel = new RandomAccessFile(filename, "rw").getChannel
        channel.tryLock
        writers += new FileVariantWriter(channel) {

          override val charset = props.charsets.to

          override def capacity = props.capacities.to
          override def order = props.orders.to

          // Write magic
          mode = Datatype.String
          write(MagicId.Column.toString)

          // Write column type
          mode = Datatype.Byte
          write(valueTypeOf.id.toByte)

          // Write column length
          mode = Datatype.Int
          write(0)

          // Set column value type
          if(valueTypeOf == Datatype.Symbol || valueTypeOf == Datatype.String)
            mode = Datatype.Long
          else
            mode = valueTypeOf

        }

        if(valueTypeOf == Datatype.String || valueTypeOf == Datatype.Symbol) {

          var companionFile: File = null

          if(valueTypeOf == Datatype.Symbol)
            companionFile = new File(file.getPath + ".sym")
          else if(valueTypeOf == Datatype.String)
            companionFile = new File(file.getPath + ".str")

          if(companionFile.exists)
            companionFile.delete

          val channel =
            new RandomAccessFile(companionFile.getPath, "rw").getChannel
          channel.tryLock

          val writer = new FileVariantWriter(channel) {

            override val charset = props.charsets.to

            override def capacity = props.capacities.to
            override def order = props.orders.to

            mode = valueTypeOf

          }

          valueTypeOf match {

            case Datatype.Symbol =>
              symbolCompanions += writers.size - 1 -> new Companion(writer, 0L)

            case Datatype.String =>
              stringCompanions += writers.size - 1 -> new Companion(writer, 0L)

          }
        }
      }
    }

    def writeRecords(records: Int) {

      writers.foreach { writer =>

        if(writer.isOpen) {

          writer.flush
          writer.mode = Datatype.Byte
          writer.position =
            (MagicId.Column.toString .getBytes(writer.charset)).size + 1

          writer.mode = Datatype.Int
          writer.write(records)

        }
      }
    }

    def releaseWriters {

      writers.foreach(writer =>
        if(writer.isOpen) writer.close)
      writers.clear

      symbolCompanions.values.foreach(companion =>
        if(companion.writer.isOpen)
          companion.writer.close)
      symbolCompanions.clear

      stringCompanions.values.foreach(companion =>
        if(companion.writer.isOpen)
          companion.writer.close)
      stringCompanions.clear

    }

    if(domain == null) {

      dir =
        if(segmented == null)
          new File(path.getPath + "/" + table)
        else
          new File(segmented.getPath + "/" + table)

      if(!dir.exists)
        dir.mkdir

      initWriters

    }

    val dbi =
      if(segmented == null)
        db.instance(path.getPath)
      else
        db.instance(segmented.getPath)

    import dbi.tables._

    var numberOfRecordsOnPart = 0
    var numberOfRecordsLoaded = 0

    var lastInt: Option[Int] = scala.None
    var lastDate: LocalDate = null

    line = reader.readLine

    while(line != null) {

      val record = filter(line.split(delimiter))

      var partition: String = null

      if(domain != null)
        domain match {

          case PartitionDomain.Int =>
            val int = record(0).toInt

            var newPartition = false

            if(lastInt.isEmpty)
              newPartition = true
            else if(lastInt.get != int) {

              info("End of int " + int + " passed, +" + numberOfRecordsOnPart +
                " records")

              newPartition = true

            }

            if(newPartition) {

              lastInt = Some(int)
              partition = lastInt.get.toString

            }

          case domain if
            domain == PartitionDomain.Yearly ||
            domain == PartitionDomain.Monthly ||
            domain == PartitionDomain.Daily =>

            var newPartition = false

            if(lastDate == null)
              newPartition = true
            else {

              val today = new LocalDate(record(0).toLong)

              domain match {

                case PartitionDomain.Yearly =>
                  if(lastDate.getYear != today.getYear) {

                    info("End of year " + lastDate.toString("yyyy") +
                      " passed, +" + numberOfRecordsOnPart + " records")

                    newPartition = true

                  }

                case PartitionDomain.Monthly =>
                  if(
                    lastDate.getYear != today.getYear ||
                    lastDate.getMonthOfYear != today.getMonthOfYear) {

                    info("End of month " + lastDate.toString("yyyy-MM") +
                      " passed, +" + numberOfRecordsOnPart + " records")

                    newPartition = true

                  }

                case PartitionDomain.Daily =>
                  if(
                    lastDate.getYear != today.getYear ||
                    lastDate.getMonthOfYear != today.getMonthOfYear ||
                    lastDate.getDayOfMonth != today.getDayOfMonth) {

                    info("End of day " + lastDate.toString("yyyy-MM-dd") +
                      " passed, +" + numberOfRecordsOnPart + " records")

                    newPartition = true

                  }
              }
            }

            if(newPartition) {

              lastDate = new LocalDate(record(0).toLong)

              domain match {

                case PartitionDomain.Yearly => partition = lastDate.toString("yyyy")
                case PartitionDomain.Monthly => partition = lastDate.toString("yyyy-MM")
                case PartitionDomain.Daily => partition = lastDate.toString("yyyy-MM-dd")

              }
            }
        }

      if(partition != null) {

        writeRecords(numberOfRecordsOnPart)
        releaseWriters

        // Set column labels
        save.col(table, ".d", labels)(partition)

        dir =
          if(segmented == null)
            new File(path.getPath + "/" + partition + "/" + table)
          else
            new File(segmented.getPath + "/" + partition + "/" + table)

        initWriters
        numberOfRecordsOnPart = 0

      }

      var i = 0

      record.foreach { value =>

        valueTypes(i) match {

          case Datatype.Bool => 
            if(value == "true")
              writers(i).write(true)
            else if(value == "false")
              writers(i).write(false)
            else {

              val boolValue = value.getBytes()(0)

              if(boolValue == '1')
                writers(i).write(true)
              else if(boolValue == '0')
                writers(i).write(false)
              else
                throw TypeMismatchException(Datatype.Bool, boolValue.toString)

            }

          case Datatype.Byte => writers(i).write(java.lang.Byte.valueOf(value))
          case Datatype.Char => writers(i).write(value.toCharArray.head)
          case Datatype.Double => writers(i).write(value.toDouble)
          case Datatype.Float => writers(i).write(value.toFloat)
          case Datatype.Int => writers(i).write(value.toInt)
          case Datatype.Long => writers(i).write(value.toLong)
          case Datatype.Short => writers(i).write(value.toShort)
          case Datatype.Month => writers(i).write(new YearMonth(value.toLong))
          case Datatype.Date => writers(i).write(new LocalDate(value.toLong))
          case Datatype.Minute => writers(i).write(Minutes.minutes(value.toInt))
          case Datatype.Second => writers(i).write(Seconds.seconds(value.toInt))
          case Datatype.Time => writers(i).write(new LocalTime(value.toLong))
          case Datatype.DateTime => writers(i).write(new LocalDateTime(value.toLong))
          case Datatype.Timestamp => writers(i).write(new Timestamp(value.toLong))

          case Datatype.Symbol =>
            symbolCompanions(i).lastEnded += value.toString.getBytes(
                symbolCompanions(i).writer.charset).size

            writers(i).write(symbolCompanions(i).lastEnded)
            symbolCompanions(i).writer.write(value)

          case Datatype.String =>
            stringCompanions(i).lastEnded +=
              value.getBytes(stringCompanions(i).writer.charset).size

            writers(i).write(stringCompanions(i).lastEnded)
            stringCompanions(i).writer.write(value)

        }

        i += 1

      }

      numberOfRecordsOnPart += 1
      numberOfRecordsLoaded += 1

      line = reader.readLine

    }

    reader.close

    writeRecords(numberOfRecordsOnPart)
    releaseWriters

    info("All data written succesfully, number of records written: " +
      numberOfRecordsLoaded)

  }
}

