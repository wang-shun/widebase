package widebase.db

/** Table package.
 *
 * @author myst3r10n
 */
package object table {

  /** Global properties. */
  def props = Props

  /** Sort a table.
    *
    * @param table to sort
    * @param label of column
    * @param method of sort
    * @param direction of sort
   */
  def sort(table: Table, label: Any, method: Symbol, direction: Symbol = 'a) {

    val sortDirection = direction match {

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
        method == 'insertion => Sort.insertion(table, label, sortDirection)
      case method if
        method == 's ||
        method == 'select ||
        method == 'selection => Sort.selection(table, label, sortDirection)

    }
  }
}

