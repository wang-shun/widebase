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

import scala.collection.mutable. {

  LinkedHashMap,
  LinkedHashSet,
  WrappedArray

}

import vario.data.Datatype
import vario.io. { VariantReader, VariantWriter }
import vario.filter.StreamFilter
import vario.filter.StreamFilter.StreamFilter

import widebase.db.column.VariantColumn
import widebase.io.column. { ColumnReader, ColumnWriter }

/** Column based table.
 *
 * @author myst3r10n
 */
case class Table(
  protected val n: LinkedHashSet[String],
  protected val c: VariantColumn*) {

  def this() = this(LinkedHashSet[String](), null)
  def this(n: LinkedHashSet[String]) = this(n, null)

  /** Columns. */
  protected var map = LinkedHashMap(n.zip(c ++ WrappedArray.make[VariantColumn](
    Array.fill(n.size - c.size)(new VariantColumn))).toSeq:_*)

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
      label: String,
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
      label: String,
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

            columns.foreach(column => column(j + 1) = column(j))

            j -= 1

            if(j < 0)
              done = true

          } else
            done = true

        } while(!done)

        var k = 0

        columns.foreach { column =>

          column(j + 1) = values(k)

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
      label: String,
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
          columns.foreach { column =>

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
  def ++=(pair: (String, VariantColumn)) = {

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
    val other = table.columns.toBuffer

    for(i <- 0 to columns.size - 1)
      columns(i) ++= other(i)

    this

  }

  /** Removes column from this table.
   *
   * @param label the label of column to remove
   *
   * @return the table itself
   */
  def --=(label: String) = {

    if(map.contains(label))
      map -= label

    this

  }

  /** Select a column by its label in the table.
   *
   * @param column The label where columns are selected.
   *
   * @return The column by label.
   */
  def apply(column: String) = map(column)

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

      value match {

        case value: Byte => iterator.next += value
        case value: Char => iterator.next += value
        case value: Double => iterator.next += value
        case value: Float => iterator.next += value
        case value: Int => iterator.next += value
        case value: Long => iterator.next += value
        case value: Short => iterator.next += value
        case value: YearMonth => iterator.next += value
        case value: LocalDate => iterator.next += value
        case value: Minutes => iterator.next += value
        case value: Seconds => iterator.next += value
        case value: LocalTime => iterator.next += value
        case value: LocalDateTime => iterator.next += value
        case value: Timestamp => iterator.next += value
        case value: String => iterator.next += value

      }
    }

    records.foreach { record =>

      record match {

        case values: Vector[_] =>
          values.foreach(insert(n, _))
          iterator = map.values.toIterator

        case value: Any =>
          insert(n, value)

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
  def foreach[U](f: ((String, VariantColumn)) =>  U) = map.foreach(f)

  /** labels of columns.
   *
   * @return The labels of columns.
   */
  def labels = map.keys

  def update(label: String, column: VariantColumn) {

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
    val labels = new VariantColumn('S)
    labels.strings ++= this.labels
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

      if(buffer(i).typeOf == Datatype.None)
        value match {

          case typedValue: Boolean => buffer(i) += typedValue
          case typedValue: Byte => buffer(i) += typedValue
          case typedValue: Char => buffer(i) += typedValue
          case typedValue: Double => buffer(i) += typedValue
          case typedValue: Float => buffer(i) += typedValue
          case typedValue: Int => buffer(i) += typedValue
          case typedValue: Long => buffer(i) += typedValue
          case typedValue: Short => buffer(i) += typedValue
          case typedValue: YearMonth => buffer(i) += typedValue
          case typedValue: LocalDate => buffer(i) += typedValue
          case typedValue: Minutes => buffer(i) += typedValue
          case typedValue: Seconds => buffer(i) += typedValue
          case typedValue: LocalTime => buffer(i) += typedValue
          case typedValue: LocalDateTime => buffer(i) += typedValue
          case typedValue: Timestamp => buffer(i) += typedValue
          case typedValue: Symbol => buffer(i) += typedValue
          case typedValue: String => buffer(i) += typedValue

        }
      else
        value match {

          case typedValue: Boolean => buffer(i).bools += typedValue
          case typedValue: Byte => buffer(i).bytes += typedValue
          case typedValue: Char => buffer(i).chars += typedValue
          case typedValue: Double => buffer(i).doubles += typedValue
          case typedValue: Float => buffer(i).floats += typedValue
          case typedValue: Int => buffer(i).ints += typedValue
          case typedValue: Long => buffer(i).longs += typedValue
          case typedValue: Short => buffer(i).shorts += typedValue
          case typedValue: YearMonth => buffer(i).months += typedValue
          case typedValue: LocalDate => buffer(i).dates += typedValue
          case typedValue: Minutes => buffer(i).minutes += typedValue
          case typedValue: Seconds => buffer(i).seconds += typedValue
          case typedValue: LocalTime => buffer(i).times += typedValue
          case typedValue: LocalDateTime => buffer(i).dateTimes += typedValue
          case typedValue: Timestamp => buffer(i).timestamps += typedValue
          case typedValue: Symbol => buffer(i).symbols += typedValue
          case typedValue: String => buffer(i).strings += typedValue

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

    reader.read.strings.foreach(label => table ++= (label, reader.read))

    reader.close

    table
  
  }
}

