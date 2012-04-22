package widebase.io.column

import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter
import vario.io.VariantWriter

import widebase.db.column.VariantColumn
import widebase.io.filter.MagicId

/** Writes columns into channel.
 *
 * @param writer self-explanatory
 * @param companion self-explanatory
 *
 * @author myst3r10n
 */
class ColumnWriter(writer: VariantWriter, companion: VariantWriter = null) {

  /** Closes writer and companion. */
  def close {

    if(companion != null)
      companion.close

    writer.close

  }

  /** Writes column into file.
    *
    * @param column to write
   */
  def write(column: VariantColumn) {

    // Write magic
    if(writer.mode != Datatype.String)
      writer.mode = Datatype.String

    writer.write(MagicId.Column.toString)

    // Write column type
    writer.mode = Datatype.Byte
    writer.write(column.typeOf.id.toByte)

    // Write column length
    writer.mode = Datatype.Int
    writer.write(column.length)

    // Write column values
    if(companion == null)
      writer.mode = column.typeOf
    else
      companion.mode = column.typeOf

    column.typeOf match {

      case Datatype.Bool => column.bools.foreach(bool => writer.write(bool))
      case Datatype.Byte => column.bytes.foreach(byte => writer.write(byte))
      case Datatype.Char => column.chars.foreach(char => writer.write(char))
      case Datatype.Double => column.doubles.foreach(double => writer.write(double))
      case Datatype.Float => column.floats.foreach(float => writer.write(float))
      case Datatype.Int => column.ints.foreach(int => writer.write(int))
      case Datatype.Long => column.longs.foreach(long => writer.write(long))
      case Datatype.Short => column.shorts.foreach(short => writer.write(short))
      case Datatype.Month => column.months.foreach(value => writer.write(value))
      case Datatype.Date => column.dates.foreach(value => writer.write(value))
      case Datatype.Minute => column.minutes.foreach(value => writer.write(value))
      case Datatype.Second => column.seconds.foreach(value => writer.write(value))
      case Datatype.Time => column.times.foreach(value => writer.write(value))
      case Datatype.DateTime => column.dateTimes.foreach(value => writer.write(value))
      case Datatype.Timestamp => column.timestamps.foreach(value => writer.write(value))
      case Datatype.Symbol =>
        if(companion == null)
          column.symbols.foreach(value => writer.write(value, true))
        else {

          var lastEnded = 0

          writer.mode = Datatype.Int

          column.symbols.foreach { value =>

            lastEnded += value.toString.getBytes(companion.charset).size - 1

            writer.write(lastEnded)
            companion.write(value, false)

          }
        }

      case Datatype.String =>
        if(companion == null)
          column.strings.foreach(value => writer.write(value, true))
        else {
          var lastEnded = 0L

          writer.mode = Datatype.Long

          column.strings.foreach { value =>

            lastEnded += value.getBytes(companion.charset).size

            writer.write(lastEnded)
            companion.write(value, false)

          }
        }
    }
  }
}

