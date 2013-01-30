package widebase.stream

/** Socket package
 *
 * Example how to create a client:
 *
 * {{{
 * class Client extends ClientLike with LoginLike with RemoteShutdownLike {
 *   pipeline += "lengthDecoder" -> new LengthDecoder
 *   pipeline += "responseDecoder" -> new ResponseDecoder
 *   pipeline += "lengthEncoder" -> new LengthEncoder
 *   pipeline += "requestEncoder" -> new RequestEncoder
 * }
 *
 * val client = new Client
 * try {
 *   val future = client.open.awaitUninterruptibly
 *
 *   if(!future.isSuccess)
 *     throw new IOException(future.getCause)
 * } finally {
 *   client.close
 * }
 * }}}
 *
 * Example how to create a server:
 *
 * {{{
 * class Server extends ServerLike {
 *   pipeline += "lengthDecoder" -> new LengthDecoder
 *   pipeline += "requestDecoder" -> new RequestDecoder
 *   pipeline += "lengthEncoder" -> new LengthEncoder
 *   pipeline += "responseEncoder" -> new ResponseEncoder
 *
 *   override def bind(implicit port: Int = this.port) = {
 *     pipeline += "authHandler" -> new AuthHandler(allChannels, auths)
 *     pipeline += "controlHandler" -> new ControlHandler(allChannels)
 *
 *     super.bind(port)
 *     this
 *   }
 *
 *   override def close = {
 *     super.close
 *
 *     if(pipeline.contains("controlHandler"))
 *       pipeline -= "controlHandler"
 *     if(pipeline.contains("authHandler"))
 *       pipeline -= "authHandler"
 *
 *     this
 *   }
 * }
 *
 * val server = new Server
 * try {
 *   server.bind.await
 * } finally {
 *   server.close
 * }
 * }}}
 *
 * @author myst3r10n
 */
package object socket

