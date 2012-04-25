package widebase.db.column

import java.nio.channels.FileChannel

import vario.data.Datatype.Datatype

/** Implements a variable column.
 *
 * @param t type of column
 * @param r records of mapper
 *
 * @author myst3r10n
 */
abstract class VariableColumn[A](t: Datatype) extends TypedColumn[A](t) {

  /** Channel of companion. */
  protected val channel: FileChannel

  /** Indexing logic.
   *
   * @param idx the index of value
   *
   * @return the element of its index
   */
  protected def get(idx: Int): A

  override def apply(idx: Int) =
    if(mapper == null || idx > records)
      buffer(idx)
    else
      get(idx)

  override def contains(elem: Any): Boolean = {

    if(mapper != null) {

      for(i <- 0 to records - 1)
        if(elem == get(i))
          return true

    }

    buffer.contains(elem)

  }

  override def foreach[U](f: A => U) = {

    if(mapper != null)
      for(i <- 0 to records - 1)
        f(get(i))

    buffer.foreach(f)

  }

  override def head =
    if(mapper == null || records == 0)
      buffer.head
    else
      get(0)

  override def indexOf[B >: A](elem: B): Int = {

    if(mapper != null)
      for(i <- 0 to records - 1)
        if(elem == get(i))
          return i

    buffer.indexOf(elem)

  }

  override def last =
    if(mapper == null || records == 0)
      buffer.last
    else
      get(records - 1)

  override def update(idx: Int, value: A) {

    if(mapper != null && idx < records)
      throw new UnsupportedOperationException(typeOf.toString)
    else
      buffer(idx - records) = value

  }
}

