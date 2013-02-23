package widebase.testkit

import java.io.IOException

import net.liftweb.common. { Loggable, Logger }

import org.jboss.netty.channel.ChannelFuture

import widebase.stream.codec. {

  LengthDecoder,
  LengthEncoder,
  RequestDecoder,
  RequestEncoder,
  ResponseDecoder,
  ResponseEncoder

}

import widebase.stream.handler. { AuthHandler, ControlHandler }

import widebase.stream.socket. {

  ClientLike,
  LoginLike,
  RemoteShutdownLike,
  ServerLike

}

/* A short client/server test.
 *
 * @author myst3r10n
 */
object Socket extends Logger with Loggable {

  class Client extends ClientLike with LoginLike with RemoteShutdownLike {

    pipeline += "lengthDecoder" -> new LengthDecoder
    pipeline += "responseDecoder" -> new ResponseDecoder
    pipeline += "lengthEncoder" -> new LengthEncoder
    pipeline += "requestEncoder" -> new RequestEncoder

  }

  class Server extends ServerLike {

    pipeline += "lengthDecoder" -> new LengthDecoder
    pipeline += "requestDecoder" -> new RequestDecoder
    pipeline += "lengthEncoder" -> new LengthEncoder
    pipeline += "responseEncoder" -> new ResponseEncoder
    pipeline += "authHandler" -> new AuthHandler(allChannels, auths)
    pipeline += "controlHandler" -> new ControlHandler(allChannels)

  }

  def main(args: Array[String]) {

    val client = new Client
    val server = new Server

    try {

      var future: ChannelFuture = null

      server.bind

      client.open
      client.close
      client.open
      client.remoteShutdown

      server.await

    } finally {

      client.close
      server.close

    }
  }
}

