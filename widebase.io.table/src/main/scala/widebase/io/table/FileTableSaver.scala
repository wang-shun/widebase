package widebase.io.table

/** Wrapper of [[widebase.io.table.FileTableSave]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileTableSaver(path: String) {

  /** Saves tables into database.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object save extends FileTableSave(path)

}

