package widebase.stream.codec.rq

import java.nio.charset.Charset

import widebase.util.SysProps

/** A common trait for properties.
 *
 * @author myst3r10n
 */
trait PropsLike {

  /** Package name. */
  protected val packageName: String

  protected val defaultCharset = SysProps.getCharset(
    packageName + ".charset",
    SysProps.getCharset("widebase.io.charset", Charset.forName("UTF-8")))

}

