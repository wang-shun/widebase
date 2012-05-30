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

import widebase.db.table.Table

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

  /** Implicitly converts a segment key into segment path.
   *
   * @param key to convert
   *
   * @return a conversion purposed object
   */
  implicit def asSegmentPath(key: String) = new SegmentConversion(key, segment)

  object tables extends FileRecordEditor(path) {

    /** Routines that handle find within database. */
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

    /** Routines that handle load from database. */
    object load extends FileTableLoad(path) {

      protected val loader = new FileColumnLoader(path)

      /** Load column from directory table.
        *
        * @param name of table
        * @param label label of column
        * @param indexable column
        * @param amount values to load, 0 load all
        * @param parted partition name
        * @param segmented path of segment
        *
        * @return [[widebase.db.column.TypedColumn]]
       */
      def col(
        name: String,
        label: String,
        indexable: Boolean = false,
        amount: Int = 0)
        (implicit parted: String = null, segmented: File = null) =
        loader.load(name, label, indexable, amount)(parted, segmented)

    }

    /** Routines that handle map from database. */
    object map extends FileTableMap(path) {

      protected val mapper = new FileColumnMapper(path)

      /** Map column from directory table.
        *
        * @param name of table
        * @param label label of column
        * @param amount values to load, 0 load all
        * @param parted partition name
        * @param segmented path of segment
        *
        * @return [[widebase.db.column.TypedColumn]]
       */
      def col(
        name: String,
        label: String,
        amount: Int = 0)
        (implicit parted: String = null, segmented: File = null) =
        mapper.map(name, label, amount)(parted, segmented)

    }

    /** Routines that handle save into database. */
    object save extends FileTableSave(path) {

      protected val saver = new FileColumnSaver(path)

      /** Save column into directory table.
        *
        * @param name of table
        * @param label of column
        * @param column itself
        * @param indexable column
        * @param parted partition name
        * @param segmented path of segment
       */
      def col[A](
        name: String,
        label: String,
        column: TypedColumn[A],
        indexable: Boolean = false)
        (implicit parted: String = null, segmented: File = null) {

        saver.save(name, label, column, indexable)(parted, segmented)

      }
    }

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

      save.col(name, label.toString, column, true)(parted, segmented)

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

      save.col(name, ".d", labels)(parted, segmented)

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

      save.col(name, ".d", labels)(parted, segmented)

    }
  }
}

