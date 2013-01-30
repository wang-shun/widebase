package widebase.stream.handler.cq

import scala.collection.mutable.LinkedHashMap

import widebase.db.table.Table

/** Stores tables.
 *
 * Format:
 * {{{
 * table name / series of table
 * }}}
 *
 * @author myst3r10n
 */
class TableMap extends LinkedHashMap[String, Table] {

  /** A serie of names. */
  def names = keys

  /** A serie of tables. */
  def tables = values

}

