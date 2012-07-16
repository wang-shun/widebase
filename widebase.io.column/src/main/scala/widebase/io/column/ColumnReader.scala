package widebase.io.column

import vario.data.Datatype

import java.io.RandomAccessFile
import java.nio.channels.FileChannel

import scala.util.control.Breaks. { break, breakable }

import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter
import vario.io.VariantReader

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

import widebase.io.filter. {

  InvalidMagicException,
  MagicId,
  MagicNotFoundException,
  WrongMagicException

}

/** Reads columns from channel.
 *
 * @param reader self-explanatory
 * @param companion self-explanatory
 * @param filename only exception purpose
 *
 * @author myst3r10n
 */
class ColumnReader(
  reader: VariantReader,
  companion: VariantReader = null)
  (implicit filename: String = "") {

  /** Closes writer and companion. */
  def close {

    if(companion != null)
      companion.close

    reader.close

  }

  /** Reads column from file.
    *
    * @param amount values to read, 0 read all
    *
    * @return [[widebase.db.column.VariantColumn]]
   */
  def read(amount: Int = 0): TypedColumn[_] = {

    // Read magic
    if(reader.mode != Datatype.String)
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
    reader.mode = Datatype.Byte
    val typeOf = Datatype(reader.read)

    // Read column length
    reader.mode = Datatype.Int
    var length = reader.readInt // Must read!

    if(amount > 0)
      length = amount

    // Read column values
    if(companion == null)
      reader.mode = typeOf
    else
      reader.mode = typeOf

    typeOf match {

      case Datatype.Bool => new BoolColumn ++= reader.readBool(length)
      case Datatype.Byte => new ByteColumn ++= reader.read(length)
      case Datatype.Char => new CharColumn ++= reader.readChar(length)
      case Datatype.Double => new DoubleColumn ++= reader.readDouble(length)
      case Datatype.Float => new FloatColumn ++= reader.readFloat(length)
      case Datatype.Int => new IntColumn ++= reader.readInt(length)
      case Datatype.Long => new LongColumn ++= reader.readLong(length)
      case Datatype.Short => new ShortColumn ++= reader.readShort(length)

      case Datatype.Month =>
        val column = new MonthColumn

        while(column.length < length)
          column += reader.readMonth

        column

      case Datatype.Date => new DateColumn ++= reader.readDate(length)
      case Datatype.Minute => new MinuteColumn ++= reader.readMinute(length)
      case Datatype.Second => new SecondColumn ++= reader.readSecond(length)
      case Datatype.Time => new TimeColumn ++= reader.readTime(length)
      case Datatype.DateTime =>new DateTimeColumn ++= reader.readDateTime(length)

      case Datatype.Timestamp =>
        val column = new TimestampColumn

        while(column.length < length)
          column += reader.readTimestamp

        column

      case Datatype.Symbol =>
        val column = new SymbolColumn

        if(companion == null)
          while(column.length < length)
            column += reader.readSymbol
        else {

          var lastEnded = 0L

          reader.mode = Datatype.Long

          while(column.length < length) {

            val currentEnded = reader.readLong

            column += companion.readSymbol((currentEnded - lastEnded).toInt)

            lastEnded = currentEnded

          }
        }

        column

      case Datatype.String =>
        val column = new StringColumn

        if(companion == null)
          while(column.length < length)
            column += reader.readString
        else {

          var lastEnded = 0L

          reader.mode = Datatype.Long

          while(column.length < length) {

            val currentEnded = reader.readLong

            column += companion.readString((currentEnded - lastEnded).toInt)

            lastEnded = currentEnded

          }
        }

        column
    }
  }
}

