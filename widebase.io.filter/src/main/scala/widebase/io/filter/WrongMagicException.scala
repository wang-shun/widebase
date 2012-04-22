package widebase.io.filter

/** Thrown if wrong magic.
 *
 * @param path of file
 * @param magic value
 *
 * @author myst3r10n
 */
case class WrongMagicException(
  path: String,
  magic: String)
  extends Exception(magic + " in " + path)

