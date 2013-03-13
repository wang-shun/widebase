package widebase.ui.ide.event

import scala.swing.event.Event

/** Rename tab has been requested.
 *
 * @author myst3r10n
*/
case class RenameTab(val name: String) extends Event

