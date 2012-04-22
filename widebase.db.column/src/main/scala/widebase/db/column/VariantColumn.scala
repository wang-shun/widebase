package widebase.db.column

import java.nio.channels.FileChannel
import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalTime,
  LocalDateTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable. { ArrayBuffer, WrappedArray }

import vario.data.Datatype
import vario.data.Datatype.Datatype
import vario.file.FileVariantMapper
import vario.filter.MapFilter

/** Implements a variant column.
 *
 * @param t type of column
 * @param m mapper of file
 * @param r records of column
 * @param c channel of companion
 *
 * @author myst3r10n
 */
class VariantColumn(t: Datatype)
  (implicit
    m: FileVariantMapper = null,
    r: Int = 0,
    c: FileChannel = null)
  extends TypedColumn(t)(m, r, c) {

  import vario.data

  def this()
    (implicit m: FileVariantMapper = null, r: Int = 0) =
    this(Datatype.None)(m, r)

  def this(t: Symbol)
    (implicit m: FileVariantMapper = null, r: Int = 0) =
    this(vario.data.by(t))(m, r)

  /** A [[widebase.db.column.TypedHybridBuffer]] for mixed types.
   *
   * @note Must be fixed-length.
   *
   * @author myst3r10n
   */
  protected abstract class HybridMixedBuffer[A]
    extends HybridFixedBuffer[A] {

    override def apply(idx: Int) =
      if(mapper == null || idx > records)
        buffer.apply(idx)
      else {

        val backupMode = mapper.mode
        mapper.mode = Datatype.Byte
        mapper.position = idx * sizeOf
        mapper.mode = backupMode

        read

      }

    override def last =
      if(mapper == null || records == 0)
        buffer.last
      else {

        val backupMode = mapper.mode
        mapper.mode = Datatype.Byte
        mapper.position = (records - 1) * sizeOf
        mapper.mode = backupMode

        read

      }

    override def update(idx: Int, value: A) {

      if(mapper != null && idx < records) {

        val backupMode = mapper.mode
        mapper.mode = Datatype.Byte
        mapper.position = idx * sizeOf
        mapper.mode = backupMode

        write(value)

      } else
        buffer(idx - records) = value

    }
  }

  /** A [[widebase.db.column.TypedHybridBuffer]] for variable-length types.
   *
   * @author myst3r10n
   */
  protected abstract class HybridVariableBuffer[A]
    extends HybridFixedBuffer[A] {

    /** Indexing logic.
     *
     * @param idx the index of value
     *
     * @return the element of its index
     */
    protected def get(idx: Int): A

    override def apply(idx: Int) =
      if(mapper == null || idx > records)
        buffer(idx)
      else
        get(idx)

    override def contains(elem: Any): Boolean = {

      if(mapper != null) {

        for(i <- 0 to records - 1)
          if(elem == get(i))
            return true

      }

      buffer.contains(elem)

    }

    override def foreach[U](f: A => U) = {

      if(mapper != null)
        for(i <- 0 to records - 1)
          f(get(i))

      buffer.foreach(f)

    }

    override def head =
      if(mapper == null || records == 0)
        buffer.head
      else
        get(0)

    override def indexOf[B >: A](elem: B): Int = {

      if(mapper != null)
        for(i <- 0 to records - 1)
          if(elem == get(i))
            return i

      buffer.indexOf(elem)

    }

    override def last =
      if(mapper == null || records == 0)
        buffer.last
      else
        get(records - 1)

    override def update(idx: Int, value: A) {

      if(mapper != null && idx < records)
        throw new UnsupportedOperationException(typeOf.toString)
      else
        buffer(idx - records) = value

    }
  }

  /** Column of [[org.joda.time.YearMonth]].
   *
   * @author myst3r10n
   */
  object months extends HybridMixedBuffer[YearMonth] {

    protected val sizeOf = data.sizeOf.month

    val typeOf = Datatype.Month

    protected def read = mapper.readMonth
    protected def write(value: YearMonth) {

      mapper.write(value)

    }
  }

  /** Column of [[org.joda.time.LocalDate]].
   *
   * @author myst3r10n
   */
  object dates extends HybridFixedBuffer[LocalDate] {

    protected val sizeOf = data.sizeOf.date

    val typeOf = Datatype.Date

    protected def read = mapper.readDate
    protected def write(value: LocalDate) {

      mapper.write(value)

    }
  }

  /** Column of [[org.joda.time.Minutes]].
   *
   * @author myst3r10n
   */
  object minutes extends HybridFixedBuffer[Minutes] {

    protected val sizeOf = data.sizeOf.minute

    val typeOf = Datatype.Minute

    protected def read = mapper.readMinute
    protected def write(value: Minutes) {

      mapper.write(value)

    }
  }

  /** Column of [[org.joda.time.Seconds]].
   *
   * @author myst3r10n
   */
  object seconds extends HybridFixedBuffer[Seconds] {

    protected val sizeOf = data.sizeOf.second

    val typeOf = Datatype.Second

    protected def read = mapper.readSecond
    protected def write(value: Seconds) {

      mapper.write(value)

    }
  }

  /** Column of [[org.joda.time.LocalTime]].
   *
   * @author myst3r10n
   */
  object times extends HybridFixedBuffer[LocalTime] {

    protected val sizeOf = data.sizeOf.time

    val typeOf = Datatype.Time

    protected def read = mapper.readTime
    protected def write(value: LocalTime) {

      mapper.write(value)

    }
  }

  /** Column of [[org.joda.time.LocalDateTime]].
   *
   * @author myst3r10n
   */
  object dateTimes extends HybridFixedBuffer[LocalDateTime] {

    protected val sizeOf = data.sizeOf.dateTime

    val typeOf = Datatype.DateTime

    protected def read = mapper.readDateTime
    protected def write(value: LocalDateTime) {

      mapper.write(value)

    }
  }

  /** Column of [[java.sql.Timestamp]].
   *
   * @author myst3r10n
   */
  object timestamps extends HybridMixedBuffer[Timestamp] {

    protected val sizeOf = data.sizeOf.timestamp

    val typeOf = Datatype.Timestamp

    protected def read = mapper.readTimestamp
    protected def write(value: Timestamp) {

      mapper.write(value)

    }
  }

  /** Column of [[scala.Symbol]].
   *
   * @author myst3r10n
   */
  object symbols extends HybridVariableBuffer[Symbol] {

    protected val sizeOf = data.sizeOf.int

    val typeOf= Datatype.Symbol

    protected var symbolMapper: FileVariantMapper = null

    if(
      mapper != null &&
      channel != null &&
      VariantColumn.this.typeOf == Datatype.Symbol) {

      if(mapper.mode != Datatype.Int)
        mapper.mode = Datatype.Int

      symbolMapper = new FileVariantMapper(channel)(MapFilter.Private) {

        override val charset = props.charsets.symbols

        override def order = props.orders.symbols

      }
      symbolMapper.close // Only file channel ;)

    }

    protected def get(idx: Int) = {

      symbolMapper.position =
        if(idx == 0)
          0
        else {

          mapper.position = (idx - 1) * data.sizeOf.int
          mapper.readInt

        }

      mapper.position = idx * data.sizeOf.int
      symbolMapper.readSymbol(mapper.readInt - symbolMapper.position)

    }

    protected def read = mapper.readSymbol
    protected def write(value: Symbol) {

      mapper.write(value)

    }
  }

  /** Column of [[java.lang.String]].
   *
   * @author myst3r10n
   */
  object strings extends HybridVariableBuffer[String] {

    protected val sizeOf = data.sizeOf.long

    val typeOf = Datatype.String

    if(
      mapper != null &&
      channel != null &&
      VariantColumn.this.typeOf == Datatype.String &&
      mapper.mode != Datatype.Long)
      mapper.mode = Datatype.Long

    protected def get(idx: Int) = {

      val position =
        if(idx == 0)
          0L
        else {

          mapper.position = (idx - 1) * data.sizeOf.long
          mapper.readLong

        }

      mapper.position = idx * data.sizeOf.long
      val size = mapper.readLong - position

      val stringMapper = new FileVariantMapper(
        VariantColumn.this.channel,
        position,
        size)(MapFilter.Private) {

        override val charset = props.charsets.strings

        override def order = props.orders.strings

      }

      stringMapper.readString(size.toInt)

    }

    protected def read = mapper.readString
    protected def write(value: String) {

      mapper.write(value)

    }
  }

  override def +=(value: Any) = {

    value match {

      case value: YearMonth => months += value
      case value: LocalDate => dates += value
      case value: Minutes => minutes += value
      case value: Seconds => seconds += value
      case value: LocalTime => times += value
      case value: LocalDateTime => dateTimes += value
      case value: Timestamp => timestamps += value
      case value: Symbol => symbols += value
      case value: String => strings += value
      case _ => super.+=(value)

    }

    this

  }

  override def -=(value: Any) = {

    value match {

      case value: YearMonth => months -= value
      case value: LocalDate => dates -= value
      case value: Minutes => minutes -= value
      case value: Seconds => seconds -= value
      case value: LocalTime => times -= value
      case value: LocalDateTime => dateTimes -= value
      case value: Timestamp => timestamps -= value
      case value: Symbol => symbols -= value
      case value: String => strings -= value
      case _ => super.-=(value)

    }

    this

  }

  def ++=(column: VariantColumn) = {

    column.typeOf match {

      case Datatype.Month => months ++= column.months
      case Datatype.Date => dates ++= column.dates
      case Datatype.Minute => minutes ++= column.minutes
      case Datatype.Second => seconds ++= column.seconds
      case Datatype.Time => times ++= column.times
      case Datatype.DateTime => dateTimes ++= column.dateTimes
      case Datatype.Timestamp => timestamps ++= column.timestamps
      case Datatype.Symbol => symbols ++= column.symbols
      case Datatype.String => strings ++= column.strings
      case _ => super.++=(column)

    }

    this

  }

  def --=(column: VariantColumn) = {

    column.typeOf match {

      case Datatype.Month => months --= column.months
      case Datatype.Date => dates --= column.dates
      case Datatype.Minute => minutes --= column.minutes
      case Datatype.Second => seconds --= column.seconds
      case Datatype.Time => times --= column.times
      case Datatype.DateTime => dateTimes --= column.dateTimes
      case Datatype.Timestamp => timestamps --= column.timestamps
      case Datatype.Symbol => symbols --= column.symbols
      case Datatype.String => strings --= column.strings
      case _ => super.--=(column)

    }

    this

  }

  override def apply(idx: Int) =
    typeOf match {

      case Datatype.Month => months(idx)
      case Datatype.Date => dates(idx)
      case Datatype.Minute => minutes(idx)
      case Datatype.Second => seconds(idx)
      case Datatype.Time => times(idx)
      case Datatype.DateTime => dateTimes(idx)
      case Datatype.Timestamp => timestamps(idx)
      case Datatype.Symbol => symbols(idx)
      case Datatype.String => strings(idx)
      case _ => super.apply(idx)

    }

  override def clear {

    typeOf match {

      case Datatype.Month => months.clear
      case Datatype.Date => dates.clear
      case Datatype.Minute => minutes.clear
      case Datatype.Second => seconds.clear
      case Datatype.Time => times.clear
      case Datatype.DateTime => dateTimes.clear
      case Datatype.Timestamp => timestamps.clear
      case Datatype.Symbol => symbols.clear
      case Datatype.String => strings.clear
      case _ => super.clear

    }
  }

  override def foreach[U](f: Any => U) =
    typeOf match {

      case Datatype.Month => months.foreach(f)
      case Datatype.Date => dates.foreach(f)
      case Datatype.Minute => minutes.foreach(f)
      case Datatype.Second => seconds.foreach(f)
      case Datatype.Time => times.foreach(f)
      case Datatype.DateTime => dateTimes.foreach(f)
      case Datatype.Timestamp => timestamps.foreach(f)
      case Datatype.Symbol => symbols.foreach(f)
      case Datatype.String => strings.foreach(f)
      case _ => super.foreach(f)

    }

  override def head =
    typeOf match {

      case Datatype.Month => months.head
      case Datatype.Date => dates.head
      case Datatype.Minute => minutes.head
      case Datatype.Second => seconds.head
      case Datatype.Time => times.head
      case Datatype.DateTime => dateTimes.head
      case Datatype.Timestamp => timestamps.head
      case Datatype.Symbol => symbols.head
      case Datatype.String => strings.head
      case _ => super.head

    }

  override def insert(n: Int, value: Any) {

    value match {

      case value: YearMonth => months.insert(n, value)
      case value: LocalDate => dates.insert(n, value)
      case value: Minutes => minutes.insert(n, value)
      case value: Seconds => seconds.insert(n, value)
      case value: LocalTime => times.insert(n, value)
      case value: LocalDateTime => dateTimes.insert(n, value)
      case value: Timestamp => timestamps.insert(n, value)
      case value: Symbol => symbols.insert(n, value)
      case value: String => strings.insert(n, value)
      case _ => super.insert(n, value)

    }
  }

  def insertAll(n: Int, column: VariantColumn) {

    column.typeOf match {

      case Datatype.Month => months.insertAll(n, column.months)
      case Datatype.Date => dates.insertAll(n, column.dates)
      case Datatype.Minute => minutes.insertAll(n, column.minutes)
      case Datatype.Second => seconds.insertAll(n, column.seconds)
      case Datatype.Time => times.insertAll(n, column.times)
      case Datatype.DateTime => dateTimes.insertAll(n, column.dateTimes)
      case Datatype.Timestamp => timestamps.insertAll(n, column.timestamps)
      case Datatype.Symbol => symbols.insertAll(n, column.symbols)
      case Datatype.String => strings.insertAll(n, column.strings)
      case _ => super.insertAll(n, column)

    }
  }

  override def last =
    typeOf match {

      case Datatype.Month => months.last
      case Datatype.Date => dates.last
      case Datatype.Minute => minutes.last
      case Datatype.Second => seconds.last
      case Datatype.Time => times.last
      case Datatype.DateTime => dateTimes.last
      case Datatype.Timestamp => timestamps.last
      case Datatype.Symbol => symbols.last
      case Datatype.String => strings.last
      case _ => super.last

    }

  override def length = 
    typeOf match {

      case Datatype.Month => months.length
      case Datatype.Date => dates.length
      case Datatype.Minute => minutes.length
      case Datatype.Second => seconds.length
      case Datatype.Time => times.length
      case Datatype.DateTime => dateTimes.length
      case Datatype.Timestamp => timestamps.length
      case Datatype.Symbol => symbols.length
      case Datatype.String => strings.length
      case _ => super.length

    }

  override def update(n: Int, replace: Any) {

    replace match {

      case replace: YearMonth => months(n) = replace
      case replace: LocalDate => dates(n) = replace
      case replace: Minutes => minutes(n) = replace
      case replace: Seconds => seconds(n) = replace
      case replace: LocalTime => times(n) = replace
      case replace: LocalDateTime => dateTimes(n) = replace
      case replace: Timestamp => timestamps(n) = replace
      case replace: Symbol => symbols(n) = replace
      case replace: String => strings(n) = replace
      case _ => super.update(n, replace)

    }
  }

  override protected def finalize {

    if(channel != null && typeOf == Datatype.String)
      channel.close

    super.finalize

  }
}

/** Companion of [[widebase.db.column.VariantColumn]].
 *
 * @author myst3r10n
 */
object VariantColumn {

  /** Creates [[widebase.db.column.VariantColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Any*) =
    new VariantColumn(Datatype.None) {

      typeOf = Datatype.withValue(values.head)

      values.head match {

        case value: Boolean => bools ++= values.asInstanceOf[WrappedArray[Boolean]]
        case value: Byte => bytes ++= values.asInstanceOf[WrappedArray[Byte]]
        case value: Char => chars ++= values.asInstanceOf[WrappedArray[Char]]
        case value: Double => doubles ++= values.asInstanceOf[WrappedArray[Double]]
        case value: Float => floats ++= values.asInstanceOf[WrappedArray[Float]]
        case value: Int => ints ++= values.asInstanceOf[WrappedArray[Int]]
        case value: Long => longs ++= values.asInstanceOf[WrappedArray[Long]]
        case value: Short => shorts ++= values.asInstanceOf[WrappedArray[Short]]
        case value: YearMonth => months ++= values.asInstanceOf[WrappedArray[YearMonth]]
        case value: LocalDate => dates ++= values.asInstanceOf[WrappedArray[LocalDate]]
        case value: Minutes => minutes ++= values.asInstanceOf[WrappedArray[Minutes]]
        case value: Seconds => seconds ++= values.asInstanceOf[WrappedArray[Seconds]]
        case value: LocalTime => times ++= values.asInstanceOf[WrappedArray[LocalTime]]
        case value: LocalDateTime => dateTimes ++= values.asInstanceOf[WrappedArray[LocalDateTime]]
        case value: Timestamp => timestamps ++= values.asInstanceOf[WrappedArray[Timestamp]]
        case value: Symbol => symbols ++= values.asInstanceOf[WrappedArray[Symbol]]
        case value: String => strings ++= values.asInstanceOf[WrappedArray[String]]

      }
    }
}

