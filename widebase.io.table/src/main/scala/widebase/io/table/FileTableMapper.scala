package widebase.io.table

/** Wrapper of [[widebase.io.table.FileTableMap]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileTableMapper(path: String) {

  /** Map directory tables from database.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object map extends FileTableMap(path)

}

