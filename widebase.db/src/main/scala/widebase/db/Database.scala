package widebase.db

import java.io.File
import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

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

import widebase.io.column. {

  FileColumnFinder,
  FileColumnLoader,
  FileColumnMapper,
  FileColumnSaver,
  FileRecordEditor

}

import widebase.io.table. {

  FileTableFind,
  FileTableLoad,
  FileTableMap,
  FileTableSave

}

/** Interface to column based database.
 *
 * @param path of database
 * @param segment of database
 *
 * @author myst3r10n
 */
class Database protected[db](val path: String, val segment: SegmentMap) {

  /** Converts segment key into segment path.
   *
   * @param key to convert
   *
   * @return segment path
   */
  implicit def asSegmentPath(key: String) = {

    class SegmentConversions(key: String) {

      def S = segment(key)

    }

    new SegmentConversions(key)

  }

  object tables extends FileRecordEditor(path) {

    /** Attach column into directory table.
      *
      * @param name of table
      * @param label of column
      * @param column to attach
      * @param parted partition name
      * @param segmented path of segment
     */
    def attach[A](name: String, label: Any, column: TypedColumn[A])
      (implicit parted: String = null, segmented: File = null) {

      save.col(name, label.toString, column)(parted, segmented)

      val labels = load.col(name, ".d")(parted, segmented)

      label match {

        case label: Boolean => labels.asInstanceOf[BoolColumn] += label
        case label: Byte => labels.asInstanceOf[ByteColumn] += label
        case label: Char => labels.asInstanceOf[CharColumn] += label
        case label: Double => labels.asInstanceOf[DoubleColumn] += label
        case label: Float => labels.asInstanceOf[FloatColumn] += label
        case label: Int => labels.asInstanceOf[IntColumn] += label
        case label: Long => labels.asInstanceOf[LongColumn] += label
        case label: Short => labels.asInstanceOf[ShortColumn] += label
        case label: YearMonth => labels.asInstanceOf[MonthColumn] += label
        case label: LocalDate => labels.asInstanceOf[DateColumn] += label
        case label: Minutes => labels.asInstanceOf[MinuteColumn] += label
        case label: Seconds => labels.asInstanceOf[SecondColumn] += label
        case label: LocalTime => labels.asInstanceOf[TimeColumn] += label
        case label: LocalDateTime => labels.asInstanceOf[DateTimeColumn] += label
        case label: Timestamp => labels.asInstanceOf[TimestampColumn] += label
        case label: Symbol => labels.asInstanceOf[SymbolColumn] += label
        case label: String => labels.asInstanceOf[StringColumn] += label

      }

      save.col(name, ".d", labels, true)(parted, segmented)

    }

    /** Detach column from directory table.
      *
      * @param name of table
      * @param label of column
      * @param parted partition name
      * @param segmented path of segment
     */
    def detach(name: String, label: Any)
      (implicit parted: String = null, segmented: File = null) {

      val labels = load.col(name, ".d")(parted, segmented)

      label match {

        case label: Boolean => labels.asInstanceOf[BoolColumn] -= label
        case label: Byte => labels.asInstanceOf[ByteColumn] -= label
        case label: Char => labels.asInstanceOf[CharColumn] -= label
        case label: Double => labels.asInstanceOf[DoubleColumn] -= label
        case label: Float => labels.asInstanceOf[FloatColumn] -= label
        case label: Int => labels.asInstanceOf[IntColumn] -= label
        case label: Long => labels.asInstanceOf[LongColumn] -= label
        case label: Short => labels.asInstanceOf[ShortColumn] -= label
        case label: YearMonth => labels.asInstanceOf[MonthColumn] -= label
        case label: LocalDate => labels.asInstanceOf[DateColumn] -= label
        case label: Minutes => labels.asInstanceOf[MinuteColumn] -= label
        case label: Seconds => labels.asInstanceOf[SecondColumn] -= label
        case label: LocalTime => labels.asInstanceOf[TimeColumn] -= label
        case label: LocalDateTime => labels.asInstanceOf[DateTimeColumn] -= label
        case label: Timestamp => labels.asInstanceOf[TimestampColumn] -= label
        case label: Symbol => labels.asInstanceOf[SymbolColumn] -= label
        case label: String => labels.asInstanceOf[StringColumn] -= label

      }

      save.col(name, ".d", labels, true)(parted, segmented)

    }

    /** Finds tables within database. */
    object find extends FileTableFind(path) {

      protected val finder = new FileColumnFinder(path)

      /** Checks whether column exists within directory table.
        *
        * @param name of table
        * @param parted partition name
        * @param segmented path of segment
        *
        * @return true if exists, else false
       */
      def col(name: String)
      (implicit parted: String = null, segmented: File = null) =
        finder.find(name)(parted, segmented)

    }

    /** Loads tables within database. */
    object load extends FileTableLoad(path) {

      protected val loader = new FileColumnLoader(path)

      /** Loads columns from directory table.
        *
        * @param name of table
        * @param label label of column
        * @param parted partition name
        * @param segmented path of segment
        *
        * @return [[widebase.db.column.TypedColumn]]
       */
      def col(
        name: String,
        label: String)
        (implicit parted: String = null, segmented: File = null) =
        loader.load(name, label)(parted, segmented)

    }

    /** Map tables within database. */
    object map extends FileTableMap(path) {

      protected val mapper = new FileColumnMapper(path)

      /** Map columns from directory table.
        *
        * @param name of table
        * @param label label of column
        * @param parted partition name
        * @param segmented path of segment
        *
        * @return [[widebase.db.column.TypedColumn]]
       */
      def col(
        name: String,
        label: String)
        (implicit parted: String = null, segmented: File = null) =
        mapper.map(name, label)(parted, segmented)

    }

    /** Saves tables within database. */
    object save extends FileTableSave(path) {

      protected val saver = new FileColumnSaver(path)

      /** Saves columns into directory table.
        *
        * @param name of table
        * @param label label of column
        * @param column self-explanatory
        * @param parted partition name
        * @param segmented path of segment
       */
      def col[A](
        name: String,
        label: String,
        column: TypedColumn[A],
        seamless: Boolean = false)
        (implicit parted: String = null, segmented: File = null) {

        saver.save(name, label, column, seamless)(parted, segmented)

      }
    }
  }
}

