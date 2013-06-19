package widebase.ui.swing.event

import javax.swing.UIManager.LookAndFeelInfo

import scala.swing.event.Event

/** Look and feel was changed.
 *
 * @param replaced look and feel
 *
 * @author myst3r10n
*/
case class LookAndFeelChanged(val replaced: LookAndFeelInfo) extends Event

