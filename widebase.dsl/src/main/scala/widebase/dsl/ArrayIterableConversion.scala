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

/** A collection of implicit conversions to convert a iterable [[widebase.db.column.TypedColumn]] into specific array column.
 *
 * @author myst3r10n
 */
class ArrayIterableConversion(iterable: Iterable[TypedColumn[_]]) {

  /** Untouched. */
  def apply() = iterable

  /** Converted array column. */
  def bia = iterable.asInstanceOf[Iterable[BoolColumn]].toArray

  /** Converted array column. */
  def xia = iterable.asInstanceOf[Iterable[ByteColumn]].toArray

  /** Converted array column. */
  def cia = iterable.asInstanceOf[Iterable[CharColumn]].toArray

  /** Converted array column. */
  def dia = iterable.asInstanceOf[Iterable[DoubleColumn]].toArray

  /** Converted array column. */
  def fia = iterable.asInstanceOf[Iterable[FloatColumn]].toArray

  /** Converted array column. */
  def iia = iterable.asInstanceOf[Iterable[IntColumn]].toArray

  /** Converted array column. */
  def lia = iterable.asInstanceOf[Iterable[LongColumn]].toArray

  /** Converted array column. */
  def sia = iterable.asInstanceOf[Iterable[ShortColumn]].toArray

  /** Converted array column. */
  def Mia = iterable.asInstanceOf[Iterable[MonthColumn]].toArray

  /** Converted array column. */
  def Dia = iterable.asInstanceOf[Iterable[DateColumn]].toArray

  /** Converted array column. */
  def Uia = iterable.asInstanceOf[Iterable[MinuteColumn]].toArray

  /** Converted array column. */
  def Via = iterable.asInstanceOf[Iterable[SecondColumn]].toArray

  /** Converted array column. */
  def Tia = iterable.asInstanceOf[Iterable[TimeColumn]].toArray

  /** Converted array column. */
  def Zia = iterable.asInstanceOf[Iterable[DateTimeColumn]].toArray

  /** Converted array column. */
  def Pia = iterable.asInstanceOf[Iterable[TimestampColumn]].toArray

  /** Converted array column. */
  def Yia = iterable.asInstanceOf[Iterable[SymbolColumn]].toArray

  /** Converted array column. */
  def Sia = iterable.asInstanceOf[Iterable[StringColumn]].toArray

}

