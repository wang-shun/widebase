package widebase.db.column

import java.nio.charset.Charset

import widebase.io.filter.ByteOrder

import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { charset, order }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.db.column. { charset, order }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.db.column. { charsets._, orders._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  import widebase.io.filter.ByteOrder.ByteOrder

  /** This package name. */
  override protected val packageName = "widebase.db.column"

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] for [[scala.Symbol]] columns. */
    var symbols: Charset = _

    /** Used [[java.nio.charset.Charset]] for [[java.lang.String]] columns. */
    var strings: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      symbols = SysProps.getCharset(packageName + ".charsets.symbols", defaultCharset)
      strings = SysProps.getCharset(packageName + ".charsets.strings", defaultCharset)

    }
  }

  /** [[widebase.io.filter.ByteOrder]] properties.
   *
   * @author myst3r10n
   */
  object orders {

    /** Used [[widebase.io.filter.ByteOrder]] for [[scala.Symbol]] columns. */
    var symbols: ByteOrder = _

    /** Used [[widebase.io.filter.ByteOrder]] for [[java.lang.String]] columns. */
    var strings: ByteOrder = _

    reset

    /** Resets [[widebase.io.filter.ByteOrder]] properties. */
    def reset {

      symbols = SysProps.getOrder(packageName + ".orders.symbols", defaultOrder)
      strings = SysProps.getOrder(packageName + ".orders.symbols", defaultOrder)

    }
  }
}

