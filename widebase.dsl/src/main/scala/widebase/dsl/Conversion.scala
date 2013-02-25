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

/** A collection of implicit conversions.
 *
 * @author myst3r10n
 */
object Conversion {

  /** Implicitly converts a [[scala.Any]] into [[widebase.dsl.AnyConversion]].
   *
   * @param value to convert
   *
   * @return a conversion purposed object
   */
  implicit def asAnyConversion(value: Any) = new AnyConversion(value)

  /** Implicitly converts a [[scala.Int]] into [[widebase.dsl.IntConversion]].
   *
   * @param column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asIntConversion(int: Int) = new IntConversion(int)

  /** Implicitly converts a [[java.lang.String]] into dsl [[widebase.dsl.StringConversion]].
   *
   * @param column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asStringConversion(text: String) = new StringConversion(text)

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.BoolColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asBoolColumn(column: TypedColumn[_]) =
    column.asInstanceOf[BoolColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.ByteColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asByteColumn(column: TypedColumn[_]) =
    column.asInstanceOf[ByteColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.CharColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asCharColumn(column: TypedColumn[_]) =
    column.asInstanceOf[CharColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.DoubleColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asDoubleColumn(column: TypedColumn[_]) =
    column.asInstanceOf[DoubleColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.FloatColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asFloatColumn(column: TypedColumn[_]) =
    column.asInstanceOf[FloatColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.IntColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asIntColumn(column: TypedColumn[_]) =
    column.asInstanceOf[IntColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.LongColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asLongColumn(column: TypedColumn[_]) =
    column.asInstanceOf[LongColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.ShortColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asShortColumn(column: TypedColumn[_]) =
    column.asInstanceOf[ShortColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.MonthColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asMonthColumn(column: TypedColumn[_]) =
    column.asInstanceOf[MonthColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.DateColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asDateColumn(column: TypedColumn[_]) =
    column.asInstanceOf[DateColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.MinuteColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asMinuteColumn(column: TypedColumn[_]) =
    column.asInstanceOf[MinuteColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.SecondColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asSecondColumn(column: TypedColumn[_]) =
    column.asInstanceOf[SecondColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.TimeColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asTimeColumn(column: TypedColumn[_]) =
    column.asInstanceOf[TimeColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.DateTimeColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asDateTimeColumn(column: TypedColumn[_]) =
    column.asInstanceOf[DateTimeColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.TimestampColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asTimestampColumn(column: TypedColumn[_]) =
    column.asInstanceOf[TimestampColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.SymbolColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asSymbolColumn(column: TypedColumn[_]) =
    column.asInstanceOf[SymbolColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.db.column.StringColumn]].
   *
   * @param column to convert
   *
   * @return converted column
   */
  implicit def asStringColumn(column: TypedColumn[_]) =
    column.asInstanceOf[StringColumn]

  /** Implicitly converts a [[widebase.db.column.TypedColumn]] into [[widebase.dsl.ColumnConversion]].
   *
   * @param column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asColumnConversion(column: TypedColumn[_]) =
    new ColumnConversion(column)

  /** Implicitly converts a array of [[widebase.db.column.TypedColumn]] into [[widebase.dsl.ArrayConversion]].
   *
   * @param iterable column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asArrayConversion(array: Array[TypedColumn[_]]) =
    new ArrayConversion(array)

  /** Implicitly converts a iterable [[widebase.db.column.TypedColumn]] into [[widebase.dsl.ArrayIterableConversion]].
   *
   * @param iterable column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asArrayIterableConversion(iterable: Iterable[TypedColumn[_]]) =
    new ArrayIterableConversion(iterable)

  /** Implicitly converts a iterable [[widebase.db.column.TypedColumn]] into [[widebase.dsl.IterableConversion]].
   *
   * @param iterable column to convert
   *
   * @return a conversion purposed object
   */
  implicit def asIterableConversion(iterable: Iterable[TypedColumn[_]]) =
    new IterableConversion(iterable)

}

