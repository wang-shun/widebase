package widebase.stream.socket.rq.test

import java.util.concurrent.LinkedBlockingQueue

import net.liftweb.common. { Loggable, Logger }

import scala.actors. { Actor, TIMEOUT }

/* A concurrent test.
 *
 * @author myst3r10n
 */
object Concurrent extends Logger with Loggable {

  import widebase.stream.socket.rq

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    var fill = 100
    var amount = 100

    actorBox(fill, amount)
//    threadBox(fill, amount)

  }

  def actorBox(fill: Int, amount: Int) {

    var boxes = Array.fill(fill)(new ActorBox(amount))

    boxes.foreach(_.start)

    for(i <- 1 to amount)
      boxes.foreach(_ ! "Hello World!")

  }

  def threadBox(fill: Int, amount: Int) {

    var boxes = Array.fill(fill)(new ThreadBox(amount))

    boxes.foreach(_.start)

    for(i <- 1 to amount)
      boxes.foreach(_.add("Hello World!"))

  }

  protected object Abort

  protected class ActorBox(amount: Int) extends Actor {

    protected var count = 0
    protected var started = 0L

    def act {

      started = System.currentTimeMillis

      loop {

        reactWithin(0) {

          case Abort => action(Abort)
          case TIMEOUT => react { case msg => action(msg) }

        }
      }
    }

    protected def action(msg: Any) {

      msg match {

        case Abort =>
          println("Actor based box received " + count + " messages in " +
            diff(started, System.currentTimeMillis))

          exit

        case msg =>
          count += 1

          if(count == amount)
            this ! Abort

      }
    }
  }

  protected class ThreadBox(amount: Int) extends Thread {

    protected var count = 0
    protected val queue = new LinkedBlockingQueue[Object]
    protected var started = 0L

    def add(obj: Object) {

      queue.add(obj)

    }

    override def run {

      var continue = true

      started = System.currentTimeMillis

      while(continue) {

        queue.take match {

          case Abort =>
            println("Thread based box received " + count + " messages in " +
              diff(started, System.currentTimeMillis))

            continue = false

          case msg =>
            count += 1

            if(count == amount) {

              println("Thread based box received " + count + " messages in " +
                diff(started, System.currentTimeMillis))

              continue = false

            }
        }
      }
    }
  }
}

