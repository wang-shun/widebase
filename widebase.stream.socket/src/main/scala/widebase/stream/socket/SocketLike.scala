package widebase.stream.socket

import org.jboss.netty.channel.ChannelHandler

import scala.collection.mutable.LinkedHashMap

import widebase.io.filter. { CompressionLevel, StreamFilter }

/** A common trait to build client/server sockets.
 *
 * @author myst3r10n
 */
trait SocketLike {

  import widebase.io.filter.StreamFilter.StreamFilter

  /** Pipeline. */
  val pipeline = new LinkedHashMap[String, ChannelHandler]

  /** Inbound filter. */
  var inFilter = StreamFilter.None

  /** Compression level. */
  var level = CompressionLevel.Default

  /** Outbound filter. */
  var outFilter = StreamFilter.None

  /** port of server. */
  var port = 50000

  /** Setup I/O filter.
   *
   * @param filter to setup
   *
   */
  def filter(filter: String) = {

    inFilter =
      if(filter.contains("rg"))
        StreamFilter.Gzip
      else if(filter.contains("rz"))
        StreamFilter.Zlib
      else
        StreamFilter.None

    outFilter =
      if(filter.contains("wg"))
        StreamFilter.Gzip
      else if(filter.contains("wz"))
        StreamFilter.Zlib
      else
        StreamFilter.None

    level =
      if(!filter.contains("l"))
        CompressionLevel.Default
      else if(filter.contains("l0"))
        0
      else if(filter.contains("l1"))
        1
      else if(filter.contains("l2"))
        2
      else if(filter.contains("l3"))
        3
      else if(filter.contains("l4"))
        4
      else if(filter.contains("l5"))
        5
      else if(filter.contains("l6"))
        6
      else if(filter.contains("l7"))
        7
      else if(filter.contains("l8"))
        8
      else if(filter.contains("l9"))
        9
      else
        CompressionLevel.Default

    this

  }
}

