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
    val records = reader.readInt

    // Read column values
    val column = new VariantColumn(typeOf)

    reader.mode = typeOf

    typeOf match {

      case Datatype.None =>

      case Datatype.Bool =>
        while(column.bools.length < records)
          column.bools += reader.readBool

      case Datatype.Byte =>
        while(column.bytes.length < records)
          column.bytes += reader.read

      case Datatype.Char =>
        while(column.chars.length < records)
          column.chars += reader.readChar

      case Datatype.Double =>
        while(column.doubles.length < records)
          column.doubles += reader.readDouble

      case Datatype.Float =>
        while(column.floats.length < records)
          column.floats += reader.readFloat

      case Datatype.Int =>
        while(column.ints.length < records)
          column.ints += reader.readInt

      case Datatype.Long =>
        while(column.longs.length < records)
          column.longs += reader.readLong

      case Datatype.Short =>
        while(column.shorts.length < records)
          column.shorts += reader.readShort

      case Datatype.Month =>
        while(column.months.length < records)
          column.months += reader.readMonth

      case Datatype.Date =>
        while(column.dates.length < records)
          column.dates += reader.readDate

      case Datatype.Minute =>
        while(column.minutes.length < records)
          column.minutes += reader.readMinute

      case Datatype.Second =>
        while(column.seconds.length < records)
          column.seconds += reader.readSecond

      case Datatype.Time =>
        while(column.times.length < records)
          column.times += reader.readTime

      case Datatype.DateTime =>
        while(column.dateTimes.length < records)
          column.dateTimes += reader.readDateTime

      case Datatype.Timestamp =>
        while(column.timestamps.length < records)
          column.timestamps += reader.readTimestamp

      case Datatype.Symbol =>
        while(column.symbols.length < records)
          column.symbols += reader.readSymbol

      case Datatype.String =>
        while(column.strings.length < records)
          column.strings += reader.readString

    }

    column

  }
}

