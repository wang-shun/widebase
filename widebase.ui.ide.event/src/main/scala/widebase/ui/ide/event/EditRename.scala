package widebase.ui.ide.event

import scala.swing.event.Event

/** Edit rename has been requested.
 *
 * @author myst3r10n
*/
case class EditRename(val replace: String) extends Event

