package widebase.io.column

import widebase.data.Datatype

import java.io.RandomAccessFile
import java.nio.channels.FileChannel

import scala.util.control.Breaks. { break, breakable }

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

import widebase.io.VariantReader

import widebase.io.filter. {

  InvalidMagicException,
  MagicId,
  MagicNotFoundException,
  StreamFilter,
  WrongMagicException

}

import widebase.io.filter.StreamFilter.StreamFilter

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

      case Datatype.Bool =>
        val column = new BoolColumn

        if(column.length < length)
          column ++= reader.readBool(length)

        column

      case Datatype.Byte =>
        val column = new ByteColumn

        if(column.length < length)
          column ++= reader.read(length)

        column

      case Datatype.Char =>
        val column = new CharColumn

        if(column.length < length)
          column ++= reader.readChar(length)

        column

      case Datatype.Double =>
        val column = new DoubleColumn

        if(column.length < length)
          column ++= reader.readDouble(length)

        column

      case Datatype.Float =>
        val column = new FloatColumn

        if(column.length < length)
          column ++= reader.readFloat(length)

        column

      case Datatype.Int =>
        val column = new IntColumn

        if(column.length < length)
          column ++= reader.readInt(length)

        column

      case Datatype.Long =>
        val column = new LongColumn

        if(column.length < length)
          column ++= reader.readLong(length)

        column

      case Datatype.Short =>
        val column = new ShortColumn

        if(column.length < length)
          column ++= reader.readShort(length)

        column

      case Datatype.Month =>
        val column = new MonthColumn

        while(column.length < length)
          column += reader.readMonth

        column

      case Datatype.Date =>
        val column = new DateColumn

        if(column.length < length)
          column ++= reader.readDate(length)

        column

      case Datatype.Minute =>
        val column = new MinuteColumn

        if(column.length < length)
          column ++= reader.readMinute(length)

        column

      case Datatype.Second =>
        val column = new SecondColumn

        if(column.length < length)
          column ++= reader.readSecond(length)

        column

      case Datatype.Time =>
        val column = new TimeColumn

        if(column.length < length)
          column ++= reader.readTime(length)

        column

      case Datatype.DateTime =>
        val column = new DateTimeColumn

        if(column.length < length)
          column ++= reader.readDateTime(length)

        column

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

