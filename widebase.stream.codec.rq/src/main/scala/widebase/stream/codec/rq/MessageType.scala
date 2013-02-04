package widebase.stream.codec.rq

/** Message types.
 *
 * @author myst3r10n
 */
trait MessageType extends widebase.stream.codec.MessageType {

  val
    EventMessage,
    FlushMessage,
    NotifyMessage,
    PublishMessage,
    RollbackMessage,
    SubscribeMessage,
    TableMessage,
    UnparsableMessage,
    UnsubscribeMessage = Value

}

/** Companion.
 *
 * @author myst3r10n
 */
object MessageType extends MessageType

