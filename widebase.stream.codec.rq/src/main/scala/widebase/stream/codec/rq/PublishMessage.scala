package widebase.stream.codec.rq

/** Publish records.
 *
 * @param name of table
 * @param bytes of table
 *
 * @author myst3r10n
 */
class PublishMessage(val name: String, val bytes: Array[Byte])

