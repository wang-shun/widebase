package widebase.ui.workspace

import moreswing.swing.i18n.LAction

/* An [scala.swing.Action] with i18n support.
 *
 * @author myst3r10n
*/
abstract class Action(title0: String) extends scala.swing.Action(title0) with LAction

