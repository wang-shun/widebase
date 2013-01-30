package widebase.db.column

import widebase.data.Datatype.Datatype

/** Thrown if types mixed.
 *
 * @param typeOf this
 * @param valueType type of other
 *
 * @author myst3r10n
 */
case class MixedTypeException(typeOf: Datatype, valueType: Datatype)
  extends Exception(typeOf + " not mixable with " + valueType)

