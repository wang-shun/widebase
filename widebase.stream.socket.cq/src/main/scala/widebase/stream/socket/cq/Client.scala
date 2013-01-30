package widebase.stream.socket.cq

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

import widebase.stream.codec.cq. {

  FindMessage,
  LoadMessage,
  QueryMessage,
  RejectMessage,
  RequestEncoder,
  ResponseDecoder,
  SaveMessage,
  TableFoundMessage,
  TableMessage,
  TableNotFoundMessage

}

import widebase.stream.socket. { ClientLike, LoginLike, RemoteShutdownLike }

/** A client.
 *
 * @param h host of server
 * @param p port of server
 *
 * @author myst3r10n
 */
class Client(
  h: String,
  p: Int)
  extends ClientLike
  with LoginLike
  with RemoteShutdownLike {

  host = h
  port = p

  pipeline += "lengthDecoder" -> new LengthDecoder
  pipeline += "responseDecoder" -> new ResponseDecoder
  pipeline += "lengthEncoder" -> new LengthEncoder
  pipeline += "requestEncoder" -> new RequestEncoder

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

  /** Find table within remote cached database.
    *
    * @param name of table
    * @param timeout of response
    *
    * @return true if found, else false
   */
  def find(name: String, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new FindMessage(name)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: ForbiddenMessage => throw new ForbiddenMessage
      case message: LoginRequiredMessage => throw new LoginRequiredMessage
      case message: TableFoundMessage => true
      case message: TableNotFoundMessage => false
      case message: UnauthorizedMessage => throw new UnauthorizedMessage

    }
  }

  /** Load table from remote cached database.
    *
    * @param name of table
    * @param timeout of response
    *
    * @return table
   */
  def load(name: String, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new LoadMessage(name)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: TableMessage => message.table
      case message: TableNotFoundMessage => throw message
      case message: UnauthorizedMessage => throw message

    }
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

    super.open(host, port)
    this

  }

  /** Query remote cached database.
    *
    * @param statement of query
    * @param timeout of response
    *
    * @return table
   */
  def query(statement: String, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new QueryMessage(statement)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: ForbiddenMessage => throw message
      case message: LoginRequiredMessage => throw message
      case message: RejectMessage => throw message
      case message: TableMessage => message.table
      case message: TableNotFoundMessage => throw message
      case message: UnauthorizedMessage => throw message

    }
  }

  override def remoteShutdown(implicit timeout: Int = 60) = {

    super.remoteShutdown(timeout)

    this

  }

  /** Save table into remote cached database.
    *
    * @param name of table
    * @param table self-explanatory
    * @param timeout of response
    *
    * @return itself
   */
  def save(name: String, table: Table, timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new SaveMessage(name, table)).awaitUninterruptibly

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
}

