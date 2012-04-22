package widebase.io.filter

/** Thrown if magic invalid.
 *
 * @param path of file
 * @param magic value
 *
 * @author myst3r10n
 */
case class InvalidMagicException(
  path: String,
  magic: String)
  extends Exception(magic + " in " + path)

