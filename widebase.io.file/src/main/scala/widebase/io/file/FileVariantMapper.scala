package widebase.io.file

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
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

import widebase.io. { CharsetLike, TypeMismatchException }
import widebase.io.filter.MapFilter
import widebase.io.filter.MapFilter.MapFilter

/** Map a [[java.nio.channels.FileChannel]] for variant types.
 *
 * @param channel @see [[java.lang.channels.FileChannel]]
 * @param offset within the file at which the mapped region is to start; must be non-negative
 * @param size of the region to be mapped; must be non-negative and no greater than [[java.lang.Integer.MAX_VALUE]]
 * @param filter of mapper, default is [[widebase.io.filter.MapFilter.ReadOnly]]
 *
 * @author myst3r10n
 */
class FileVariantMapper(
  channel: FileChannel,
  offset: Long = 0L,
  size: Long = -1L)
  (implicit filter: MapFilter = MapFilter.ReadOnly)
  extends FileTypedMapper(channel, offset, size)(filter)
  with CharsetLike {

  import widebase.data
  import widebase.data.Datatype
  import widebase.data.Datatype.Datatype

  override def position: Int = {

    mode match {

      case Datatype.Month => buffer.position
      case mode if
        mode == Datatype.Date ||
        mode == Datatype.Minute ||
        mode == Datatype.Second ||
        mode == Datatype.Time => buffer.position +
          (intBuffer.position * data.sizeOf.int)

      case mode if
        mode == Datatype.DateTime ||
        mode == Datatype.Timestamp => buffer.position +
          (longBuffer.position * data.sizeOf.long)

      case mode if
        mode == Datatype.Symbol ||
        mode == Datatype.String => buffer.position

      case _ => super.position

    }
  }

  override def position_=(replace: Int) {

    mode match {

      case Datatype.Month => buffer.position(replace)
      case mode if
        mode == Datatype.Date ||
        mode == Datatype.Minute ||
        mode == Datatype.Second ||
        mode == Datatype.Time =>

        buffer.position(replace)
        intBuffer = buffer.asIntBuffer

      case mode if
        mode == Datatype.DateTime ||
        mode == Datatype.Timestamp =>

        buffer.position(replace)
        longBuffer = buffer.asLongBuffer

      case mode if
        mode == Datatype.Symbol ||
        mode == Datatype.String => buffer.position(replace)

      case _ => super.position = replace

    }
  }

  /** Read [[org.joda.time.YearMonth]] from buffer. */
  def readMonth: YearMonth = {

    val backupMode = mode
    mode = Datatype.Int
    val year = readInt
    mode = backupMode

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

  /** Read [[org.joda.time.LocalDateTime]] from buffer. */
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

    val backupMode = mode
    mode = Datatype.Int
    val nanos = readInt
    mode = backupMode

    val timestamp = new Timestamp(dateTime)
    timestamp.setNanos(nanos)
    timestamp

  }

  /** Read null terminated [[java.lang.Symbol]] from buffer. */
  def readSymbol: Symbol = Symbol(readString)

  /** Read fixed-length [[java.lang.Symbol]] from buffer.
   *
   * @param length of bytes to be read
  */
  def readSymbol(length: Int): Symbol = Symbol(readString(length))

  /** Read null terminated [[java.lang.String]] from buffer. */
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

  /** Write [[org.joda.time.YearMonth]] into buffer
   *
   * @param value to write
  */
  def write(value: YearMonth) {

    val backupMode = mode
    mode = Datatype.Int
    write(value.getYear)
    mode = backupMode

    write(value.getMonthOfYear.toByte)

  }

  /** Write [[org.joda.time.LocalDate]] into buffer
   *
   * @param value to write
  */
  def write(value: LocalDate) {

    write((value.toDateMidnight(value.getChronology.getZone).getMillis / 1000).toInt)

  }

  /** Write array of [[scala.LocalDate]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[LocalDate]) {
  
    write(for(value <- values)
      yield((value.toDateMidnight(value.getChronology.getZone).getMillis / 1000).toInt))

  }

  /** Write [[org.joda.time.Minutes]] into buffer
   *
   * @param value to write
  */
  def write(value: Minutes) {

    write(value.getMinutes)

  }

  /** Write array of [[scala.Minutes]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Minutes]) {
  
    write(for(value <- values)
      yield(value.getMinutes))

  }

  /** Write [[org.joda.time.Seconds]] into buffer
   *
   * @param value to write
  */
  def write(value: Seconds) {

    write(value.getSeconds)

  }

  /** Write array of [[scala.Seconds]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Seconds]) {
  
    write(for(value <- values)
      yield(value.getSeconds))

  }

  /** Write [[org.joda.time.LocalTime]] into buffer
   *
   * @param value to write
  */
  def write(value: LocalTime) {

    write(value.getMillisOfDay)

  }

  /** Write array of [[scala.LocalTime]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[LocalTime]) {
  
    write(for(value <- values)
      yield(value.getMillisOfDay))

  }

  /** Write [[org.joda.time.LocalDateTime]] into buffer
   *
   * @param value to write
  */
  def write(value: LocalDateTime) {

    write(value.toDateTime.getMillis)

  }

  /** Write array of [[scala.LocalDateTime]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[LocalDateTime]) {
  
    write(for(value <- values)
      yield(value.toDateTime.getMillis))

  }

  /** Write [[java.sql.Timestamp]] into buffer
   *
   * @param value to write
  */
  def write(value: Timestamp) {

    write(value.getTime)

    val backupMode = mode
    mode = Datatype.Int
    write(value.getNanos)
    mode = backupMode

  }

  /** Write [[java.lang.Symbol]] into buffer
   *
   * @param value to write
   *
   * @return amount of bytes written
  */
  def write(value: Symbol): Int = write(value, false)

  /** Write [[java.lang.Symbol]] into buffer
   *
   * @param value to write
   * @param terminated write null terminated [[java.lang.Symbol]]
   *
   * @return amount of bytes written
  */
  def write(value: Symbol, terminated: Boolean): Int =
    write(value.toString.drop(1), terminated)

  /** Write [[java.lang.String]] into buffer
   *
   * @param value to write
   *
   * @return amount of bytes written
  */
  def write(value: String): Int = write(value, false)

  /** Write [[java.lang.String]] into buffer
   *
   * @param value to write
   * @param terminated write null terminated [[java.lang.String]]
   *
   * @return amount of bytes written
  */
  def write(value: String, terminated: Boolean) = {

    var bytes = value.getBytes(charset)

    if(terminated)
      bytes = bytes :+ (0: Byte)

    super.write(bytes)

    bytes.size

  }

  override protected def reposition {

    mode match {

      case mode if
        mode == Datatype.Month ||
        mode == Datatype.Symbol ||
        mode == Datatype.String =>

      case mode if
        mode == Datatype.Date ||
        mode == Datatype.Minute ||
        mode == Datatype.Second ||
        mode == Datatype.Time => buffer.position(buffer.position +
          (intBuffer.position * data.sizeOf.int))

      case mode if
        mode == Datatype.DateTime ||
        mode == Datatype.Timestamp => buffer.position(buffer.position +
          (longBuffer.position * data.sizeOf.long))

      case _ => super.reposition

    }
  }

  override protected def review(replace: Datatype) {

    replace match {

      case replace if
        replace == Datatype.Month ||
        replace == Datatype.Symbol ||
        replace == Datatype.String =>

      case replace if
        replace == Datatype.Date ||
        replace == Datatype.Minute ||
        replace == Datatype.Second ||
        replace == Datatype.Time => intBuffer = buffer.asIntBuffer

      case replace if
        replace == Datatype.DateTime ||
        replace == Datatype.Timestamp => longBuffer = buffer.asLongBuffer

      case _ => super.review(replace)

    }
  }
}

