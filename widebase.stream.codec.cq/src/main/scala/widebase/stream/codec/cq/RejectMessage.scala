package widebase.stream.codec.cq

/** Query rejected.
 *
 * @param reason of reject
 *
 * @author myst3r10n
 */
class RejectMessage(val reason: String) extends Exception

