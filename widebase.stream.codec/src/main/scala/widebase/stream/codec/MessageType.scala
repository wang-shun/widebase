package widebase.stream.codec

/** Message types.
 *
 * @author myst3r10n
 */
trait MessageType extends Enumeration {

  type MessageType = Value

  val
    BadMessage,
    DoneMessage,
    ForbiddenMessage,
    LoginFailedMessage,
    LoginGrantedMessage,
    LoginRequiredMessage,
    LoginMessage,
    RemoteShutdownMessage,
    UnauthorizedMessage = Value

}

/** Companion.
 *
 * @author myst3r10n
 */
object MessageType extends MessageType

