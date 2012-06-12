package widebase.io.csv.filter

import vario.data.Datatype

import widebase.db.column.StringColumn
import widebase.db.table.Table

/** A filter that indexing [[java.lang.String]] based symbols.
 *
 * @author myst3r10n
 */
trait SymbolFilter {

  /** Indexing [[java.lang.String]] based symbol into symbol table if missing.
   *
   * @param symbol table to update
   * @param field of symbol column
   * @param values of CSV line
   *
   * @return filtered values of CSV line
   */
  def apply(symbol: Table, field: Int) = (values: Array[String]) => {

    if(symbol("symbol") == null)
      symbol("symbol") = new StringColumn

    val symbolCol = symbol("symbol").asInstanceOf[StringColumn]

    val indexOf = symbolCol.indexOf(values(field))

    values(field) =
      if(indexOf == -1) {

        symbolCol += values(field)
        (symbolCol.length - 1).toString

      } else
        indexOf.toString

  }
}

