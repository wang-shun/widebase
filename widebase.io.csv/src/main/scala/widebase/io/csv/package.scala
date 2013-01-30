package widebase.io

import java.io. {

  BufferedReader,
  FileInputStream,
  FileReader,
  InputStreamReader

}

import java.util.zip. { GZIPInputStream, ZipInputStream }

import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

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
  StringColumn,

  TypedColumn

}

import widebase.io.csv.filter.ZipFilter

/** CSV I/O.
 *
 * Example how to import data into columns:
 *
 * {{{
 * val columns = csv.columns("ZddS", ",", filter.none, "data.csv.gz", "r")
 * }}}
 *
 * @author myst3r10n
 */
package object csv {

  import widebase.data
  import widebase.data.Datatype.Datatype
  import widebase.io.csv.filter.ZipFilter.ZipFilter

  /** Reference to itself. */
  val ref = this

  /** Predefined routines to processing CSV files. */
  object table extends TableProcessor

  /** Reads columns from csv file.
   *
   * @param tokens tokenized types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename to CSV file
   * @param zipped self-explanatory
   *
   * @return processed array of [[widebase.db.column.VariantColumn]]
  */
  def columns(
    tokens: String,
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipped: String = ""): Array[TypedColumn[_]] = {

    val zipFilter =
      if(zipped.contains("g"))
        ZipFilter.Gzip
      else if(zipped.contains("z"))
        ZipFilter.Zlib
      else
        ZipFilter.None

    columns(data.by(tokens), delimiter, filter, filename, zipFilter)

  }

  /** Reads a CSV file into columns.
   *
   * @param valueTypes resolved types
   * @param delimiter of CSV file
   * @param filter individual processing
   * @param filename to CSV file
   * @param zipFilter compression filter
   *
   * @return processed array of [[widebase.db.column.VariantColumn]]
  */
  def columns(
    valueTypes: Array[Datatype],
    delimiter: String,
    filter: Array[String] => Array[String],
    filename: String,
    zipFilter: ZipFilter): Array[TypedColumn[_]] = {

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

    var columns: Array[TypedColumn[_]] = null

    while(line != null) {

      val csv = filter(line.split(delimiter))

      if(columns == null)
        columns = Array.fill(csv.size)(null)

      var i = 0

      csv.foreach { value =>

        valueTypes(i) match {

          case Datatype.Bool => 
            if(columns(i) == null)
              columns(i) = new BoolColumn

            if(value == "true")
              columns(i).asInstanceOf[BoolColumn] += true
            else if(value == "false")
              columns(i).asInstanceOf[BoolColumn] += false
            else {

              val boolValue = value.getBytes()(0)

              if(boolValue == '1')
                columns(i).asInstanceOf[BoolColumn] += true
              else if(boolValue == '0')
                columns(i).asInstanceOf[BoolColumn] += false
              else
                throw TypeMismatchException(Datatype.Bool, boolValue.toString)

            }

          case Datatype.Byte =>
            if(columns(i) == null)
              columns(i) = new ByteColumn

            columns(i).asInstanceOf[ByteColumn] += java.lang.Byte.valueOf(value)

          case Datatype.Char =>
            if(columns(i) == null)
              columns(i) = new CharColumn

            columns(i).asInstanceOf[CharColumn] += value.toCharArray.head

          case Datatype.Double =>
            if(columns(i) == null)
              columns(i) = new DoubleColumn

            columns(i).asInstanceOf[DoubleColumn] += value.toDouble

          case Datatype.Float =>
            if(columns(i) == null)
              columns(i) = new FloatColumn

            columns(i).asInstanceOf[FloatColumn] += value.toFloat

          case Datatype.Int =>
            if(columns(i) == null)
              columns(i) = new IntColumn

            columns(i).asInstanceOf[IntColumn] += value.toInt

          case Datatype.Long =>
            if(columns(i) == null)
              columns(i) = new LongColumn

            columns(i).asInstanceOf[LongColumn] += value.toLong

          case Datatype.Short =>
            if(columns(i) == null)
              columns(i) = new ShortColumn

            columns(i).asInstanceOf[ShortColumn] += value.toShort

          case Datatype.Month =>
            if(columns(i) == null)
              columns(i) = new MonthColumn

            columns(i).asInstanceOf[MonthColumn] += new YearMonth(value.toLong)

          case Datatype.Date =>
            if(columns(i) == null)
              columns(i) = new DateColumn

            columns(i).asInstanceOf[DateColumn] += new LocalDate(value.toLong)

          case Datatype.Minute =>
            if(columns(i) == null)
              columns(i) = new MinuteColumn

            columns(i).asInstanceOf[MinuteColumn] += Minutes.minutes(value.toInt)

          case Datatype.Second =>
            if(columns(i) == null)
              columns(i) = new SecondColumn

            columns(i).asInstanceOf[SecondColumn] += Seconds.seconds(value.toInt)

          case Datatype.Time =>
            if(columns(i) == null)
              columns(i) = new TimeColumn

            columns(i).asInstanceOf[TimeColumn] += new LocalTime(value.toLong)

          case Datatype.DateTime =>
            if(columns(i) == null)
              columns(i) = new DateTimeColumn

            columns(i).asInstanceOf[DateTimeColumn] += new LocalDateTime(value.toLong)

          case Datatype.Timestamp =>
            if(columns(i) == null)
              columns(i) = new TimestampColumn

            columns(i).asInstanceOf[TimestampColumn] += new Timestamp(value.toLong)

          case Datatype.Symbol =>
            if(columns(i) == null)
              columns(i) = new SymbolColumn

            columns(i).asInstanceOf[SymbolColumn] += Symbol(value)

          case Datatype.String =>
            if(columns(i) == null)
              columns(i) = new StringColumn

            columns(i).asInstanceOf[StringColumn] += value

        }

        i += 1

      }

      line = reader.readLine

    }

    reader.close

    columns

  }

  /** Scope properties. */
  def props = Props

}

