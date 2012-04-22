package widebase.io.table

/** Wrapper of [[widebase.io.table.FileTableLoad]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileTableLoader(path: String) {

  /** Loads tables from database.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object load extends FileTableLoad(path)

}

