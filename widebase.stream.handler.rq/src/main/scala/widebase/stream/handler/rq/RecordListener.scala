package widebase.stream.handler.rq

/** A trait to build record listeners.
 *
 * @author myst3r10n
 */
trait RecordListener {

  /** React on subscriptions. */
  def react: PartialFunction[Any, Unit]

}

