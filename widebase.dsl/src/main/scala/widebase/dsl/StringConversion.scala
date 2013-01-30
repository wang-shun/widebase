package widebase.dsl

import java.sql.Timestamp

import org.joda.time. { LocalDate, LocalDateTime, LocalTime, YearMonth }

import org.joda.time.format.DateTimeFormat

/** A collection of implicit conversions to convert [[java.lang.String]] into specific type.
 *
 * @author myst3r10n
 */
class StringConversion(text: String) {

  /** Format pattern for date based types. */
  protected val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  /** Format pattern for date and time based types. */
  protected val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

  /** Format pattern for month based types. */
  protected val monthFormatter = DateTimeFormat.forPattern("yyyy-MM")

  /** Origin type. */
  def apply() = text

  /** Converted [[org.joda.time.YearMonth]]. */
  def M = YearMonth.parse(text, monthFormatter)

  /** Converted [[org.joda.time.LocalDate]]. */
  def D = LocalDate.parse(text, dateFormatter)

  /** Converted [[org.joda.time.LocalTime]]. */
  def T = new LocalTime(text)

  /** Converted [[org.joda.time.LocalDateTime]]. */
  def Z = LocalDateTime.parse(text, dateTimeFormatter)

  /** Converted [[java.sql.Timestamp]]. */
  def P = Timestamp.valueOf("1970-01-01 " + text)

}

