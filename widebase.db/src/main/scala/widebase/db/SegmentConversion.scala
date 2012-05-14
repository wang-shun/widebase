package widebase.db

/** A collection of implicit conversions to convert segment key into segment path.
 *
 * @author myst3r10n
 */
class SegmentConversion(key: String, segment: SegmentMap) {

  /** Untouched. */
  def apply() = key

  /** Converted segment path. */
  def S = segment(key)

}

