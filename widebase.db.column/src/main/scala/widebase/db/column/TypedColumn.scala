package widebase.db.column

import scala.collection.Traversable

import scala.collection.generic. {

  CanBuildFrom,
  GenericTraversableTemplate,
  TraversableFactory

}

import scala.collection.mutable.LazyBuilder

import vario.collection.mutable.HybridBufferLike
import vario.data.Datatype.Datatype

/** Implements a typed column.
 *
 * @param t type of column
 * @param r records of mapper
 *
 * @author myst3r10n
 */
abstract class TypedColumn[A](t: Datatype) extends HybridBufferLike[A]
/*  with Traversable[A]
  with GenericTraversableTemplate[A, TypedColumn]*/ {

  import vario.data

  /** Type of buffer. */
  val typeOf = t

//  override def companion = TypedColumn

  /** Records of mapper. */
  protected val records: Int

  /** Capacity of mapper. */
  protected val capacity = records

  {

    if(mapper != null)
      mapper.mode = typeOf

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
/*
object TypedColumn extends TraversableFactory[TypedColumn] {

  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, TypedColumn[A]] =
    new GenericCanBuildFrom[A]

  def newBuilder[A] = new LazyBuilder[A,TypedColumn[A]] {

    def result = {

      val data = parts.foldLeft(List[A]()) { (l,n) => l ++ n }

      new TypedColumn(data:_*)

    }
  }
}
*/

