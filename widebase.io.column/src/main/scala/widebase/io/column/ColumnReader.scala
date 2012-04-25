package widebase.io.column

import vario.data.Datatype

import java.io.RandomAccessFile
import java.nio.channels.FileChannel

import scala.util.control.Breaks. { break, breakable }

import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter
import vario.io.VariantReader

import widebase.db.column.VariantColumn

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
  def read: VariantColumn = {

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
    val column = new VariantColumn(typeOf)

    reader.mode = typeOf

    typeOf match {

      case Datatype.None =>

      case Datatype.Bool =>
        while(column.bools.length < length)
          column.bools += reader.readBool

      case Datatype.Byte =>
        while(column.bytes.length < length)
          column.bytes += reader.read

      case Datatype.Char =>
        while(column.chars.length < length)
          column.chars += reader.readChar

      case Datatype.Double =>
        while(column.doubles.length < length)
          column.doubles += reader.readDouble

      case Datatype.Float =>
        while(column.floats.length < length)
          column.floats += reader.readFloat

      case Datatype.Int =>
        while(column.ints.length < length)
          column.ints += reader.readInt

      case Datatype.Long =>
        while(column.longs.length < length)
          column.longs += reader.readLong

      case Datatype.Short =>
        while(column.shorts.length < length)
          column.shorts += reader.readShort

      case Datatype.Month =>
        while(column.months.length < length)
          column.months += reader.readMonth

      case Datatype.Date =>
        while(column.dates.length < length)
          column.dates += reader.readDate

      case Datatype.Minute =>
        while(column.minutes.length < length)
          column.minutes += reader.readMinute

      case Datatype.Second =>
        while(column.seconds.length < length)
          column.seconds += reader.readSecond

      case Datatype.Time =>
        while(column.times.length < length)
          column.times += reader.readTime

      case Datatype.DateTime =>
        while(column.dateTimes.length < length)
          column.dateTimes += reader.readDateTime

      case Datatype.Timestamp =>
        while(column.timestamps.length < length)
          column.timestamps += reader.readTimestamp

      case Datatype.Symbol =>
        while(column.symbols.length < length)
          column.symbols += reader.readSymbol

      case Datatype.String =>
        while(column.strings.length < length)
          column.strings += reader.readString

    }

    column

  }
}

