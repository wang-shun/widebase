package widebase.db.column

import vario.collection.mutable.HybridBufferLike

/** A typed hybrid buffer.
 *
 * @author myst3r10n
 */
trait TypedHybridBuffer[A] extends HybridBufferLike[A] {

  import vario.data.Datatype.Datatype

  /** Type of buffer. */
  val typeOf: Datatype

}

