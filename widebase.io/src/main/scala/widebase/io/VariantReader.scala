package widebase.io

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.sql.Timestamp

import org.joda.time. {

  DateTimeZone,
  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks. { break, breakable }

import widebase.data.Datatype
import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Read variant types from [[java.nio.channels.ReadableByteChannel]].
 *
 * @param channel @see [[java.lang.channels.ReadableByteChannel]]
 * @param filter of reader, default is [[widebase.io.filter.StreamFilter.None]]
 *
 * @author myst3r10n
 */
class VariantReader(
  channel: ReadableByteChannel,
  filter: StreamFilter = StreamFilter.None)
  extends TypedReader(channel, filter)
  with CharsetLike
  with ToggleVariantMode {

  /** Read [[org.joda.time.YearMonth]] from buffer. */
  def readMonth: YearMonth = {

    val originMode = mode
    mode = Datatype.Int
    val year = readInt
    mode = originMode

    val monthOfYear = read

    new YearMonth(year, monthOfYear)

  }

  /** Read [[org.joda.time.LocalDate]] from buffer. */
  def readDate: LocalDate = new LocalDate(readInt.toLong * 1000)

  /** Read array of [[org.joda.time.LocalDate]]s from buffer.
   *
   * @param length of [[org.joda.time.LocalDate]] array to be read
  */
  def readDate(length: Int): Array[LocalDate] =
    for(value <- readInt(length))
      yield(new LocalDate(value.toLong * 1000))

  /** Read [[org.joda.time.Minutes]] from buffer. */
  def readMinute: Minutes = Minutes.minutes(readInt)

  /** Read array of [[org.joda.time.Minutes]]s from buffer.
   *
   * @param length of [[org.joda.time.Minutes]] array to be read
  */
  def readMinute(length: Int): Array[Minutes] =
    for(value <- readInt(length))
      yield(Minutes.minutes(value))

  /** Read [[org.joda.time.Seconds]] from buffer. */
  def readSecond: Seconds = Seconds.seconds(readInt)

  /** Read array of [[org.joda.time.Seconds]]s from buffer.
   *
   * @param length of [[org.joda.time.Seconds]] array to be read
  */
  def readSecond(length: Int): Array[Seconds] =
    for(value <- readInt(length))
      yield(Seconds.seconds(value))

  /** Read [[org.joda.time.LocalDate]] from buffer. */
  def readTime: LocalTime = new LocalTime(readInt, DateTimeZone.UTC)

  /** Read array of [[org.joda.time.LocalDate]]s from buffer.
   *
   * @param length of [[org.joda.time.LocalDate]] array to be read
  */
  def readTime(length: Int): Array[LocalTime] =
    for(value <- readInt(length))
      yield(new LocalTime(value, DateTimeZone.UTC))

  /** Read [[org.joda.time.LocalDateTime]] from buffer
   *
   * @return Some([[org.joda.time.LocalDateTime]]) or [[scala.None]]
  */
  def readDateTime : LocalDateTime = new LocalDateTime(readLong)

  /** Read array of [[org.joda.time.LocalDateTime]]s from buffer.
   *
   * @param length of [[org.joda.time.LocalDateTime]] array to be read
  */
  def readDateTime(length: Int): Array[LocalDateTime] =
    for(value <- readLong(length))
      yield(new LocalDateTime(value))

  /** Read [[org.joda.time.Timestamp]] from buffer. */
  def readTimestamp : Timestamp = {

    val dateTime = readLong

    val originMode = mode
    mode = Datatype.Int
    val nanos = readInt
    mode = originMode

    val timestamp = new Timestamp(dateTime)
    timestamp.setNanos(nanos)
    timestamp

  }

  /** Read null terminated [[java.lang.Symbol]] from buffer. */
  def readSymbol: Symbol = Symbol(readString)

  /** Read fixed-length [[java.lang.Symbol]] from buffer. */
  def readSymbol(length: Int): Symbol = Symbol(readString(length))

  /** Read null termianted [[java.lang.String]] from buffer. */
  def readString: String = {

    var bytes = ArrayBuffer[Byte]()

    var value = read

    breakable {

      while(true) {

        if(value == 0)
          break

        bytes += value

        value = read

      }
    }

    charset.decode(ByteBuffer.wrap(bytes.toArray).order(buffer.order)).toString

  }

  /** Read fixed-length [[java.lang.String]] from buffer.
   *
   * @param length of bytes to be read
   */
  def readString(length: Int): String =
    charset.decode(ByteBuffer.wrap(read(length)).order(buffer.order)).toString

}

