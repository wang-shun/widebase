package widebase.io

import widebase.data.Datatype

/** A common trait for toggle mode implementations.
 *
 * @author myst3r10n
 */
trait ToggleModeLike {

  import widebase.data.Datatype.Datatype

  protected var _mode = Datatype.Byte

  /** Current buffer mode. */
  def mode = _mode

  /** Toggle current buffer mode.
   *
   * @param replace mode
   */
  def mode_=(replace: Datatype) {

    reposition
    review(replace)

    _mode = replace

  }

  /** Reposition byte buffer. */
  protected def reposition

  /** Reviews datatype buffers.
   *
   * @param replace mode
   */
  protected def review(replace: Datatype)

}

