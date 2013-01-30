package widebase.stream.socket.rq

import java.io.IOException
import java.util.concurrent.TimeUnit

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
  FlushMessage,
  NotifyMessage,
  ProducerEncoder,
  PublishMessage

}

import widebase.stream.socket. { ClientLike, LoginLike, RemoteShutdownLike }

/** A producer.
 *
 * @param h of broker
 * @param p of broker
 *
 * @author myst3r10n
 */
class Producer(
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
  pipeline += "producerEncoder" -> new ProducerEncoder

  override def await = {

    super.await
    this

  }

  override def close = {

    super.close
    this

  }

  override def filter(filter: String) = {

    super.filter(filter)
    this

  }

  /** Flush persistence.
   *
   * @param name of table
   * @param timeout of response
   *
   * @return itself
   */
  def flush(name: String, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new FlushMessage(name)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: DoneMessage =>
      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: UnauthorizedMessage => throw message

    }

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

  /** Notify.
   *
   * @param name of table
   * @param text of notify
   * @param timeout of response
   *
   * @return itself
   */
  def notify(name: String, text: String, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new NotifyMessage(name, text)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: DoneMessage =>
      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: UnauthorizedMessage => throw message

    }

    this

  }

  override def open(implicit
    host: String = this.host,
    port: Int = this.port) = {

    super.open(host, port)
    this

  }

  /** Publish records.
   *
   * @param name of table
   * @param records to publish
   * @param timeout of response
   *
   * @return itself
   */
  def publish(name: String, records: Table, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new PublishMessage(name, records.toBytes())).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: DoneMessage =>
      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: UnauthorizedMessage => throw message

    }

    this

  }

  override def remoteShutdown(implicit timeout: Int = 60) = {

    super.remoteShutdown(timeout)

    this

  }
}

