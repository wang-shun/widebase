package widebase.stream.codec.cq

/** Message types.
 *
 * @author myst3r10n
 */
trait MessageType extends widebase.stream.codec.MessageType {

  val
    FindMessage,
    LoadMessage,
    QueryMessage,
    RejectMessage,
    SaveMessage,
    TableMessage,
    TableFoundMessage,
    TableNotFoundMessage = Value

}

/** Companion.
 *
 * @author myst3r10n
 */
object MessageType extends MessageType

