package widebase.data

import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalTime,
  LocalDateTime,
  Minutes,
  Seconds,
  YearMonth

}

/** Supported types.
 *
 * @author myst3r10n
 */
object Datatype extends Enumeration {

  type Datatype = Value

  val
    None,
    Bool,
    Byte,
    Char,
    Double,
    Float,
    Int,
    Long,
    Short,
    Month,
    Date,
    Minute,
    Second,
    Time,
    DateTime,
    Timestamp,
    Symbol,
    String = Value

  /** Convert a typed value.
   *
   * @param value typed
   *
   * @return resolved [[widebase.data.Datatype]]
  */
  def withValue(value: Any): Datatype =
    value match {

      case value: Boolean => Datatype.Bool
      case value: Byte => Datatype.Byte
      case value: Char => Datatype.Char
      case value: Double => Datatype.Double
      case value: Float => Datatype.Float
      case value: Int => Datatype.Int
      case value: Long => Datatype.Long
      case value: Short => Datatype.Short

      case value: YearMonth => Datatype.Month
      case value: LocalDate => Datatype.Date
      case value: Minutes => Datatype.Minute
      case value: Seconds => Datatype.Second
      case value: LocalTime => Datatype.Time
      case value: LocalDateTime => Datatype.DateTime
      case value: Timestamp => Datatype.Timestamp

      case value: Symbol => Datatype.Symbol
      case value: String => Datatype.String

      case value: Any => Datatype.None

    }
}

