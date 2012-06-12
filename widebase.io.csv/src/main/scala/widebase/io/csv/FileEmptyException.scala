package widebase.io.csv

/** Thrown if file empty.
 *
 * @param filename of empty file
 *
 * @author myst3r10n
 */
case class FileEmptyException(filename: String)
  extends Exception("File empty: " + filename)

