package widebase

/** Widebase's data package.
 *
 * Supported tokenized datatypes for convenience purposes:
 *{{{
 * 'b // widebase.data.Bool
 * 'x // widebase.data.Byte
 * 'c // widebase.data.Char
 * 'd // widebase.data.Double
 * 'f // widebase.data.Float
 * 'i // widebase.data.Int
 * 'l // widebase.data.Long
 * 's // widebase.data.Short
 *}}}
 * Supported tokenized Joda types for convenience purposes:
 *{{{
 * 'M // widebase.data.Month
 * 'D // widebase.data.Date
 * 'U // widebase.data.Minute
 * 'V // widebase.data.Second
 * 'T // widebase.data.Time
 * 'Z // widebase.data.DateTime
 * 'P // widebase.data.Timestamp
 *}}}
 * Supported tokenized variable types for convenience purposes:
 *{{{
 * 'Y // widebase.data.Symbol
 * 'S // widebase.data.String
 *}}}
 *
 * @author myst3r10n
 */
package object data {

  import Datatype.Datatype

  /** Convert a tokenized type
   *
   * @param token tokenized type
   *
   * @return resolved [[widebase.data.Datatype]]
  */
  final def by(token: Symbol): Datatype =
    token match {

      case 'n => Datatype.None
      case 'b => Datatype.Bool
      case 'x => Datatype.Byte
      case 'c => Datatype.Char
      case 'd => Datatype.Double
      case 'f => Datatype.Float
      case 'i => Datatype.Int
      case 'l => Datatype.Long
      case 's => Datatype.Short

      case 'M => Datatype.Month
      case 'D => Datatype.Date
      case 'U => Datatype.Minute
      case 'V => Datatype.Second
      case 'T => Datatype.Time
      case 'Z => Datatype.DateTime
      case 'P => Datatype.Timestamp

      case 'Y => Datatype.Symbol
      case 'S => Datatype.String

    }

  /** Convert a series of tokenized types
   *
   * @param tokens tokenized types
   *
   * @return array of resolved [[widebase.data.Datatype]]s
  */
  final def by(tokens: String): Array[Datatype] =
    for(token <- tokens.toCharArray)
      yield(by(Symbol(token.toString)))

  /** Size of each supported [[widebase.data.Datatype]]s in bytes. */
  final def sizeOf = SizeOf

}

