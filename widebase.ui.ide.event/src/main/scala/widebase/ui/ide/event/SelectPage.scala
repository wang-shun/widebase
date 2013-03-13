package widebase.ui.ide.event

import scala.swing.event.Event

/** Select page has been requested.
 *
 * @author myst3r10n
*/
case class SelectPage(val number: Int) extends Event

