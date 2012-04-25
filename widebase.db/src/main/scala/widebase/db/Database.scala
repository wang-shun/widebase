package widebase.db

import java.io.File

import widebase.db.column.TypedColumn

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

  object tables extends FileRecordEditor(path) {

    /** Finds tables within database. */
    object find extends FileTableFind(path) {

      protected val finder = new FileColumnFinder(path)

      /** Checks whether column exists within directory table.
        *
        * @note Supports optional partitioned tables.
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

