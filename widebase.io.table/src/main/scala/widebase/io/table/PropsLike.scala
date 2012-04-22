package widebase.io.table

import vario.filter.StreamFilter

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

}

