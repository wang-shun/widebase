package widebase.db.table

/** A common trait for ORM tables.
 *
 * You need first a record:
 *
 * {{{
 * case class Log(val time: LocalDateTime, val user: String)
 * }}}
 *
 * Second create a ORM table:
 *
 * {{{
 * case class LogTable(
 *   table: Table = Table(string("time", "user"), dateTime(), string()))
 *   extends TemplateTable[Log] {
 *
 *   val time = table("time").Z
 *   val user = table("user").S
 *
 *   def +=(log: Log) = { time += log.time; user += log.user; this }
 *
 *   def +=(time: LocalDateTime, user: String): LogTable =
 *     this += Log(time, user)
 *
 *   def ++=(table: LogTable) = {
 *     for(r <- 0 to table.records.length - 1)
 *       this += table(r)
 *     this
 *   }
 *
 *   def apply(index: Int) = Log(time(index), user(index))
 *
 *   def filter(predicate: Log => Boolean) = {
 *     val filteredTable = new LogTable
 *     for(r <- 0 to records.length - 1)
 *       if(predicate(this(r)))
 *         filteredTable += this(r)
 *     filteredTable
 *   }
 *
 *   def peer = table
 * }
 * }}}
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

  /** Filters all elements of this table which satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @note Is faster than `filter` by widebase.db.table.Table
   *
   * @return filtered table
   */
  def filter(predicate: R => Boolean): TemplateTable[R]

  /** Filters all elements of this table which do not satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered table
   */
  def filterNot(predicate: R => Boolean) = filter(!predicate(_))

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

