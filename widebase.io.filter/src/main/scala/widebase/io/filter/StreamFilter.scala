package widebase.io.filter

/** Supported stream filters.
 *
 * @author myst3r10n
 */
object StreamFilter extends Enumeration {

  type StreamFilter = Value

  val None, Gzip, Zlib = Value

}

