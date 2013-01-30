package widebase.stream.handler.cq

import scala.concurrent.Lock

/** Block locks.
 *
 * @author myst3r10n
 */
class BlockLock extends Lock {

  def waitOfUnlock = synchronized {

    if(!available)
      wait

  }
}

