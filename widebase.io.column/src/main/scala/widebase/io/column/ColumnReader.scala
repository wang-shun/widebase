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
 * @param filename only exception purpose
 *
 * @author myst3r10n
 */
class ColumnReader(reader: VariantReader)(implicit filename: String = "") {

  /** Closes reader. */
  def close {

    reader.close

  }

  /** Reads column from file.
    *
    * @return [[widebase.db.column.VariantColumn]]
   */
  def read: TypedColumn[_] = {

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
    val length = reader.readInt

    // Read column values
    reader.mode = typeOf

    typeOf match {

      case Datatype.Bool =>
        val column = new BoolColumn

        while(column.length < length)
          column += reader.readBool

        column

      case Datatype.Byte =>
        val column = new ByteColumn

        while(column.length < length)
          column += reader.read

        column

      case Datatype.Char =>
        val column = new CharColumn

        while(column.length < length)
          column += reader.readChar

        column

      case Datatype.Double =>
        val column = new DoubleColumn

        while(column.length < length)
          column += reader.readDouble

        column

      case Datatype.Float =>
        val column = new FloatColumn

        while(column.length < length)
          column += reader.readFloat

        column

      case Datatype.Int =>
        val column = new IntColumn

        while(column.length < length)
          column += reader.readInt

         column

      case Datatype.Long =>
        val column = new LongColumn

        while(column.length < length)
          column += reader.readLong

        column

      case Datatype.Short =>
        val column = new ShortColumn

        while(column.length < length)
          column += reader.readShort

        column

      case Datatype.Month =>
        val column = new MonthColumn

        while(column.length < length)
          column += reader.readMonth

        column

      case Datatype.Date =>
        val column = new DateColumn

        while(column.length < length)
          column += reader.readDate

        column

      case Datatype.Minute =>
        val column = new MinuteColumn

        while(column.length < length)
          column += reader.readMinute

        column

      case Datatype.Second =>
        val column = new SecondColumn

        while(column.length < length)
          column += reader.readSecond

        column

      case Datatype.Time =>
        val column = new TimeColumn

        while(column.length < length)
          column += reader.readTime

        column

      case Datatype.DateTime =>
        val column = new DateTimeColumn

        while(column.length < length)
          column += reader.readDateTime

        column

      case Datatype.Timestamp =>
        val column = new TimestampColumn

        while(column.length < length)
          column += reader.readTimestamp

        column

      case Datatype.Symbol =>
        val column = new SymbolColumn

        while(column.length < length)
          column += reader.readSymbol

        column

      case Datatype.String =>
        val column = new StringColumn

        while(column.length < length)
          column += reader.readString

        column

    }
  }
}

