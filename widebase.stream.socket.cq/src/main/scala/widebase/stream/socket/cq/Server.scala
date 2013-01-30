package widebase.stream.socket.cq

import org.jboss.netty.channel.group. {

  ChannelGroupFuture,
  ChannelGroupFutureListener

}

import scala.collection.mutable.LinkedHashMap
import scala.concurrent.Lock

import widebase.stream.codec. { LengthDecoder, LengthEncoder }
import widebase.stream.codec.cq. { RequestDecoder, ResponseEncoder }
import widebase.stream.handler. { AuthHandler, AuthMap, ControlHandler }

import widebase.stream.handler.cq. {

  BlockLock,
  CountLock,
  QueryHandler,
  ServerHandler,
  TableMap

}

import widebase.stream.socket.ServerLike

/** A server.
 *
 * @param a authorization map
 *
 * @author myst3r10n
 */
class Server(a: AuthMap) extends ServerLike {

  auths = a

  /** Cache. */
  var cache: TableMap = _

  /** Lock objects against read. */
  var readLock: CountLock = _

  /** Lock objects against write. */
  var writeLock: BlockLock = _

  pipeline += "lengthDecoder" -> new LengthDecoder
  pipeline += "requestDecoder" -> new RequestDecoder
  pipeline += "lengthEncoder" -> new LengthEncoder
  pipeline += "responseEncoder" -> new ResponseEncoder

  override def await = {

    super.await
    this

  }

  override def bind(implicit port: Int = this.port) = {

    if(cache == null)
      cache = new TableMap

    readLock = new CountLock
    writeLock = new BlockLock

    pipeline += "authHandler" -> new AuthHandler(allChannels, auths)
    pipeline += "controlHandler" -> new ControlHandler(allChannels)
    pipeline += "serverHandler" -> new ServerHandler(cache, readLock, writeLock)
    pipeline += "queryHandler" -> new QueryHandler(cache, readLock, writeLock)

    super.bind(port)

    this

  }

  override def close = {

    super.close

    if(pipeline.contains("queryHandler"))
      pipeline -= "queryHandler"

    if(pipeline.contains("serverHandler"))
      pipeline -= "serverHandler"

    if(pipeline.contains("controlHandler"))
      pipeline -= "controlHandler"

    if(pipeline.contains("authHandler"))
      pipeline -= "authHandler"

    if(writeLock != null)
      writeLock.release

    this

  }

  override def filter(filter: String) = {

    super.filter(filter)
    this

  }

  override def load(filename: String) = {

    super.load(filename)
    this

  }
}

