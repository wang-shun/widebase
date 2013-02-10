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

/** A collection of implicit conversions to convert a array of [[widebase.db.column.TypedColumn]] into specific array column.
 *
 * @author myst3r10n
 */
class ArrayConversion(array: Array[TypedColumn[_]]) {

  /** Untouched. */
  def apply() = array

  /** Converted array column. */
  def ba = array.asInstanceOf[Array[BoolColumn]]

  /** Converted array column. */
  def xa = array.asInstanceOf[Array[ByteColumn]]

  /** Converted array column. */
  def ca = array.asInstanceOf[Array[CharColumn]]

  /** Converted array column. */
  def da = array.asInstanceOf[Array[DoubleColumn]]

  /** Converted array column. */
  def fa = array.asInstanceOf[Array[FloatColumn]]

  /** Converted array column. */
  def ia = array.asInstanceOf[Array[IntColumn]]

  /** Converted array column. */
  def la = array.asInstanceOf[Array[LongColumn]]

  /** Converted array column. */
  def sa = array.asInstanceOf[Array[ShortColumn]]

  /** Converted array column. */
  def Ma = array.asInstanceOf[Array[MonthColumn]]

  /** Converted array column. */
  def Da = array.asInstanceOf[Array[DateColumn]]

  /** Converted array column. */
  def Ua = array.asInstanceOf[Array[MinuteColumn]]

  /** Converted array column. */
  def Va = array.asInstanceOf[Array[SecondColumn]]

  /** Converted array column. */
  def Ta = array.asInstanceOf[Array[TimeColumn]]

  /** Converted array column. */
  def Za = array.asInstanceOf[Array[DateTimeColumn]]

  /** Converted array column. */
  def Pa = array.asInstanceOf[Array[TimestampColumn]]

  /** Converted array column. */
  def Ya = array.asInstanceOf[Array[SymbolColumn]]

  /** Converted array column. */
  def Sa = array.asInstanceOf[Array[StringColumn]]

}

