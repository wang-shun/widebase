package widebase.db.table

/** Thrown if records mismatch.
 *
 * @param records of column
 * @param other records of other
 *
 * @author myst3r10n
 */
case class RecordsMismatchException(records: Int, other: Int)
  extends Exception(records + " != " + other)

