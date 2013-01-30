package widebase.stream.codec.rq

/** Rollback.
 *
 * @param records pending
 * @param partition of table
 *
 * @author myst3r10n
 */
class RollbackMessage(val records: Int, val partition: String)

