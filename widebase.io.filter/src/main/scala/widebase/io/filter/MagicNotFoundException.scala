package widebase.io.filter

/** Thrown if magic not found.
 *
 * @param path of file
 *
 * @author myst3r10n
 */
case class MagicNotFoundException(path: String) extends Exception(path)

