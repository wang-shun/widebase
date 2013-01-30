package widebase.io

import widebase.data.Datatype.Datatype

/** Thrown if types mismatch
 *
 * @param currentType the current type
 * @param mismatchValue value that not match with origin type
 *
 * @author myst3r10n
 */
case class TypeMismatchException(currentType: Datatype, mismatchValue: String)
  extends Exception(currentType + " mismatch with value: " + mismatchValue)

