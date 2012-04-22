package widebase.db.table

/** Thrown if length mismatch.
 *
 * @param length of record
 * @param other length of other
 *
 * @author myst3r10n
 */
case class LengthMismatchException(length: Int, other: Int)
  extends Exception(length + " != " + other)

