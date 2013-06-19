package widebase.ui.swing

import moreswing.swing.i18n.LMenu

/* A menu with i18n support and id/menu map.
 *
 * @author myst3r10n
 */
class Menu(title0: String) extends scala.swing.Menu(title0) with LMenu with MenuLike

