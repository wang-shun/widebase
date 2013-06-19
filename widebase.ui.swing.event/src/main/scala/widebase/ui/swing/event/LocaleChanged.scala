package widebase.ui.swing.event

import scala.swing.event.Event

/** Locale was changed.
 *
 * @param replaced locale
 *
 * @author myst3r10n
*/
case class LocaleChanged(val replaced: String) extends Event

