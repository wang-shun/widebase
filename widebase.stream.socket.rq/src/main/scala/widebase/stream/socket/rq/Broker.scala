package widebase.stream.socket.rq

import org.jboss.netty.channel.group. {

  ChannelGroupFuture,
  ChannelGroupFutureListener

}

import scala.collection.mutable. { ArrayBuffer, LinkedHashMap, Map }
import scala.concurrent.Lock

import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter
import widebase.stream.codec. { LengthDecoder, LengthEncoder }
import widebase.stream.codec.rq. { BrokerEncoder, RequestDecoder }
import widebase.stream.handler. { AuthHandler, AuthMap, ControlHandler }

import widebase.stream.handler.rq. {

  ConsumerWriter,
  PersistenceWriter,
  PublishingHandler,
  SubscribingHandler

}

import widebase.stream.socket.ServerLike

/** A broker.
 *
 * @param p path of synchronization database
 * @param a authorization map
 * @param i publishing interval
 *
 * @author myst3r10n
 */
class Broker(p: String, a: AuthMap, i: Int) extends ServerLike {

  var path = p
  auths = a
  var interval = i
  port = 60000

  pipeline += "lengthDecoder" -> new LengthDecoder
  pipeline += "requestDecoder" -> new RequestDecoder
  pipeline += "lengthEncoder" -> new LengthEncoder
  pipeline += "brokerEncoder" -> new BrokerEncoder

  /** Lock objects. */
  protected var lock: Lock = _

  /** Holds persistence wrtiers. */
  protected var persistences: Map[String, PersistenceWriter] = _

  /** Holds subscription wrtiers. */
  protected var subscriptions: Map[String, ArrayBuffer[ConsumerWriter]] = _

  override def await = {

    super.await
    this

  }

  override def bind(implicit port: Int = this.port) = {

    val lock = new Lock
    persistences = Map[String, PersistenceWriter]()
    subscriptions = Map[String, ArrayBuffer[ConsumerWriter]]()

    pipeline += "authHandler" -> new AuthHandler(allChannels, auths)
    pipeline += "controlHandler" -> new ControlHandler(allChannels)

    pipeline += "publishing" -> new PublishingHandler(
      path,
      interval * 1000L,
      lock,
      persistences,
      subscriptions)

    pipeline += "subscribing" -> new SubscribingHandler(
      lock,
      persistences,
      subscriptions)

    super.bind(port)

    this

  }

  override def close = {

    super.close

    if(pipeline.contains("subscribing"))
      pipeline -= "subscribing"

    if(pipeline.contains("publishing"))
      pipeline -= "publishing"

    if(pipeline.contains("controlHandler"))
      pipeline -= "controlHandler"

    if(pipeline.contains("authHandler"))
      pipeline -= "authHandler"

    if(subscriptions != null)
      subscriptions.clear

    if(persistences != null)
      persistences.clear

    if(lock != null)
      lock.release

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

