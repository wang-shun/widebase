package widebase.db.table

/** A common trait for ORM tables.
 *
 * @author myst3r10n
 */
trait TemplateTable[R] {

  /** Appends record into table.
   *
   * @param record to append
   *
   * @return the table itself
   */
  def +=(record: R): TemplateTable[R]

  /** Appends records of another table to table.
   *
   * @param table the table to append
   *
   * @return the table itself
   */
  def ++=(table: TemplateTable[R]) = {

    for(r <- 0 to table.records.length - 1)
      this += table(r)

    this

  }

  /** Record.
   *
   * @param index of record
   *
   * @return ohlc
   */
  def apply(index: Int): R

  def foreach[U](f: R =>  U) =
    for(r <- 0 to records.length - 1)
      f(apply(r))

  /** Head record. */
  def head = apply(0)

  /** Last record. */
  def last = apply(records.length - 1)

  /** Underlying table. */
  def peer: Table

  /** Records. */
  def records = peer.records

}

