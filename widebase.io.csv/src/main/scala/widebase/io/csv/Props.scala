package widebase.io.csv

import java.nio.charset.Charset

import widebase.io.column.PropsLike
import widebase.io.filter.ByteOrder
import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { charset, order, capacity }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.io.csv. { charset, order, capacity }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.io.csv. { charsets._, capacities._, orders._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  import widebase.io.filter.ByteOrder.ByteOrder

  /** This package name. */
  override protected val packageName = "widebase.io.csv"

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] to import CSV files. */
    var to: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      to = SysProps.getCharset(packageName + ".charsets.to", defaultCharset)

    }
  }

  /** Buffer capacity properties.
   *
   * @author myst3r10n
   */
  object capacities {

    /** Used buffer capacity to import CSV files. */
    var to: Int = _

    reset

    /** Resets buffer capacity properties. */
    def reset {

      to = SysProps.getInt(packageName + ".capacities.to", defaultCapacity)

    }
  }

  /** [[widebase.io.filter.ByteOrder]] properties.
   *
   * @author myst3r10n
   */
  object orders {

    /** Used [[widebase.io.filter.ByteOrder]] to import CSV files. */
    var to: ByteOrder = _

    reset

    /** Resets [[widebase.io.filter.ByteOrder]] properties. */
    def reset {

      to = SysProps.getOrder(packageName + ".orders.to", defaultOrder)

    }
  }
}

