package widebase.io

import java.nio.charset.Charset

/** A common trait for [[widebase.data.Symbol]] mode and [[widebase.data.String]] mode.
 *
 * @author myst3r10n
 */
trait CharsetLike {

  /** Overwritable charset for [[scala.Symbol]] and [[java.lang.String]], default is `UTF-8`. */
  val charset = Charset.forName("UTF-8")

}

