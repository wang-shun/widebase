package widebase.dsl

import java.io.File

/** A collection of convenience purposed functions.
 *
 * @author myst3r10n
 */
class Function {

  /** A reference to [[widebase.io.csv]] */
  val csv = widebase.io.csv.ref

  /** A reference to [[widebase.io.csv.filter]] */
  val filter = widebase.io.csv.filter.ref

  /** Creates the directory named by this abstract pathname.
   *
   * @param path self-explained
   *
   */
  def mkdir(path: String) { new File(path).mkdir }

}

