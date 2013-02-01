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

import widebase.collection.mutable.HybridBufferLike
import widebase.data.Datatype

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

import widebase.io. { VariantReader, VariantWriter }
import widebase.io.column. { ColumnReader, ColumnWriter }
import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Column based table.
 *
 * @author myst3r10n
 */
class Table {

  /** Columns. */
  protected var map = LinkedHashMap[Any, TypedColumn[_]]()

  /** Copy. */
  protected object copy {

    def label = {

      val table = new Table
      val thisColumns = columns.toBuffer // Performance purposes

      var i = 0

      labels.foreach { label =>

        thisColumns(i).typeOf match {

          case Datatype.Bool => table ++= (label, new BoolColumn)
          case Datatype.Byte => table ++= (label, new ByteColumn)
          case Datatype.Char => table ++= (label, new CharColumn)
          case Datatype.Double => table ++= (label, new DoubleColumn)
          case Datatype.Float => table ++= (label, new FloatColumn)
          case Datatype.Int => table ++= (label, new IntColumn)
          case Datatype.Long => table ++= (label, new LongColumn)
          case Datatype.Short => table ++= (label, new ShortColumn)
          case Datatype.Month => table ++= (label, new MonthColumn)
          case Datatype.Date => table ++= (label, new DateColumn)
          case Datatype.Minute => table ++= (label, new MinuteColumn)
          case Datatype.Second => table ++= (label, new SecondColumn)
          case Datatype.Time => table ++= (label, new TimeColumn)
          case Datatype.DateTime => table ++= (label, new DateTimeColumn)
          case Datatype.Timestamp => table ++= (label, new TimestampColumn)
          case Datatype.Symbol => table ++= (label, new SymbolColumn)
          case Datatype.String => table ++= (label, new StringColumn)

        }

        i += 1

      }

      table

    }

    def record(r: Int, from: Buffer[TypedColumn[_]], to: Buffer[TypedColumn[_]]) {

      for(i <- 0 to columns.size - 1)
        to(i) match {

          case column: BoolColumn => column += from(i)(r).asInstanceOf[Boolean]
          case column: ByteColumn => column += from(i)(r).asInstanceOf[Byte]
          case column: CharColumn => column += from(i)(r).asInstanceOf[Char]
          case column: DoubleColumn => column += from(i)(r).asInstanceOf[Double]
          case column: FloatColumn => column += from(i)(r).asInstanceOf[Float]
          case column: IntColumn => column += from(i)(r).asInstanceOf[Int]
          case column: LongColumn => column += from(i)(r).asInstanceOf[Long]
          case column: ShortColumn => column += from(i)(r).asInstanceOf[Short]
          case column: MonthColumn => column += from(i)(r).asInstanceOf[YearMonth]
          case column: DateColumn => column += from(i)(r).asInstanceOf[LocalDate]
          case column: MinuteColumn => column += from(i)(r).asInstanceOf[Minutes]
          case column: SecondColumn => column += from(i)(r).asInstanceOf[Seconds]
          case column: TimeColumn => column += from(i)(r).asInstanceOf[LocalTime]
          case column: DateTimeColumn => column += from(i)(r).asInstanceOf[LocalDateTime]
          case column: TimestampColumn => column += from(i)(r).asInstanceOf[Timestamp]
          case column: SymbolColumn => column += from(i)(r).asInstanceOf[Symbol]
          case column: StringColumn => column += from(i)(r).asInstanceOf[String]

        }
    }
  }

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

    /** Counts the number of records in the table which satisfy a predicate..
     *
     * @param predicate used to test elements
     *
     * @return number of elements satisfying the predicate
     */
    def count(predicate: Record => Boolean) = {

      var i = 0

      // Performance purposes
      val thisColumns = columns.toBuffer

      for(r <- 0 to length - 1)
        if(predicate(Record(labels, records(r).toArray)))
          i += 1

      i

    }

    /** Tests whether a predicate holds for some of the records of this table.
     *
     * @param predicate used to test elements
     *
     * @return true if exists, else false
     */
    def exists(predicate: Record => Boolean): Boolean = {

      // Performance purposes
      val thisColumns = columns.toBuffer

      for(r <- 0 to length - 1)
        if(predicate(Record(labels, records(r).toArray)))
          return true

      false

    }

    /** Finds the first element of the table satisfying a predicate, if any.
     *
     * @param predicate used to test elements
     *
     * @return option of table
     */
    def find(predicate: Record => Boolean): Option[Iterable[_]] = {

      // Performance purposes
      val thisColumns = columns.toBuffer

      for(r <- 0 to length - 1)
        if(predicate(Record(labels, records(r).toArray)))
          return Some(records(r))

      None

    }

    /** Applies a function to all records of this table.
     *
     * @param function apply to all records
     *
     * @note Due performance lack not use by bulk operations.
     **/
    def foreach[U](function: Iterable[Any] => U) =
      for(r <- 0 to length - 1)
        function(for(column <- columns)
          yield(column(r)))

    /** Tests whether a predicate holds for all records of this table.
     *
     * @param predicate used to test elements
     *
     * @return `true` if predicate holds for all records, else `false`
     */
    def forall(predicate: Record => Boolean): Boolean = {

      val thisColumns = columns.toBuffer

      for(r <- 0 to length - 1)
        if(!predicate(Record(labels, records(r).toArray)))
          return false

      true

    }

    /** Get first record */
    def head =
      for(column <- columns)
        yield(column.head)

    /** Finds index of first record satisfying some predicate.
     *
     * @param predicate used to test elements
     *
     * @return >= 0 if found, else -1
     */
    def indexWhere(predicate: Record => Boolean): Int =
      indexWhere(predicate, 0)

    /** Finds index of first record satisfying some predicate.
     *
     * @param predicate used to test elements
     * @param from start index
     *
     * @return >= 0 if found, else -1
     */
    def indexWhere(predicate: Record => Boolean, from: Int): Int = {

      val thisColumns = columns.toBuffer

      for(r <- from to length - 1)
        if(!predicate(Record(labels, records(r).toArray)))
          return r

      -1

    }

    /** Tests whether any record exists. */
    def isEmpty = length == 0

    /** Get last record */
    def last =
      for(column <- columns)
        yield(column.last)

    /** Finds index of last record satisfying some predicate.
     *
     * @param predicate used to test elements
     *
     * @return >= 0 if found, else -1
     */
    def lastIndexWhere(predicate: Record => Boolean): Int =
      lastIndexWhere(predicate, 0)

    /** Finds index of last record satisfying some predicate.
     *
     * @param predicate used to test elements
     * @param from start index
     *
     * @return >= 0 if found, else -1
     */
    def lastIndexWhere(predicate: Record => Boolean, from: Int): Int = {

      val thisColumns = columns.toBuffer

      var r = length - 1

      while(r >= 0) {

        if(!predicate(Record(labels, records(r).toArray)))
          return r

        r -= 1

      }

      -1

    }

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

  /** Columns.
   *
   * @return The columns.
   */
  def columns = map.values

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

  /** Filters all records of this table which satisfy a predicate.
   *
   * @param predicate used to test records.
   *
   * @note Is slower than `filter` by widebase.db.table.TemplateTable
   *
   * @return filtered table
   */
  def filter(predicate: Record => Boolean) = {

    val filteredTable = copy.label

    // Performance purposes
    val thisColumns = columns.toBuffer
    val filteredColumns = filteredTable.columns.toBuffer

    for(r <- 0 to records.length - 1)
      if(predicate(Record(labels, records(r).toArray)))
        copy.record(r, thisColumns, filteredColumns)

    filteredTable

  }

  /** Filters all records of this table which do not satisfy a predicate.
   *
   * @param predicate used to test records.
   *
   * @return filtered table
   */
  def filterNot(predicate: Record => Boolean) = filter(!predicate(_))

  /** Applies a function to all columns of this table.
   *
   * @param function apply to all columns
   */
  def foreach[U](function: ((Any, TypedColumn[_])) =>  U) = map.foreach(function)

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

