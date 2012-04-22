package widebase.io.column

import widebase.util.SysProps

/** A common trait for properties.
 *
 * @author myst3r10n
 */
trait PropsLike extends widebase.db.column.PropsLike {

  /** Default capacity. */
  protected val defaultCapacity = SysProps.getInt(
    packageName + ".capacity",
    SysProps.getInt("widebase.io.capacity", 4096))

}

