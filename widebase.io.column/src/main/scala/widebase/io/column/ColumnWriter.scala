package widebase.io.column

import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter
import vario.io.VariantWriter

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
  def write[A](column: TypedColumn[A]) {

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

    column match {

      case column: BoolColumn => column.foreach(value => writer.write(value))
      case column: ByteColumn => column.foreach(value => writer.write(value))
      case column: CharColumn => column.foreach(value => writer.write(value))
      case column: DoubleColumn => column.foreach(value => writer.write(value))
      case column: FloatColumn => column.foreach(value => writer.write(value))
      case column: IntColumn => column.foreach(value => writer.write(value))
      case column: LongColumn => column.foreach(value => writer.write(value))
      case column: ShortColumn => column.foreach(value => writer.write(value))
      case column: MonthColumn => column.foreach(value => writer.write(value))
      case column: DateColumn => column.foreach(value => writer.write(value))
      case column: MinuteColumn => column.foreach(value => writer.write(value))
      case column: SecondColumn => column.foreach(value => writer.write(value))
      case column: TimeColumn => column.foreach(value => writer.write(value))
      case column: DateTimeColumn => column.foreach(value => writer.write(value))
      case column: TimestampColumn => column.foreach(value => writer.write(value))
      case column: SymbolColumn =>
        if(companion == null)
          column.foreach(value => writer.write(value, true))
        else {

          var lastEnded = 0L

          writer.mode = Datatype.Long

          column.foreach { value =>

            lastEnded += value.toString.getBytes(companion.charset).size - 1

            writer.write(lastEnded)
            companion.write(value, false)

          }
        }

      case column: StringColumn =>
        if(companion == null)
          column.foreach(value => writer.write(value, true))
        else {
          var lastEnded = 0L

          writer.mode = Datatype.Long

          column.foreach { value =>

            lastEnded += value.getBytes(companion.charset).size

            writer.write(lastEnded)
            companion.write(value, false)

          }
        }
    }
  }
}

