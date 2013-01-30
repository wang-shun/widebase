package widebase

import java.io.File

/** Dsl package.
 *
 * @author myst3r10n
 */
package object dsl {

  /** A collection of implicit conversions. */
  val conversion = Conversion

  /** A collection of convenience purposed types. */
  val datatype = Datatype

  /** A collection of convenience purposed functions. */
  val function = new Function

}

