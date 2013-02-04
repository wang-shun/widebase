package widebase.stream.socket.rq

import java.io.IOException
import java.util.concurrent.TimeUnit

import org.jboss.netty.channel.SimpleChannelUpstreamHandler

import scala.collection.mutable.LinkedHashMap

import widebase.db.table.Table

import widebase.stream.codec. {

  DoneMessage,
  ForbiddenMessage,
  LengthDecoder,
  LengthEncoder,
  LoginRequiredMessage,
  UnauthorizedMessage

}

import widebase.stream.codec.rq. {

  BrokerDecoder,
  ConsumerEncoder,
  SubscribeMessage,
  UnparsableMessage,
  UnsubscribeMessage

}

import widebase.stream.handler.rq. { ConsumerHandler, RecordListener }
import widebase.stream.socket. { ClientLike, LoginLike, RemoteShutdownLike }

/** A consumer.
 *
 * @param h host of broker
 * @param p port of broker
 *
 * @author myst3r10n
 */
class Consumer(
  h: String,
  p: Int)
  extends ClientLike
  with LoginLike
  with RemoteShutdownLike {

  host = h
  port = p

  pipeline += "lengthDecoder" -> new LengthDecoder
  pipeline += "brokerDecoder" -> new BrokerDecoder
  pipeline += "lengthEncoder" -> new LengthEncoder
  pipeline += "consumerEncoder" -> new ConsumerEncoder

  /** Listen broker. */
  var listener: RecordListener = _

  override def await = {

    super.await
    this

  }

  override def close = {

    super.close

    if(pipeline.contains("consumer"))
      pipeline -= "consumer"

    this

  }

  override def filter(filter: String) = {

    super.filter(filter)
    this

  }

  override def login(implicit
    username: String = this.username,
    password: String = this.password,
    timeout: Int = 60) = {

    open(host, port)

    super.login(username, password, timeout)

    this

  }

  override def open(implicit
    host: String = this.host,
    port: Int = this.port) = {

    pipeline += "consumer" -> new ConsumerHandler(listener)
    super.open(host, port)
    this

  }

  override def remoteShutdown(implicit timeout: Int = 60) = {

    super.remoteShutdown(timeout)

    this

  }

  /** Subscribe table.
   *
   * @param name of table
   * @param selector of table
   * @param timeout of response
   *
   * @return itself
   */
  def subscribe(
    name: String,
    selector: String = "",
    timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new SubscribeMessage(name, selector)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: DoneMessage =>
      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: UnparsableMessage => throw message
      case message: UnauthorizedMessage => throw message

    }

    this

  }

  /** Unsubscribe table.
   *
   * @param timeout of response
   *
   * @return itself
   */
  def unsubscribe(implicit timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new UnsubscribeMessage).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    this

  }
}

