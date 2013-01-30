package widebase.stream.socket

import java.io.IOException
import java.util.concurrent.TimeUnit

import org.jboss.netty.channel.ChannelFuture
import org.jboss.netty.handler.queue.BlockingReadHandler

import widebase.stream.codec. {

  LoginMessage,
  LoginFailedMessage,
  LoginGrantedMessage

}

/** A common trait to build login based sockets.
 *
 * @author myst3r10n
 */
trait LoginLike {

  protected var future: ChannelFuture

  /** reader. */
  protected var reader: BlockingReadHandler[Object]

  /** password. */
  var password = ""

  /** username. */
  var username = ""

  /** Login request.
    *
    * @param username to login, default `this.username`
    * @param password to login, default `this.password`
    * @param timeout of response
    *
    * @return itself
   */
  def login(implicit
    username: String = this.username,
    password: String = this.password,
    timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new LoginMessage(username, password)).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: LoginFailedMessage => throw new LoginFailedMessage
      case message: LoginGrantedMessage =>

    }

    this

  }
}

