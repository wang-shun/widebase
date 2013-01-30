package widebase.stream.codec.cq

import java.nio.charset.Charset

import widebase.util.SysProps

/** Scope properties.
 *
 * Set directly or by system properties.
 *
 * Global property keys:
 * {{{
 * widebase.io. { filter }
 * }}}
 *
 * Package property keys:
 * {{{
 * widebase.stream.codec.ct. { charset }
 * }}}
 *
 * Individual property keys:
 * {{{
 * widebase.stream.codec.ct. { charsets._ }
 * }}}
 *
 * @author myst3r10n
 */
object Props extends PropsLike {

  /** This package name. */
  override protected val packageName = "widebase.steram.codec.ct"

  /** [[java.nio.charset.Charset]] properties.
   *
   * @author myst3r10n
   */
  object charsets {

    /** Used [[java.nio.charset.Charset]] to decode data. */
    var decoder: Charset = _

    /** Used [[java.nio.charset.Charset]] to encode data. */
    var encoder: Charset = _

    reset

    /** Resets [[java.nio.charset.Charset]] properties. */
    def reset {

      decoder = SysProps.getCharset(packageName + ".charsets.decoder", defaultCharset)
      encoder = SysProps.getCharset(packageName + ".charsets.encoder", defaultCharset)

    }
  }
}

