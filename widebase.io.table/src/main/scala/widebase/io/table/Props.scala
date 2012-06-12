package widebase.io.table

import java.nio.charset.Charset

import vario.filter.ByteOrder

import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { capacity, charset, filter, order }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.io.table. { capacity, charset, filter, level, order }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.io.table. { capacities._, charsets._, filters._, levels._, orders._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  import vario.filter.ByteOrder.ByteOrder
  import vario.filter.StreamFilter.StreamFilter

  /** This package name. */
  override protected val packageName = "widebase.io.table"

  /** Buffer capacity properties.
   *
   * @author myst3r10n
   */
  object capacities {

    /** Used buffer capacity to save tables. */
    var saver: Int = _

    /** Used buffer capacity to save partitioned tabled. */
    var parted: Int = _

    reset

    /** Resets buffer capacity properties. */
    def reset {

      saver = SysProps.getInt(packageName + ".capacities.saver", defaultCapacity)
      parted = SysProps.getInt(packageName + ".capacities.parted", defaultCapacity)

    }
  }

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] to find tables. */
    var finder: Charset = _

    /** Used [[java.nio.charset.Charset]] to load tables. */
    var loader: Charset = _

    /** Used [[java.nio.charset.Charset]] to save tables. */
    var saver: Charset = _

    /** Used [[java.nio.charset.Charset]] to save partitioned tables. */
    var parted: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      finder = SysProps.getCharset(packageName + ".charsets.finder", defaultCharset)
      loader = SysProps.getCharset(packageName + ".charsets.loader", defaultCharset)
      saver = SysProps.getCharset(packageName + ".charsets.saver", defaultCharset)
      parted = SysProps.getCharset(packageName + ".charsets.parted", defaultCharset)

    }
  }

  /** Stream filter properties.
   *
   * @author myst3r10n
   */
  object filters {

    /** Used stream filter to find tables. */
    var finder: StreamFilter = _

    /** Used stream filter to load tables. */
    var loader: StreamFilter = _

    /** Used stream filter to save tables. */
    var saver: StreamFilter = _

    reset

    /** Resets stream filter properties. */
    def reset {

      finder = SysProps.getFilter(packageName + ".filters.saver", defaultFilter)
      loader = SysProps.getFilter(packageName + ".filters.loader", defaultFilter)
      saver = SysProps.getFilter(packageName + ".filters.saver", defaultFilter)

    }
  }

  /** Compression level properties.
   *
   * @author myst3r10n
   */
  object levels {

    /** Used compression level to save tables. */
    var saver: Int = _

    reset

    /** Resets compression level properties. */
    def reset {

      saver = SysProps.getLevel(packageName + ".levels.saver", defaultLevel)

    }
  }

  /** [[vario.filter.ByteOrder]] properties.
   *
   * @author myst3r10n
   */
  object orders {

    /** Used [[vario.filter.ByteOrder]] to find tables. */
    var finder: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to load tables. */
    var loader: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to save tables. */
    var saver: ByteOrder = _

    /** Used [[vario.filter.ByteOrder]] to save partitioned tables. */
    var parted: ByteOrder = _

    reset

    /** Resets [[vario.filter.ByteOrder]] properties. */
    def reset {

      finder = SysProps.getOrder(packageName + ".orders.finder", defaultOrder)
      loader = SysProps.getOrder(packageName + ".orders.loader", defaultOrder)
      saver = SysProps.getOrder(packageName + ".orders.saver", defaultOrder)
      parted = SysProps.getOrder(packageName + ".orders.parted", defaultOrder)

    }
  }
}

