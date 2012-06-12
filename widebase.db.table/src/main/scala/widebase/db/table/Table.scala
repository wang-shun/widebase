package widebase.db.table

import java.io. { ByteArrayInputStream, ByteArrayOutputStream }
import java.nio.channels.Channels
import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable. { Buffer, LinkedHashMap, WrappedArray }

import vario.collection.mutable.HybridBufferLike
import vario.data.Datatype
import vario.io. { VariantReader, VariantWriter }
import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter

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

  TypedColumn,
  MixedTypeException

}

import widebase.io.column. { ColumnReader, ColumnWriter }

/** Column based table.
 *
 * @author myst3r10n
 */
class Table {

  /** Columns. */
  protected var map = LinkedHashMap[Any, TypedColumn[_]]()

  /** Records. */
  object records {

    /** Select a record by its row in the table.
     *
     * @param n the row where records are selected
     *
     * @return the record at row n
     *
     * @note Due performance lack not use by bulk operations.
     */
    def apply(n: Int) =
      for(column <- columns)
        yield(column(n))

    /** Applies a function to all records of this table.
     *
     * @param f self-explanatory
     *
     * @note Due performance lack not use by bulk operations.
     **/
    def foreach[U](f: Iterable[Any] => U) =
      for(r <- 0 to length - 1)
        f(for(column <- columns)
          yield(column(r)))

    /** Get first record */
    def head =
      for(column <- columns)
        yield(column.head)

    /** Get last record */
    def last =
      for(column <- columns)
        yield(column.last)

    /** Amount of records.
     *
     * @return the amount of records
     */
    def length: Int = {

      if(labels == null || labels.length == 0 || columns.size == 0)
        return 0 // No labels or columns

      columns.foreach(column =>
        if(column == null)
          return 0) // Any column undefined

      columns.head.length

    }
  }

  /** Appends new records to columns.
   *
   * @param records the new records to append
   *
   * @return the table itself
   */
  def +=(records: Any*) = {

    append(records.asInstanceOf[WrappedArray[_]])
    this

  }

  /** Appends column to this table.
   *
   * @param pair the label and column to append
   *
   * @return the table itself
   */
  def ++=(pair: (Any, TypedColumn[_])) = {

    columns.foreach(column =>
      if(column != null && column.length != pair._2.length)
        throw RecordsMismatchException(map.values.head.length, pair._2.length))

    map += pair._1 -> pair._2

    this

  }

  /** Appends columns of another table to this table.
   *
   * @param table the table to append
   *
   * @return the table itself
   */
  def ++=(table: Table): Table = {

    if(table.records.length < 1)
      return this

    val others = table.columns.toBuffer

    for(i <- 0 to columns.size - 1) {

      if(columns.toBuffer(i).typeOf != others(i).typeOf)
        throw new MixedTypeException(columns.toBuffer(i).typeOf, others(i).typeOf)

      wasUntyped(i, others(i).head)

      columns.toBuffer(i) match {

        case column: BoolColumn => column ++= others(i).asInstanceOf[BoolColumn]
        case column: ByteColumn => column ++= others(i).asInstanceOf[ByteColumn]
        case column: CharColumn => column ++= others(i).asInstanceOf[CharColumn]
        case column: DoubleColumn => column ++= others(i).asInstanceOf[DoubleColumn]
        case column: FloatColumn => column ++= others(i).asInstanceOf[FloatColumn]
        case column: IntColumn => column ++= others(i).asInstanceOf[IntColumn]
        case column: LongColumn => column ++= others(i).asInstanceOf[LongColumn]
        case column: ShortColumn => column ++= others(i).asInstanceOf[ShortColumn]
        case column: MonthColumn => column ++= others(i).asInstanceOf[MonthColumn]
        case column: DateColumn => column ++= others(i).asInstanceOf[DateColumn]
        case column: MinuteColumn => column ++= others(i).asInstanceOf[MinuteColumn]
        case column: SecondColumn => column ++= others(i).asInstanceOf[SecondColumn]
        case column: TimeColumn => column ++= others(i).asInstanceOf[TimeColumn]
        case column: DateTimeColumn => column ++= others(i).asInstanceOf[DateTimeColumn]
        case column: TimestampColumn => column ++= others(i).asInstanceOf[TimestampColumn]
        case column: SymbolColumn => column ++= others(i).asInstanceOf[SymbolColumn]
        case column: StringColumn => column ++= others(i).asInstanceOf[StringColumn]

      }
    }

    this

  }

  /** Removes column from this table.
   *
   * @param label the label of column to remove
   *
   * @return the table itself
   */
  def --=(label: Any) = {

    if(map.contains(label))
      map -= label

    this

  }

  /** Select a column by its label in the table.
   *
   * @param label where columns are selected
   *
   * @return column by label
   */
  def apply(label: Any) = map(label)

  /** Inserts new records at a given index into columns.
   *
   * @param n the index where new records are inserted
   * @param records the traversable collection containing the records to insert.
   */
  def insert(n: Int, records: Any*) {

    if(records.length != map.values.size)
      throw LengthMismatchException(map.values.size, records.length)

    var i = 0

    def insert(n: Int, value: Any) {

      wasUntyped(i, value)

      columns.toBuffer(i) match {

        case column: BoolColumn => column.insert(n, value.asInstanceOf[Boolean])
        case column: ByteColumn => column.insert(n, value.asInstanceOf[Byte])
        case column: CharColumn => column.insert(n, value.asInstanceOf[Char])
        case column: DoubleColumn => column.insert(n, value.asInstanceOf[Double])
        case column: FloatColumn => column.insert(n, value.asInstanceOf[Float])
        case column: IntColumn => column.insert(n, value.asInstanceOf[Int])
        case column: LongColumn => column.insert(n, value.asInstanceOf[Long])
        case column: ShortColumn => column.insert(n, value.asInstanceOf[Short])
        case column: MonthColumn => column.insert(n, value.asInstanceOf[YearMonth])
        case column: DateColumn => column.insert(n, value.asInstanceOf[LocalDate])
        case column: MinuteColumn => column.insert(n, value.asInstanceOf[Minutes])
        case column: SecondColumn => column.insert(n, value.asInstanceOf[Seconds])
        case column: TimeColumn => column.insert(n, value.asInstanceOf[LocalTime])
        case column: DateTimeColumn => column.insert(n, value.asInstanceOf[LocalDateTime])
        case column: TimestampColumn => column.insert(n, value.asInstanceOf[Timestamp])
        case column: SymbolColumn => column.insert(n, value.asInstanceOf[Symbol])
        case column: StringColumn => column.insert(n, value.asInstanceOf[String])

      }
    }

    records.foreach { record =>

      record match {

        case values: Vector[_] =>
          values.foreach(insert(n, _))
          i = 0

        case value: Any => insert(n, value)

      }

      i += 1

    }
  }

  /** Columns.
   *
   * @return The columns.
   */
  def columns = map.values

  /** Applies a function to all columns of this table.
   *
   * @param f self-explanatory
   */
  def foreach[U](f: ((Any, TypedColumn[_])) =>  U) = map.foreach(f)

  /** labels of columns.
   *
   * @return the labels of columns.
   */
  def labels =
    if(map.keys.size == 0)
      null
    else
      map.keys.head match {

        case label: Boolean => new BoolColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Boolean]]
        case label: Byte => new ByteColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Byte]]
        case label: Char => new CharColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Char]]
        case label: Double => new DoubleColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Double]]
        case label: Float => new FloatColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Float]]
        case label: Int => new IntColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Int]]
        case label: Long => new LongColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Long]]
        case label: Short => new ShortColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Short]]
        case label: YearMonth => new MonthColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[YearMonth]]
        case label: LocalDate => new DateColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[LocalDate]]
        case label: Minutes => new MinuteColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Minutes]]
        case label: Seconds => new SecondColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Seconds]]
        case label: LocalTime => new TimeColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[LocalTime]]
        case label: LocalDateTime => new DateTimeColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[LocalDateTime]]
        case label: Timestamp => new TimestampColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Timestamp]]
        case label: Symbol => new SymbolColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[Symbol]]
        case label: String => new StringColumn ++= map.keys.toBuffer.asInstanceOf[Buffer[String]]

      }

  def update(label: Any, column: TypedColumn[_]) {

    map(label) = column

  }

  /** Converts this table to bytes.
   *
   * @param filter self-explanatory
   * @param records to convert, 0 convert all
   *
   * @return [[scala.Byte]]s of table
   */
  def toBytes(filter: StreamFilter = StreamFilter.None)
    (implicit records: Int = 0) = {

    val stream = new ByteArrayOutputStream
    val channel = Channels.newChannel(stream)

    val vwriter = new VariantWriter(channel, filter) {

      override val charset = props.charsets.to

      override def order = props.orders.to

    }

    val writer = new ColumnWriter(vwriter)

    // Write column labels
    writer.write(labels)

    // Write columns
    columns.foreach(column => writer.write(column, records))

    writer.close

    stream.toByteArray
  
  }

  /** A printable [[widebase.db.table.Table]]. */
  override def toString: String = {

    if(records.length == 0)
      return "Empty"

    var printable = ""
    val lineSeparator = System.getProperty("line.separator")

    var amount = records.length

    if(amount > 5)
      amount = 5

    if(amount > 0) {

      amount -= 1

      for(r <- 0 to amount) {

        printable += records(r)

        if(r < amount)
          printable += lineSeparator

      }
    }

    if(records.length > 5)
      printable += lineSeparator + "..."

    printable

  }

  /** Appends new records to columns.
   *
   * @param records the new records to append
   *
   * @note Due performance lack not use by bulk operations.
   */
  protected def append(records: WrappedArray[_]) {

    if(map.values.size != records.length)
      throw LengthMismatchException(map.values.size, records.length)

    var i = 0

    def insert(value: Any) {

      wasUntyped(i, value)

      columns.toBuffer(i) match {

        case column: BoolColumn => column += value.asInstanceOf[Boolean]
        case column: ByteColumn => column += value.asInstanceOf[Byte]
        case column: CharColumn => column += value.asInstanceOf[Char]
        case column: DoubleColumn => column += value.asInstanceOf[Double]
        case column: FloatColumn => column += value.asInstanceOf[Float]
        case column: IntColumn => column += value.asInstanceOf[Int]
        case column: LongColumn => column += value.asInstanceOf[Long]
        case column: ShortColumn => column += value.asInstanceOf[Short]
        case column: MonthColumn => column += value.asInstanceOf[YearMonth]
        case column: DateColumn => column += value.asInstanceOf[LocalDate]
        case column: MinuteColumn => column += value.asInstanceOf[Minutes]
        case column: SecondColumn => column += value.asInstanceOf[Seconds]
        case column: TimeColumn => column += value.asInstanceOf[LocalTime]
        case column: DateTimeColumn => column += value.asInstanceOf[LocalDateTime]
        case column: TimestampColumn => column += value.asInstanceOf[Timestamp]
        case column: SymbolColumn => column += value.asInstanceOf[Symbol]
        case column: StringColumn => column += value.asInstanceOf[String]

      }
    }

    records.foreach { record =>

      record match {

        case values: Vector[_] =>
          values.foreach(insert(_))
          i = 0

        case value: Any =>
          insert(value)
      }

      i += 1

    }
  }

  /** Ensure that column is typed.
   *
   * @param index of column
   * @param value with type
   */
  protected def wasUntyped(index: Int, value: Any) {

    if(columns.toBuffer(index) == null)
      value match {

        case value: Boolean => map(map.keys.toBuffer(index)) = new BoolColumn
        case value: Byte => map(map.keys.toBuffer(index)) = new ByteColumn
        case value: Char => map(map.keys.toBuffer(index)) = new CharColumn
        case value: Double => map(map.keys.toBuffer(index)) = new DoubleColumn
        case value: Float => map(map.keys.toBuffer(index)) = new FloatColumn
        case value: Int => map(map.keys.toBuffer(index)) = new IntColumn
        case value: Long => map(map.keys.toBuffer(index)) = new LongColumn
        case value: Short => map(map.keys.toBuffer(index)) = new ShortColumn
        case value: YearMonth => map(map.keys.toBuffer(index)) = new MonthColumn
        case value: LocalDate => map(map.keys.toBuffer(index)) = new DateColumn
        case value: Minutes => map(map.keys.toBuffer(index)) = new MinuteColumn
        case value: Seconds => map(map.keys.toBuffer(index)) = new SecondColumn
        case value: LocalTime => map(map.keys.toBuffer(index)) = new TimeColumn
        case value: LocalDateTime => map(map.keys.toBuffer(index)) = new DateTimeColumn
        case value: Timestamp => map(map.keys.toBuffer(index)) = new TimestampColumn
        case value: Symbol => map(map.keys.toBuffer(index)) = new SymbolColumn
        case value: String => map(map.keys.toBuffer(index)) = new StringColumn

      }
  }
}

/** Companion of [[widebase.db.table.Table]].
 *
 * @author myst3r10n
 */
object Table {

  /** Creates [[widebase.db.table.Table]].
   *
   * @param labels of table
   * @param columns of table
   */
  def apply(labels: TypedColumn[_], columns: TypedColumn[_]*) = {

    var labelIndex = 0
    var columnIndex = 0
    val table = new Table

    while(
      labels != null &&
      labelIndex < labels.length &&
      columnIndex < columns.length) {

      table ++= labels(labelIndex) -> columns(columnIndex)

      labelIndex += 1
      columnIndex += 1

    }

    while(labels != null && labelIndex < labels.length) {

      table ++= labels(labelIndex) -> null

      labelIndex += 1
      columnIndex += 1

    }

    while(columnIndex < columns.length) {

      table ++= "" -> columns(columnIndex)

      labelIndex += 1
      columnIndex += 1

    }

    table

  }

  /** Creates [[widebase.db.table.Table]] from bytes.
   *
   * @param bytes of table
   * @param filter self-explanatory
   * @param records to recover, 0 recover all
   */
  def fromBytes(
    bytes: Array[Byte],
    filter: StreamFilter = StreamFilter.None)
    (implicit records: Int = 0) = {

    val stream = new ByteArrayInputStream(bytes)
    val channel = Channels.newChannel(stream)

    val vreader = new VariantReader(channel, filter) {

      override val charset = props.charsets.from

      override def order = props.orders.from

    }

    val reader = new ColumnReader(vreader)

    val table = new Table

    reader.read().foreach(label => table ++= (label, reader.read(records)))

    reader.close

    table
  
  }
}

