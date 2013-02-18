package widebase.ui.chart

import java.sql.Timestamp

import org.jfree.data.time. { Day, Millisecond, Month }

import org.joda.time. { LocalDate, LocalDateTime, YearMonth }

/** Annotations package.
 *
 * @author myst3r10n
 */
package object annotations {

  /** Convert value into double.
   *
   * @param value to convert
   *
   * @return double
   */
  def asDouble(value: Any) =
    if(value.isInstanceOf[YearMonth]) {

      val periodValue = value.asInstanceOf[YearMonth]

      new Month(
        periodValue.getMonthOfYear,
        periodValue.getYear).getFirstMillisecond

    } else if(value.isInstanceOf[LocalDate]) {

      val periodValue = value.asInstanceOf[LocalDate]

      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear).getFirstMillisecond

    } else if(value.isInstanceOf[LocalDateTime]) {

      val periodValue = value.asInstanceOf[LocalDateTime]

      new Millisecond(
        periodValue.getMillisOfSecond,
        periodValue.getSecondOfMinute,
        periodValue.getMinuteOfHour,
        periodValue.getHourOfDay,
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear).getFirstMillisecond

    } else if(value.isInstanceOf[Timestamp])
      new Millisecond(value.asInstanceOf[Timestamp]).getFirstMillisecond
    else if(value.isInstanceOf[Int])
      value.asInstanceOf[Int]
    else
      value.asInstanceOf[Double]

}

