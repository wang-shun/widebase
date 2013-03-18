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

  /** Decrement lock counter. */
  def decrement = synchronized {

    if(counter == 0)
      throw new RuntimeException("counter < 0")

    counter -= 1

    if(counter == 0)
      notify

  }

  /** Increment lock counter. */
  def increment = synchronized {

    if(counter == Int.MaxValue)
      throw new RuntimeException("counter > Int.MaxValue")

    counter += 1

  }

  /** Wait until conter is zero. */
  def waitOfUnlock = synchronized {

    if(counter > 0)
      wait

  }
}

