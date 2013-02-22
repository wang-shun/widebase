package widebase

/** Testkit package.
 *
 * @author myst3r10n
 */
package object testkit {

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

}

