package widebase.db.table

import widebase.db.column.TypedColumn

/** A label accessible record.
 *
 * @param index of record
 * @param labels of record
 * @param values of record
 *
 * @author myst3r10n
 */
class Record(val index: Int, labels: TypedColumn[_], values: Array[Any]) {

  /** Select a value by its label.
   *
   * @param label where values are selected
   *
   * @return value by label
   */
  def apply(label: Any) = {

    val index = labels.indexOf(label)

    if(index == -1)
      throw new ValueNotFound

    values(labels.indexOf(label))

  }

  override def toString = {

    var printable = ""
    val lineSeparator = System.getProperty("line.separator")

    for(i <- 0 to values.size - 1) {

      printable += values(i)

      if(i + 1 < values.size)
        printable += ", "

    }

    printable

  }
}

