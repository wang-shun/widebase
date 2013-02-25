package widebase.dsl

import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

/** A collection of implicit conversions to convert [[scala.Any]] into specific datatype.
 *
 * @author myst3r10n
 */
class AnyConversion(value: Any) {

  /** Untouched. */
  def apply() = value

  /** Converted value. */
  def b = value.asInstanceOf[Boolean]

  /** Converted value. */
  def x = value.asInstanceOf[Byte]

  /** Converted value. */
  def c = value.asInstanceOf[Char]

  /** Converted value. */
  def d = value.asInstanceOf[Double]

  /** Converted value. */
  def f = value.asInstanceOf[Float]

  /** Converted value. */
  def i = value.asInstanceOf[Int]

  /** Converted value. */
  def l = value.asInstanceOf[Long]

  /** Converted value. */
  def s = value.asInstanceOf[Short]

  /** Converted value. */
  def M = value.asInstanceOf[YearMonth]

  /** Converted value. */
  def D = value.asInstanceOf[LocalDate]

  /** Converted value. */
  def U = value.asInstanceOf[Minutes]

  /** Converted value. */
  def V = value.asInstanceOf[Seconds]

  /** Converted value. */
  def T = value.asInstanceOf[LocalTime]

  /** Converted value. */
  def Z = value.asInstanceOf[LocalDateTime]

  /** Converted value. */
  def P = value.asInstanceOf[Timestamp]

  /** Converted value. */
  def Y = value.asInstanceOf[Symbol]

  /** Converted value. */
  def S = value.asInstanceOf[String]

}

