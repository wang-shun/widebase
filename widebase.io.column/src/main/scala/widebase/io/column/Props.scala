package widebase.io.column

import java.nio.charset.Charset

import vario.filter.ByteOrder

import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { capacity, charset, order }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.io.column. { capacity, charset, order }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.io.column. { capacities._, charsets._, orders._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  import vario.filter.ByteOrder.ByteOrder

  /** This package name. */
  override protected val packageName = "widebase.io.column"

  /** Buffer capacity properties.
   *
   * @author myst3r10n
   */
  object capacities {

    /** Used buffer capacity to load columns. */
    var loader: Int = _

    /** Used buffer capacity to map columns. */
    var mapper: Int = _

    /** Used buffer capacity to save columns. */
    var saver: Int = _

    /** Used buffer capacity to set values on columns. */
    var set: Int = _

    /** Used buffer capacity to upsert records into columns. */
    var append: Int = _

    reset

    /** Resets buffer capacity properties. */
    def reset {

      loader = SysProps.getInt(packageName + ".capacities.loader", defaultCapacity)
      mapper = SysProps.getInt(packageName + ".capacities.mapper", defaultCapacity)
      saver = SysProps.getInt(packageName + ".capacities.saver", defaultCapacity)
      set = SysProps.getInt(packageName + ".capacities.set", defaultCapacity)
      append = SysProps.getInt(packageName + ".capacities.append", defaultCapacity)

    }
  }

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] to find columns. */
    var finder: Charset = _

    /** Used [[java.nio.charset.Charset]] to load columns. */
    var loader: Charset = _

    /** Used [[java.nio.charset.Charset]] to map columns. */
    var mapper: Charset = _

    /** Used [[java.nio.charset.Charset]] to save columns. */
    var saver: Charset = _

    /** Used [[java.nio.charset.Charset]] to set values on columns. */
    var set: Charset = _

    /** Used [[java.nio.charset.Charset]] to upsert records into columns. */
    var append: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      finder = SysProps.getCharset(packageName + ".charsets.finder", defaultCharset)
      loader = SysProps.getCharset(packageName + ".charsets.loader", defaultCharset)
      mapper = SysProps.getCharset(packageName + ".charsets.mapper", defaultCharset)
      saver = SysProps.getCharset(packageName + ".charsets.saver", defaultCharset)
      set = SysProps.getCharset(packageName + ".charsets.set", defaultCharset)
      append = SysProps.getCharset(packageName + ".charsets.append", defaultCharset)

    }
  }

  /** [[vario.filter.ByteOrder]] properties.
   *
   * @author myst3r10n
   */
  object orders {

    /** Used [[vario.filter.ByteOrder]] to find columns. */
    var finder: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to load columns. */
    var loader: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to map columns. */
    var mapper: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to save columns. */
    var saver: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to set values on columns. */
    var set: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to upsert records into columns. */
    var append: ByteOrder = _

    reset

    /** Resets [[vario.filter.ByteOrder]] properties. */
    def reset {

      finder = SysProps.getOrder(packageName + ".orders.finder", defaultOrder)
      loader = SysProps.getOrder(packageName + ".orders.loader", defaultOrder)
      mapper = SysProps.getOrder(packageName + ".orders.mapper", defaultOrder)
      saver = SysProps.getOrder(packageName + ".orders.saver", defaultOrder)
      set = SysProps.getOrder(packageName + ".orders.set", defaultOrder)
      append = SysProps.getOrder(packageName + ".orders.append", defaultOrder)

    }
  }
}

