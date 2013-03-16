package widebase.ui.ide.editor.event

import scala.swing.event.Event

/** Edit selection has been requested.
 *
 * @author myst3r10n
*/
case class EditSelection(val index: Int) extends Event

