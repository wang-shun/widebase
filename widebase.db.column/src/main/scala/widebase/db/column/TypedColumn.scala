package widebase.db.column

import scala.collection.Traversable

import vario.collection.mutable.HybridBufferLike
import vario.data.Datatype.Datatype

/** Implements a typed column.
 *
 * @param t type of column
 * @param r records of mapper
 *
 * @author myst3r10n
 */
abstract class TypedColumn[A](t: Datatype) extends HybridBufferLike[A] {

  import vario.data

  /** Type of buffer. */
  val typeOf = t

  /** Records of mapper. */
  protected val records: Int

  /** Elements of mapper. */
  protected val mappedElements = records

  {

    if(mappers != null)
      mappers.foreach(mapper => mapper.mode = typeOf)

  }

  override def +=(value: A) = {

    super.+=(value)

    this

  }

  /** Append values of other column into this column.
   *
   * @param other column to append
   */
  def ++=(other: TypedColumn[A]) = {

    super.++=(other)

    this

  }

  override def ++=(xs: TraversableOnce[A]) = {

    super.++=(xs)

    this

  }

  override def -=(value: A) = {

    super.-=(value)

    this

  }

  /** Removes values of other column from this column.
   *
   * @param other column to remove
   */
  def --=(other: TypedColumn[A]) = {

    super.--=(other)

    this

  }
}

