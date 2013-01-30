package widebase.stream.socket

import widebase.io.filter. { CompressionLevel, StreamFilter }
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener

/** Socket RQ package
 *
 * Example how to create a broker:
 *
 * {{{
 * val broker = rq.broker
 * broker.auths("auths.properties")
 * try {
 *   broker.bind.await
 * } finally {
 *   broker.close
 * }
 * }}}
 *
 * Example how to create a consumer:
 *
 * {{{
 * object listener extends RecordListener {
 *  def react = {
 *    case event: String => println(text)
 *    case (records: Int, partition: String) =>
 *    case chunk: Table =>
 *  }
 * }
 *
 * val consumer = rq.consumer(listener)
 * try {
 *   consumer.login("consumer", "password").subscribe("table")
 * } finally {
 *   consumer.close
 * }
 * }}}
 *
 * Example how to create a producer:
 *
 * {{{
 * val producer = rq.producer
 * try {
 *   producer.login("producer", "password").notify("table", "Hello World!")
 * } finally {
 *   producer.close
 * }
 * }}}
 *
 * @author myst3r10n
 */
package object rq {

  import widebase.io.filter.StreamFilter.StreamFilter

  /** Create [[widebase.stream.socket.rq.Broker]] instance.
   *
   * @param path of synchronization database
   * @param auths authorization map, default disabled
   * @param interval of publishing, default zero-latency
   * @param filter0 of socket I/O
   *
   * @return instance of [[widebase.stream.socket.rq.Broker]]
   */
  def broker(implicit 
    path: String = null,
    auths: AuthMap = null,
    interval: Int = 0,
    filter0: String = "") =
    new Broker(path, auths, interval) {

      filter(filter0)

    }

  /** Create [[widebase.stream.socket.rq.Consumer]] instance.
   *
   * @param listener0 of broker
   * @param host of broker, default `localhost`
   * @param port of broker, default `60000`
   * @param filter0 of socket I/O
   *
   * @return instance of [[widebase.stream.socket.rq.Consumer]]
   */
  def consumer(implicit
    listener0: RecordListener = null,
    host: String = "localhost",
    port: Int = 60000,
    filter0: String = "") =
    new Consumer(host, port) {

      filter(filter0)
      listener = listener0

    }

  /** Create [[widebase.stream.socket.rq.Producer]] instance.
   *
   * @param host of broker, default `localhost`
   * @param port of broker, default `60000`
   * @param filter0 of socket I/O
   *
   * @return instance of [[widebase.stream.socket.rq.Producer]]
   */
  def producer(implicit
    host: String = "localhost",
    port: Int = 60000,
    filter0: String = "") =
    new Producer(host, port) {

      filter(filter0)

    }
}

