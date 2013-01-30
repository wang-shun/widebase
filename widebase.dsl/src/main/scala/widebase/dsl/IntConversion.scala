package widebase.dsl

import org.joda.time. { Minutes, Seconds }

/** A collection of implicit conversions to convert [[Scala.Int]] into specific type.
 *
 * @author myst3r10n
 */
class IntConversion(int: Int) {

  /** Origin type. */
  def apply() = int

  /** Converted [[scala.Byte]]. */
  def b = int.toByte

  /** Converted [[scala.Short]]. */
  def s = int.toShort

  /** Converted [[org.joda.time.Minutes]]. */
  def U = Minutes.minutes(int)

  /** Converted [[org.joda.time.Seconds]]. */
  def V = Seconds.seconds(int)

}

