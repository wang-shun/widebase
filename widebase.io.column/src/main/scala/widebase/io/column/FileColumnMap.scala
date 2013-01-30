package widebase.io.column

import java.io. { File, RandomAccessFile }
import java.nio.channels.FileChannel

import scala.collection.mutable.ArrayBuffer

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

import widebase.io.file. { FileVariantMapper, FileVariantReader }

import widebase.io.filter. {

  InvalidMagicException,
  MagicId,
  MagicNotFoundException,
  MapFilter,
  StreamFilter,
  WrongMagicException

}

/** Map columns from directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnMap(path: String) {

  import widebase.data

  /** Map columns from directory table.
    *
    * @param name of table
    * @param label label of column
    * @param amount values to map, 0 map all
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return [[widebase.db.column.TypedColumn]]
   */
  def apply(
    name: String,
    label: Any,
    amount: Int = 0)
    (implicit parted: String = null, segmented: File = null) = {

    var filename =
      if(segmented == null)
        path
      else
        segmented.getPath

    if(parted != null)
      filename += "/" + parted

    filename +=  "/" + name + "/" + label.toString

    val reader = new FileVariantReader(
      new RandomAccessFile(filename, "r").getChannel) {

      override val charset = props.charsets.mapper

      override def order = props.orders.mapper

    }

    // Read magic
    reader.mode = Datatype.String
    val magic =
      reader.readString(MagicId.Column.toString.getBytes(reader.charset).size)

    if(magic.isEmpty)
      throw MagicNotFoundException(filename)

    try {

      if(MagicId.withName(magic) != MagicId.Column)
        throw WrongMagicException(filename, magic)

    } catch {
      case e: NoSuchElementException =>
        throw InvalidMagicException(filename, magic)
    }

    // Read column type
    if(reader.mode != Datatype.Byte)
      reader.mode = Datatype.Byte

    val typeOf = Datatype(reader.read)

    // Read column length
    reader.mode = Datatype.Int
    val records =
      if(amount > 0)
        amount
      else
        reader.readInt

    reader.close

    // Map column
    val offset = (MagicId.Column.toString.getBytes(reader.charset)).size + 1 +
      data.sizeOf.int

    val channel = new RandomAccessFile(filename, "rw").getChannel

    val mappers = ArrayBuffer[FileVariantMapper]()

    mappers += new FileVariantMapper(channel, offset)(MapFilter.Private) {

      override val charset = props.charsets.mapper

      override def order = props.orders.mapper

    }

    for(i <- 0 to (channel.size / Int.MaxValue).toInt) {

      mappers += new FileVariantMapper(
        channel,
        offset + Int.MaxValue * i)(MapFilter.Private) {

        override val charset = props.charsets.mapper

        override def order = props.orders.mapper

      }
    }

    channel.close

    typeOf match {

      case Datatype.Bool => new BoolColumn(mappers, records)
      case Datatype.Byte => new ByteColumn(mappers, records)
      case Datatype.Char => new CharColumn(mappers, records)
      case Datatype.Double => new DoubleColumn(mappers, records)
      case Datatype.Float => new FloatColumn(mappers, records)
      case Datatype.Int => new IntColumn(mappers, records)
      case Datatype.Long => new LongColumn(mappers, records)
      case Datatype.Short => new ShortColumn(mappers, records)
      case Datatype.Month => new MonthColumn(mappers, records)
      case Datatype.Date => new DateColumn(mappers, records)
      case Datatype.Minute => new MinuteColumn(mappers, records)
      case Datatype.Second => new SecondColumn(mappers, records)
      case Datatype.Time => new TimeColumn(mappers, records)
      case Datatype.DateTime => new DateTimeColumn(mappers, records)
      case Datatype.Timestamp => new TimestampColumn(mappers, records)
      case Datatype.Symbol =>
        new SymbolColumn(
          mappers,
          records,
          new RandomAccessFile(filename + ".sym", "rw").getChannel)

      case Datatype.String =>
        new StringColumn(
          mappers,
          records,
          new RandomAccessFile(filename + ".str", "rw").getChannel)

    }
  }
}

