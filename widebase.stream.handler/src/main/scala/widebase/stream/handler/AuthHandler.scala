package widebase.stream.handler

import java.io. { PrintWriter, StringWriter }
import javax.security.auth.login. { FailedLoginException, LoginContext }

import net.liftweb.common.Logger

import org.apache.activemq.jaas.JassCredentialCallbackHandler

import org.jboss.netty.channel. {

  Channel,
  ChannelFutureListener,
  ChannelHandlerContext,
  ChannelLocal,
  Channels,
  ChannelStateEvent,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.channel.ChannelHandler.Sharable
import org.jboss.netty.channel.group.DefaultChannelGroup

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

import widebase.stream.codec. {

  BadMessage,
  ForbiddenMessage,
  LoginFailedMessage,
  LoginGrantedMessage,
  LoginRequiredMessage,
  LoginMessage,
  UnauthorizedMessage

}

/** Handles authentication and authorization.
 *
 * @param allChannels self-explanatory
 * @param auths permission
 *
 * @author myst3r10n
 */
class AuthHandler(
  allChannels: DefaultChannelGroup,
  auths: AuthMap)
  extends SimpleChannelUpstreamHandler
  with Logger {

  import scala.collection.JavaConversions.asScalaSet

  override def channelClosed(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    Channels.fireChannelClosed(ctx)

  }

  override def channelOpen(ctx: ChannelHandlerContext, evt: ChannelStateEvent) {

    allChannels.add(ctx.getChannel)

    if(auths == null) {

      AuthHandler.loggedIn.set(evt.getChannel, true)
      AuthHandler.username.set(evt.getChannel, "anonymous")

    } else
      AuthHandler.loggedIn.set(evt.getChannel, false)

    Channels.fireChannelOpen(ctx)

  }

  override def exceptionCaught(ctx: ChannelHandlerContext, evt: ExceptionEvent) {

    evt.getCause match {

      case _: ReadTimeoutException =>
        error("Read timeout: " + ctx.getChannel.getRemoteAddress)
        evt.getChannel.close

      case _: WriteTimeoutException =>
        error("Write timeout: " + ctx.getChannel.getRemoteAddress)
        evt.getChannel.close

      case _ =>
        val message = new StringWriter
        val printer = new PrintWriter(message)
        evt.getCause.printStackTrace(printer)
        error(message.toString)
        evt.getChannel.close

    }
  }

  override def messageReceived(ctx: ChannelHandlerContext, evt: MessageEvent) {

    evt.getMessage match {

      case login: LoginMessage =>
        if(auths == null) {

          AuthHandler.username.set(evt.getChannel, login.username)
          evt.getChannel.write(new LoginGrantedMessage)

          info("Login granted: " + AuthHandler.username.get(evt.getChannel) +
            " @ " + evt.getRemoteAddress)

          return

        }

        val context = new LoginContext(
          auths.jaas,
          new JassCredentialCallbackHandler(login.username, login.password))

        try {

          context.login
          AuthHandler.loggedIn.set(evt.getChannel, true)
          AuthHandler.loginCtx.set(evt.getChannel, context)
          AuthHandler.username.set(evt.getChannel, login.username)
          evt.getChannel.write(new LoginGrantedMessage)

          info("Login granted: " + AuthHandler.username.get(evt.getChannel) +
            " @ " + evt.getRemoteAddress)

        } catch {

          case e: FailedLoginException =>
            warn("Login failed: " + login.username +
              " @ " + evt.getRemoteAddress)

            evt.getChannel.write(new LoginFailedMessage)
              .addListener(ChannelFutureListener.CLOSE)

          case e: Throwable =>
            warn(e)

            evt.getChannel.write(new LoginFailedMessage)
              .addListener(ChannelFutureListener.CLOSE)

        }

      case message =>
        if(AuthHandler.loggedIn.get(evt.getChannel))
          Channels.fireMessageReceived(ctx, message)
        else {

          warn("Login required: " + evt.getRemoteAddress)
          evt.getChannel.write(new LoginRequiredMessage)
            .addListener(ChannelFutureListener.CLOSE)

        }
    }
  }

  /** Checks whether client has authorization.
   *
   * @param channel self-explanatory
   * @param message name where client wanted authorization
   */
  def hasAuthorization(channel: Channel, message: String): Boolean = {

    if(auths == null)
      return true

    if(!auths.contains(message)) {

      warn("Forbidden message: " + message + " by " +
        AuthHandler.username.get(channel) + " @ " + channel.getRemoteAddress)

      channel.write(new ForbiddenMessage)
      return false

    } else
      asScalaSet(AuthHandler.loginCtx.get(channel)
        .getSubject.getPrincipals).foreach(principal =>
          if(auths(message).contains(principal.getName))
            return true)

    warn("Unauthorized message: " + message + " by " +
      AuthHandler.username.get(channel) + " @ " + channel.getRemoteAddress)

    channel.write(new UnauthorizedMessage)
    false

  }
}

/** Companion.
 *
 * @author myst3r10n
 */
object AuthHandler {

  /** Login status. */
  val loggedIn = new ChannelLocal[Boolean]

  /** Login context. */
  val loginCtx = new ChannelLocal[LoginContext]

  /** Username. */
  val username = new ChannelLocal[String]

  /** Checks whether client has authorization.
   *
   * @param channel self-explanatory
   * @param message name where client wanted authorization
   */
  def hasAuthorization(channel: Channel, message: String) =
    channel.getPipeline.get(classOf[AuthHandler]).hasAuthorization(
      channel,
      message)

}

