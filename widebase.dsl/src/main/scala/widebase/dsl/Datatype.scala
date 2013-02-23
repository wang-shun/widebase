package widebase.dsl

import scala.collection.mutable.ArrayBuffer

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

/** A collection of convenience purposed types.
 *
 * @author myst3r10n
 */
object Datatype {

  /** Creates a [[widebase.db.column.BoolColumn]]. */
  def bool = BoolColumn

  /** Creates a [[widebase.db.column.ByteColumn]]. */
  def byte = ByteColumn

  /** Creates a [[widebase.db.column.CharColumn]]. */
  def char = CharColumn

  /** Creates a [[widebase.db.column.DoubleColumn]]. */
  def double = DoubleColumn

  /** Creates a [[widebase.db.column.FloatColumn]]. */
  def float = FloatColumn

  /** Creates a [[widebase.db.column.IntColumn]]. */
  def int = IntColumn

  /** Creates a [[widebase.db.column.LongColumn]]. */
  def long = LongColumn

  /** Creates a [[widebase.db.column.ShortColumn]]. */
  def short = ShortColumn

  /** Creates a [[widebase.db.column.MonthColumn]]. */
  def month = MonthColumn

  /** Creates a [[widebase.db.column.DateColumn]]. */
  def date = DateColumn

  /** Creates a [[widebase.db.column.MinuteColumn]]. */
  def minute = MinuteColumn

  /** Creates a [[widebase.db.column.SecondColumn]]. */
  def second = SecondColumn

  /** Creates a [[widebase.db.column.TimeColumn]]. */
  def time = TimeColumn

  /** Creates a [[widebase.db.column.DateTimeColumn]].
   *
   * @deprecated(Use `datetime` instead, 0.3.4)
   */
  def dateTime = DateTimeColumn

  /** Creates a [[widebase.db.column.DateTimeColumn]]. */
  def datetime = DateTimeColumn

  /** Creates a [[widebase.db.column.TimestampColumn]]. */
  def timestamp = TimestampColumn

  /** Creates a [[widebase.db.column.SymbolColumn]]. */
  def symbol = SymbolColumn

  /** Creates a [[widebase.db.column.StringColumn]]. */
  def string = StringColumn

  /** Creates a record. */
  def R = ArrayBuffer

}

