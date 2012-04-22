package widebase.io.table

/** Wrapper of [[widebase.io.table.FileTableFind]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileTableFinder(path: String) {

  /** Finds tables within database.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object find extends FileTableFind(path)

}

