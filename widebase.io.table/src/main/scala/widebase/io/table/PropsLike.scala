package widebase.io.table

import widebase.io.filter. { CompressionLevel, StreamFilter }
import widebase.util.SysProps

/** A common trait for properties.
 *
 * @author myst3r10n
 */
trait PropsLike extends widebase.io.column.PropsLike {

  /** Default filter. */
  protected val defaultFilter = SysProps.getFilter(
    packageName + ".filter",
    SysProps.getFilter("widebase.io.filter", StreamFilter.None))

  /** Default level. */
  protected val defaultLevel = SysProps.getLevel(
    packageName + ".level",
    SysProps.getLevel("widebase.io.level", CompressionLevel.Default))

}

