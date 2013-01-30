package widebase.stream.codec.rq

/** Table subscription.
 *
 * @param name of table
 * @param selector of filter
 *
 * @author myst3r10n
 */
class SubscribeMessage(val name: String, val selector: String)

