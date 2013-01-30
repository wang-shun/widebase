package widebase.stream.socket

import java.io.IOException
import java.util.concurrent.TimeUnit

import org.jboss.netty.channel.ChannelFuture
import org.jboss.netty.handler.queue.BlockingReadHandler

import widebase.stream.codec. {

  DoneMessage,
  ForbiddenMessage,
  LoginRequiredMessage,
  RemoteShutdownMessage,
  UnauthorizedMessage

}

/** A common trait to build login based sockets.
 *
 * @author myst3r10n
 */
trait RemoteShutdownLike {

  protected var future: ChannelFuture

  /** reader. */
  protected var reader: BlockingReadHandler[Object]

  /** Shutdown server remotely.
   *
   * @param timeout of response
   *
   * @return itself
   */
  def remoteShutdown(implicit timeout: Int = 60) = {

    val future = this.future.getChannel
      .write(new RemoteShutdownMessage).awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    val message = reader.read(timeout, TimeUnit.SECONDS)

    if(message == null)
      throw new IOException(new NullPointerException)

    message match {

      case message: DoneMessage =>
      case message: ForbiddenMessage => throw new ForbiddenMessage
      case message: LoginRequiredMessage => throw new LoginRequiredMessage
      case message: UnauthorizedMessage => throw new UnauthorizedMessage

    }

    this

  }
}

