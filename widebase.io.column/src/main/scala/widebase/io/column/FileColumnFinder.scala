package widebase.io.column

/** Wrapper of [[widebase.io.table.FileColumnFind]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnFinder(path: String) {

  /** Finds columns within directory table.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object find extends FileColumnFind(path)

}

