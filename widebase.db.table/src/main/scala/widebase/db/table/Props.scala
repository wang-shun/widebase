package widebase.db.table

import java.nio.charset.Charset

import widebase.db.column.PropsLike
import widebase.io.filter.ByteOrder
import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { capacity, charset }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.db.table. { charset, order }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.db.table. { charsets._, orders._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  import widebase.io.filter.ByteOrder.ByteOrder

  /** This package name. */
  override protected val packageName = "widebase.db.table"

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] to convert bytes into table. */
    var from: Charset = _

    /** Used [[java.nio.charset.Charset]] to convert table into bytes. */
    var to: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      from = SysProps.getCharset(packageName + ".charsets.from", defaultCharset)
      to = SysProps.getCharset(packageName + ".charsets.to", defaultCharset)

    }
  }

  /** [[widebase.io.filter.ByteOrder]] properties.
   *
   * @author myst3r10n
   */
  object orders {

    /** Used [[widebase.io.filter.ByteOrder]] to convert bytes into table. */
    var from: ByteOrder = _

    /** Used [[widebase.io.filter.ByteOrder]] to convert table into bytes. */
    var to: ByteOrder = _

    reset

    /** Resets [[widebase.io.filter.ByteOrder]] properties. */
    def reset {

      from = SysProps.getOrder(packageName + ".orders.from", defaultOrder)
      to = SysProps.getOrder(packageName + ".orders.to", defaultOrder)

    }
  }
}

