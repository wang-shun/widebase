package widebase.io.column

/** Wrapper of [[widebase.io.table.FileColumnSave]].
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnSaver(path: String) {

  /** Saves columns into directory table.
   *
   * @param path of database
   *
   * @author myst3r10n
   */
  object save extends FileColumnSave(path)

}

