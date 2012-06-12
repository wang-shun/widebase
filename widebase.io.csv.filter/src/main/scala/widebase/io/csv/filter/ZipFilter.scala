package widebase.io.csv.filter

/** Compression filters.
 *
 * @author myst3r10n
 */
object ZipFilter extends Enumeration {

  type ZipFilter = Value

  val None, Gzip, Zlib = Value

}

