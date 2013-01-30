package widebase.stream.handler.cq

/** Count locks.
 *
 * @author myst3r10n
 */
class CountLock {

  /** Amount locks. */
  protected var counter = 0

  /** Is true if no locks. */
  def available = counter == 0

  /** Increment lock counter. */
  def ++ = synchronized {

    if(counter == Int.MaxValue)
      throw new RuntimeException("counter > Int.MaxValue")

    counter += 1

  }

  /** Decrement lock counter. */
  def -- = synchronized {

    if(counter == 0)
      throw new RuntimeException("counter < 0")

    counter -= 1

    if(counter == 0)
      notify

  }

  /** Wait until conter is zero. */
  def waitOfUnlock = synchronized {

    if(counter > 0)
      wait

  }
}

