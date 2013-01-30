package widebase.stream.socket

import vario.filter. { CompressionLevel, StreamFilter }

import widebase.stream.handler.AuthMap

/** Socket CQ package
 *
 * Example how to create a client:
 *
 * {{{
 * val client = cq.client
 * try {
 *   client.login("client", "password")
 * } finally {
 *   client.close
 * }
 * }}}
 *
 * Example how to create a server:
 *
 * {{{
 * val server = cq.server
 * server.auths("auths.properties")
 * try {
 *   server.bind.await
 * } finally {
 *   server.close
 * }
 * }}}
 *
 * @author myst3r10n
 */
package object cq {

  import vario.filter.StreamFilter.StreamFilter

  import widebase.stream.socket

  /** Create [[widebase.stream.socket.cq.Client]] instance.
   *
   * @param host of server, default `localhost`
   * @param port of server, default `50000`
   * @param filter0 of socket I/O
   *
   * @return instance of [[widebase.stream.socket.cq.Client]]
   */
  def client(implicit
    host: String = "localhost",
    port: Int = 50000,
    filter0: String = "") =
    new Client(host, port) {

      filter(filter0)

    }

  /** Create [[widebase.stream.socket.cq.Server]] instance.
   *
   * @param auths authorization map, default disabled
   * @param filter0 of socket I/O
   *
   * @return instance of [[widebase.stream.socket.cq.Server]]
   */
  def server(implicit auths: AuthMap = null, filter0: String = "") =
    new Server(auths) {

      filter(filter0)

    }
}

