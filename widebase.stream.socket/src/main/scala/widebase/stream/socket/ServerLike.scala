package widebase.stream.socket

import java.net.InetSocketAddress
import java.util.LinkedList
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ServerBootstrap

import org.jboss.netty.channel. {

  Channel,
  ChannelFuture,
  ChannelPipelineFactory,
  Channels,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.channel.group. {

  ChannelGroupFuture,
  ChannelGroupFutureListener,
  DefaultChannelGroup,
  DefaultChannelGroupFuture

}

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory

import org.jboss.netty.handler.codec.compression. {

  ZlibDecoder,
  ZlibEncoder,
  ZlibWrapper

}

import widebase.io.filter.StreamFilter

import widebase.stream.codec. {

  LengthDecoder,
  LengthEncoder,
  RequestDecoder,
  ResponseEncoder

}

import widebase.stream.handler. { AuthHandler, AuthMap, ControlHandler }

/** A common trait to build socket based server.
 *
 * @author myst3r10n
 */
trait ServerLike extends SocketLike {

  import widebase.data.sizeOf

  protected var allChannels = new DefaultChannelGroup("server")
  protected var bootstrap: ServerBootstrap = _

  /** authorization map. */
  var auths: AuthMap = null

  /** Wait until socket closed.
   *
   * @return itself
   */
  def await = {

    var i = allChannels.iterator

    while(i.hasNext) {

      i.next.getCloseFuture.awaitUninterruptibly
      i = allChannels.iterator

    }

    this

  }

  /** Bind a socket to address and port.
   *
   * @param port of server, default `this.port`
   *
   * @return itself
   */
  def bind(implicit port: Int = this.port) = {

    val factory = new NioServerSocketChannelFactory(
      Executors.newCachedThreadPool,
      Executors.newCachedThreadPool)

    bootstrap = new ServerBootstrap(factory)

    bootstrap.setPipelineFactory(new ChannelPipelineFactory {

      @throws(classOf[Exception])
      def getPipeline = {

        val _pipeline = Channels.pipeline

        inFilter match {

          case StreamFilter.Gzip =>
            _pipeline.addFirst("inflater", new ZlibDecoder(ZlibWrapper.GZIP))

          case StreamFilter.Zlib =>
            _pipeline.addFirst("inflater", new ZlibDecoder(ZlibWrapper.ZLIB))

          case _ =>

        }

        outFilter match {

          case StreamFilter.Gzip =>
            _pipeline.addFirst("deflater", new ZlibEncoder(ZlibWrapper.GZIP, level))

          case StreamFilter.Zlib =>
            _pipeline.addFirst("deflater", new ZlibEncoder(ZlibWrapper.ZLIB, level))

          case _ =>

        }

        if(pipeline.size > 0)
          pipeline.foreach { case (name, handler) => _pipeline.addLast(name, handler) }

        _pipeline

      }
    } )

    allChannels.add(bootstrap.bind(new InetSocketAddress(port)))

    this

  }

  /** Close the socket and associated resources.
   *
   * @return itself
   */
  def close = {

    if(allChannels != null)
      allChannels.close.awaitUninterruptibly

    if(bootstrap != null)
      bootstrap.releaseExternalResources

    this

  }

  /** Load authorization map.
   *
   * @param filename of authorization map
   *
   * @return itself
   */
  def load(filename: String) = {

    auths = AuthMap.load(filename)

    this

  }
}

