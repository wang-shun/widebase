package widebase.io.csv

/** Predefined filters to processing CSV files.
 *
 * @author myst3r10n
 */
package object filter {

  /** Reference to itself. */
  val ref = this

  /** A filter that doing nothing.
   *
   * @return untouched values of CSV line
   */
  def none = (values: Array[String]) => values

  /** A filter that indexing [[java.lang.String]] based symbols. */
  object symbol extends SymbolFilter

}

