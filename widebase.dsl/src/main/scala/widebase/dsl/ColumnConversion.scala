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

/** A collection of implicit conversions to convert [[widebase.db.column.TypedColumn]] into specific column.
 *
 * @author myst3r10n
 */
class ColumnConversion(column: TypedColumn[_]) {

  /** Untouched. */
  def apply() = column

  /** Converted column. */
  def b = column.asInstanceOf[BoolColumn]

  /** Converted column. */
  def x = column.asInstanceOf[ByteColumn]

  /** Converted column. */
  def c = column.asInstanceOf[CharColumn]

  /** Converted column. */
  def d = column.asInstanceOf[DoubleColumn]

  /** Converted column. */
  def f = column.asInstanceOf[FloatColumn]

  /** Converted column. */
  def i = column.asInstanceOf[IntColumn]

  /** Converted column. */
  def l = column.asInstanceOf[LongColumn]

  /** Converted column. */
  def s = column.asInstanceOf[ShortColumn]

  /** Converted column. */
  def M = column.asInstanceOf[MonthColumn]

  /** Converted column. */
  def D = column.asInstanceOf[DateColumn]

  /** Converted column. */
  def U = column.asInstanceOf[MinuteColumn]

  /** Converted column. */
  def V = column.asInstanceOf[SecondColumn]

  /** Converted column. */
  def T = column.asInstanceOf[TimeColumn]

  /** Converted column. */
  def Z = column.asInstanceOf[DateTimeColumn]

  /** Converted column. */
  def P = column.asInstanceOf[TimestampColumn]

  /** Converted column. */
  def Y = column.asInstanceOf[SymbolColumn]

  /** Converted column. */
  def S = column.asInstanceOf[StringColumn]

}

