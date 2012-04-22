package widebase.db

/** Thrown if database not found.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
case class DatabaseNotFoundException(path: String) extends Exception(path)

