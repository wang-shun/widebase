package widebase.dsl

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

/** A collection of implicit conversions to convert a iterable [[widebase.db.column.TypedColumn]] into specific iterable column.
 *
 * @author myst3r10n
 */
class IterableConversion(iterable: Iterable[TypedColumn[_]]) {

  /** Untouched. */
  def apply() = iterable

  /** Converted iterable column. */
  def bi = iterable.asInstanceOf[Iterable[BoolColumn]]

  /** Converted iterable column. */
  def xi = iterable.asInstanceOf[Iterable[ByteColumn]]

  /** Converted iterable column. */
  def ci = iterable.asInstanceOf[Iterable[CharColumn]]

  /** Converted iterable column. */
  def di = iterable.asInstanceOf[Iterable[DoubleColumn]]

  /** Converted iterable column. */
  def fi = iterable.asInstanceOf[Iterable[FloatColumn]]

  /** Converted iterable column. */
  def ii = iterable.asInstanceOf[Iterable[IntColumn]]

  /** Converted iterable column. */
  def li = iterable.asInstanceOf[Iterable[LongColumn]]

  /** Converted iterable column. */
  def si = iterable.asInstanceOf[Iterable[ShortColumn]]

  /** Converted iterable column. */
  def Mi = iterable.asInstanceOf[Iterable[MonthColumn]]

  /** Converted iterable column. */
  def Di = iterable.asInstanceOf[Iterable[DateColumn]]

  /** Converted iterable column. */
  def Ui = iterable.asInstanceOf[Iterable[MinuteColumn]]

  /** Converted iterable column. */
  def Vi = iterable.asInstanceOf[Iterable[SecondColumn]]

  /** Converted iterable column. */
  def Ti = iterable.asInstanceOf[Iterable[TimeColumn]]

  /** Converted iterable column. */
  def Zi = iterable.asInstanceOf[Iterable[DateTimeColumn]]

  /** Converted iterable column. */
  def Pi = iterable.asInstanceOf[Iterable[TimestampColumn]]

  /** Converted iterable column. */
  def Yi = iterable.asInstanceOf[Iterable[SymbolColumn]]

  /** Converted iterable column. */
  def Si = iterable.asInstanceOf[Iterable[StringColumn]]

}

