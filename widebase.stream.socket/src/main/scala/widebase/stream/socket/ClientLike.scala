package widebase.stream.socket

import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ClientBootstrap

import org.jboss.netty.channel. {

  ChannelFuture,
  ChannelPipelineFactory,
  Channels

}

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory

import org.jboss.netty.handler.codec.compression. {

  ZlibDecoder,
  ZlibEncoder,
  ZlibWrapper

}

import org.jboss.netty.handler.queue.BlockingReadHandler

import widebase.io.filter. { CompressionLevel, StreamFilter }

/** A common trait to build socket based clients.
 *
 * @author myst3r10n
 */
trait ClientLike extends SocketLike {

  import widebase.io.filter.StreamFilter.StreamFilter

  protected var future: ChannelFuture = _
  protected var bootstrap: ClientBootstrap = _

  /** Queue reader. */
  protected var reader: BlockingReadHandler[Object] = _

  /** Connect timeout in seconds. */
  var connectTimeout = 10

  /** host of server. */
  var host = "localhost"

  /** Wait until socket closed.
   *
   * @return itself
   */
  def await = {

    future.getChannel.getCloseFuture.awaitUninterruptibly
    this

  }

  /** Close the socket and associated resources.
   *
   * @return itself
   */
  def close = {

    if(future != null) {

      future.getChannel.close
      future.getChannel.getCloseFuture.awaitUninterruptibly

    }

    if(bootstrap != null)
      bootstrap.releaseExternalResources

    this

  }

  /** Open a socket connection.
   *
   * @param host of server, default `this.host`
   * @param port of server, default `this.port`
   *
   * @return itself
   */
  def open(implicit host: String = this.host, port: Int = this.port) = {

    val factory = new NioClientSocketChannelFactory(
      Executors.newCachedThreadPool,
      Executors.newCachedThreadPool)

    bootstrap = new ClientBootstrap(factory);
    reader = new BlockingReadHandler[Object]

    bootstrap.setOption("connectTimeoutMillis", connectTimeout * 1000)
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

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

        _pipeline.addLast("reader", reader)

        _pipeline

      }
    } )

    future = bootstrap.connect(new InetSocketAddress(host, port))
    future.awaitUninterruptibly

    if(!future.isSuccess)
      throw new IOException(future.getCause)

    this

  }


}

