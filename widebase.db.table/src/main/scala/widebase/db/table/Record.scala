package widebase.db.table

import widebase.db.column.TypedColumn

/** A record.
 *
 * @author myst3r10n
 */
case class Record(labels: TypedColumn[_], values: Array[Any]) {

  /** Select a value by its label in the record.
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
}

