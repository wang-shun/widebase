package widebase.io.column

/** Wrapper of [[widebase.io.table.FileColumnMap]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnMapper(path: String) {

  /** Map columns from directory table.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object map extends FileColumnMap(path)

}

