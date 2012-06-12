package widebase.io.csv

import vario.data.Datatype.Datatype

/** Thrown if types mismatch.
 *
 * @param typeOf this
 * @param value of other
 *
 * @author myst3r10n
 */
case class TypeMismatchException(typeOf: Datatype, value: String)
  extends Exception(typeOf + " mismatch with " + value)

