package widebase.io.column

/** Wrapper of [[widebase.io.table.FileTableLoad]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnLoader(path: String) {

  /** Loads columns from directory table.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object load extends FileColumnLoad(path)

}

