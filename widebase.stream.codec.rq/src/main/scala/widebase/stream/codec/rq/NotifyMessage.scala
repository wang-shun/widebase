package widebase.stream.codec.rq

/** Notify.
 *
 * @param name of table
 * @param text of notify
 *
 * @author myst3r10n
 */
class NotifyMessage(val name: String, val text: String) extends Exception

