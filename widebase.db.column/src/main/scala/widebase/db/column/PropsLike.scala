package widebase.db.column

import java.nio.charset.Charset

import vario.filter.ByteOrder

import widebase.util.SysProps

/** A common trait for properties.
 *
 * @author myst3r10n
 */
trait PropsLike {

  import vario.filter.ByteOrder.ByteOrder

  /** Package name. */
  protected val packageName: String

  protected val defaultCharset = SysProps.getCharset(
    packageName + ".charset",
    SysProps.getCharset("widebase.io.charset", Charset.forName("UTF-8")))

  protected val defaultOrder = SysProps.getOrder(
    packageName + ".order",
    SysProps.getOrder("widebase.io.order", ByteOrder.Native))

}

