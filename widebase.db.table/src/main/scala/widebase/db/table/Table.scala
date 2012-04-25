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

  AnyColumn,
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
 * @param l labels
 * @param c columns
 *
 * @author myst3r10n
 */
case class Table(
  protected val l: TypedColumn[_],
  protected val c: TypedColumn[_]*) {

  def this() = this(null)

  /** Columns. */
  protected var map = LinkedHashMap[Any, TypedColumn[_]]()

  {

    var labelIndex = 0
    var columnIndex = 0

    while(l != null && labelIndex < l.length && columnIndex < c.length) {

      map += l(labelIndex) -> c(columnIndex)

      labelIndex += 1
      columnIndex += 1

    }

    while(l != null && labelIndex < l.length) {

      map += l(labelIndex) -> new AnyColumn

      labelIndex += 1
      columnIndex += 1

    }

    while(columnIndex < c.length) {

      map += "" -> c(columnIndex)

      labelIndex += 1
      columnIndex += 1

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

    /** Applies a function to all records of this table.
     *
     * @param f self-explanatory
     *
     * @note Due performance lack not use by bulk operations.
     **/
    def foreach[U](f: Iterable[Any] => U) =
      for(r <- 0 to size - 1)
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
    def size =
      if(columns.size <= 0)
        0
      else
        columns.head.length

  }

  /** Implements sort.
   *
   * @author myst3r10n
   */
  object sort {

    import SortDirection.SortDirection

    /** Sort a table directly
      *
      * @param label label of column
      * @param method the sort method
      * @param direction the sort direction
     */
    def apply(
      label: Any,
      method: Symbol,
      direction: Symbol = 'a) {

      val _direction = direction match {

        case direction if
          direction == 'a ||
          direction == 'asc ||
          direction == 'ascending => SortDirection.Ascending
        case direction if
          direction == 'd ||
          direction == 'desc ||
          direction == 'descending => SortDirection.Descending

      }

      method match {

        case method if
          method == 'i ||
          method == 'insert ||
          method == 'insertion => insertion(label, _direction)
        case method if
          method == 's ||
          method == 'select ||
          method == 'selection => selection(label, _direction)

      }

    }

    /** Sort table by insertion algorithm
      *
      * @param label label of column
      * @param direction the sort direction
     */
    def insertion(
      label: Any,
      direction: SortDirection) {

      val primary = Table.this(label)

      for(i <- 1 to primary.length - 1) {

        var j = i - 1
        var done = false
        val value = primary(i)
        val values = (for(column <- columns) yield(column(i))).toBuffer

        do {

          var trigger = false

          primary.typeOf match {

            case Datatype.Bool =>
            case Datatype.Byte =>
            case Datatype.Char =>
            case Datatype.Double =>
            case Datatype.Float =>
            case Datatype.Int =>
            case Datatype.Long =>
            case Datatype.Short =>
            case Datatype.Month =>
            case Datatype.Date =>
            case Datatype.Minute =>
            case Datatype.Second =>
            case Datatype.Time =>
            case Datatype.DateTime =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[LocalDateTime]
                    .compareTo(value.asInstanceOf[LocalDateTime]) > 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[LocalDateTime]
                    .compareTo(value.asInstanceOf[LocalDateTime]) < 0)
                    trigger = true

              }

            case Datatype.Timestamp =>
            case Datatype.Symbol =>
            case Datatype.String =>

          }

          if(trigger) {

            columns.foreach {

              case column: BoolColumn => column(j + 1) = column(j)
              case column: ByteColumn => column(j + 1) = column(j)
              case column: CharColumn => column(j + 1) = column(j)
              case column: DoubleColumn => column(j + 1) = column(j)
              case column: FloatColumn => column(j + 1) = column(j)
              case column: IntColumn => column(j + 1) = column(j)
              case column: LongColumn => column(j + 1) = column(j)
              case column: ShortColumn => column(j + 1) = column(j)
              case column: MonthColumn => column(j + 1) = column(j)
              case column: DateColumn => column(j + 1) = column(j)
              case column: MinuteColumn => column(j + 1) = column(j)
              case column: SecondColumn => column(j + 1) = column(j)
              case column: TimeColumn => column(j + 1) = column(j)
              case column: DateTimeColumn => column(j + 1) = column(j)
              case column: TimestampColumn => column(j + 1) = column(j)
              case column: SymbolColumn =>column(j + 1) = column(j)
              case column: StringColumn =>column(j + 1) = column(j)

            }

            j -= 1

            if(j < 0)
              done = true

          } else
            done = true

        } while(!done)

        var k = 0

        columns.foreach {

          case column: BoolColumn => column(j + 1) = values(k).asInstanceOf[Boolean]
          case column: ByteColumn => column(j + 1) = values(k).asInstanceOf[Byte]
          case column: CharColumn => column(j + 1) = values(k).asInstanceOf[Char]
          case column: DoubleColumn => column(j + 1) = values(k).asInstanceOf[Double]
          case column: FloatColumn => column(j + 1) = values(k).asInstanceOf[Float]
          case column: IntColumn => column(j + 1) = values(k).asInstanceOf[Int]
          case column: LongColumn => column(j + 1) = values(k).asInstanceOf[Long]
          case column: ShortColumn => column(j + 1) = values(k).asInstanceOf[Short]
          case column: MonthColumn => column(j + 1) = values(k).asInstanceOf[YearMonth]
          case column: DateColumn => column(j + 1) = values(k).asInstanceOf[LocalDate]
          case column: MinuteColumn => column(j + 1) = values(k).asInstanceOf[Minutes]
          case column: SecondColumn => column(j + 1) = values(k).asInstanceOf[Seconds]
          case column: TimeColumn => column(j + 1) = values(k).asInstanceOf[LocalTime]
          case column: DateTimeColumn => column(j + 1) = values(k).asInstanceOf[LocalDateTime]
          case column: TimestampColumn => column(j + 1) = values(k).asInstanceOf[Timestamp]
          case column: SymbolColumn => column(j + 1) = values(k).asInstanceOf[Symbol]
          case column: StringColumn => column(j + 1) = values(k).asInstanceOf[String]

          k += 1

        }
      }
    }

    /** Sort table by selection algorithm
      *
      * @param label label of column
      * @param direction the sort direction
     */
    def selection(
      label: Any,
      direction: SortDirection) {

      val primary = Table.this(label)

      for(i <- 1 to primary.length - 1) {

        var pos = -1
        var value: Any = null

        for(j <- i to primary.length - 1) {

          if(j == i) {

            pos = j
            value = primary(pos)

          } else {

            var trigger = false

            primary.typeOf match {

              case Datatype.Bool =>
              case Datatype.Byte =>
              case Datatype.Char =>
              case Datatype.Double =>
              case Datatype.Float =>
              case Datatype.Int =>
              case Datatype.Long =>
              case Datatype.Short =>
              case Datatype.Month =>
              case Datatype.Date =>
              case Datatype.Minute =>
              case Datatype.Second =>
              case Datatype.Time =>
              case Datatype.DateTime =>
                direction match {

                  case SortDirection.Ascending =>
                    if(primary(j).asInstanceOf[LocalDateTime]
                      .compareTo(value.asInstanceOf[LocalDateTime]) < 0)
                      trigger = true

                  case SortDirection.Descending =>
                    if(primary(j).asInstanceOf[LocalDateTime]
                      .compareTo(value.asInstanceOf[LocalDateTime]) > 0)
                      trigger = true

                }

              case Datatype.Timestamp =>
              case Datatype.Symbol =>
              case Datatype.String =>

            }

            if(trigger) {

              pos = j
              value = primary(pos)

            }
          }
        }

        if(i != pos)
          columns.foreach {

            case column: BoolColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: ByteColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: CharColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: DoubleColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: FloatColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: IntColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: LongColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: ShortColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: MonthColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: DateColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: MinuteColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: SecondColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: TimeColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: DateTimeColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: TimestampColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: SymbolColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

            case column: StringColumn =>
              val backup = column(pos)
              column(pos) = column(i)
              column(i) = backup

          }
      }
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

    if(map.values.size > 0 && map.values.head.length != pair._2.length)
      throw RecordsMismatchException(map.values.head.length, pair._2.length)

    map += pair._1 -> pair._2

    this

  }

  /** Appends columns of another table to this table.
   *
   * @param table the table to append
   *
   * @return the table itself
   */
  def ++=(table: Table) = {

    val columns = this.columns.toBuffer
    val others = table.columns.toBuffer

    for(i <- 0 to columns.size - 1) {

      if(columns(i).typeOf != others(i).typeOf)
        throw new MixedTypeException(columns(i).typeOf, others(i).typeOf)

      columns(i) match {

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

    if(records.size != map.values.size)
      throw LengthMismatchException(map.values.size, records.size)

    var iterator = map.values.toIterator

    def insert(n: Int, value: Any) {

      iterator.next match {

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
          iterator = map.values.toIterator

        case value: Any => insert(n, value)

      }
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
   * @return The labels of columns.
   */
  def labels =
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
   *
   * @return [[scala.Byte]]s of table
   */
  def toBytes(filter: StreamFilter = StreamFilter.None) = {

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
    columns.foreach(column => writer.write(column))

    writer.close

    stream.toByteArray
  
  }

  /** Appends new records to columns.
   *
   * @param records the new records to append
   *
   * @note Due performance lack not use by bulk operations.
   */
  protected def append(records: WrappedArray[_]) {

    if(records.size != map.values.size)
      throw LengthMismatchException(map.values.size, records.size)

    var i = 0
    val buffer = columns.toBuffer

    def insert(value: Any) {

      if(buffer(i).isInstanceOf[AnyColumn])
        value match {

          case value: Boolean => buffer(i) = new BoolColumn
          case value: Byte => buffer(i) = new ByteColumn
          case value: Char => buffer(i) = new CharColumn
          case value: Double => buffer(i) = new DoubleColumn
          case value: Float => buffer(i) = new FloatColumn
          case value: Int => buffer(i) = new IntColumn
          case value: Long => buffer(i) = new LongColumn
          case value: Short => buffer(i) = new ShortColumn
          case value: YearMonth => buffer(i) = new MonthColumn
          case value: LocalDate => buffer(i) = new DateColumn
          case value: Minutes => buffer(i) = new MinuteColumn
          case value: Seconds => buffer(i) = new SecondColumn
          case value: LocalTime => buffer(i) = new TimeColumn
          case value: LocalDateTime => buffer(i) = new DateTimeColumn
          case value: Timestamp => buffer(i) = new TimestampColumn
          case value: Symbol => buffer(i) = new SymbolColumn
          case value: String => buffer(i) = new StringColumn

        }

      buffer(i) match {

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
}

/** Companion of [[widebase.db.table.Table]].
 *
 * @author myst3r10n
 */
object Table {

  /** Creates [[widebase.db.table.Table]] from bytes.
   *
   * @param bytes of table
   * @param filter self-explanatory
   */
  def fromBytes(
    bytes: Array[Byte],
    filter: StreamFilter = StreamFilter.None) = {

    val stream = new ByteArrayInputStream(bytes)
    val channel = Channels.newChannel(stream)

    val vreader = new VariantReader(channel, filter) {

      override val charset = props.charsets.from

      override def order = props.orders.from

    }

    val reader = new ColumnReader(vreader)

    val table = new Table

    reader.read.foreach(label => table ++= (label, reader.read))

    reader.close

    table
  
  }
}

