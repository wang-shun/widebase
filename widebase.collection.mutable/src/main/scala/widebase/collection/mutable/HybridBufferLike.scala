package widebase.collection.mutable

import scala.collection.TraversableOnce
import scala.collection.mutable.ArrayBuffer

import widebase.io.file.FileVariantMapper

/** Mixes [[scala.collection.mutable.ArrayBuffer]] and [[widebase.io.file.FileVariantMapper]].
 *
 * @author myst3r10n
 */
trait HybridBufferLike[A] {

  /** Mapper of file. */
  protected val mappers: ArrayBuffer[FileVariantMapper]

  /** Elements of mapper. */
  protected val mappedElements: Int

  /** Size of type. */
  protected val sizeOf: Int

  /** Reads from [[widebase.io.file.FileVariantMapper]].
   *
   * @param region of mapped file
   */
  protected def read(region: Int): A

  /** Writes into [[widebase.io.file.FileVariantMapper]].
   *
   * @param region of mapped file
   " @param element to write
   */
  protected def write(region: Int, element: A)

  /** Volatile buffer. */
  protected val buffer = ArrayBuffer[A]()

  /** Append element into hybrid buffer.
   *
   * @param element to append
   */
  def +=(element: A) = {

    buffer += element

    this

  }

  /** Append elements of other hybrid buffer into this hybrid buffer.
   *
   * @param other hybrid buffer to append
   */
  def ++=(other: HybridBufferLike[A]) = {

    other.foreach(element => buffer += element)

    this

  }

  /** Append elements of traversable object into this hybrid buffer.
   *
   * @param other traversable object to append
   */
  def ++=(xs: TraversableOnce[A]) = {

    buffer ++= xs

    this

  }

  /** Removes element from hybrid buffer.
   *
   * @param element to remove
   */
  def -=(element: A) = {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    buffer -= element

    this

  }

  /** Removes elements of other hybrid buffer from this hybrid buffer.
   *
   * @param other hybrid buffer to remove
   */
  def --=(other: HybridBufferLike[A]) = {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    other.foreach(element => buffer -= element)

    this

  }

  /** Apply element within hybrid buffer.
   *
   * @param index to apply
   */
  def apply(index: Int) =
    if(mappers == null || index > mappedElements)
      buffer(index)
    else
      get(index)

  /** Clear hybrid buffer. */
  def clear {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    buffer.clear

  }

  /** Checks whether element exists within hybrid buffer.
   *
   * @param element to check
   *
   * @return true if exists, else false
   */
  def contains(element: Any): Boolean = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1)
        if(element == get(i))
          return true

    buffer.contains(element)

  }

  /** Filters all elements of this hybrid buffer which satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered column
   */
  def filter(predicate: A => Boolean): ArrayBuffer[A] = {

    val column = ArrayBuffer[A]()

    for(value <- this)
      if(predicate(value))
        column += value

    column.result

  }

  /** Filters all elements of this hybrid buffer which do not satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered column
   */
  def filterNot(predicate: A => Boolean) = filter(!predicate(_))

  /** Self-explanatory
   *
   * @param f self-explanatory
   */
  def foreach[U](f: A => U) = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1)
        f(get(i))

    buffer.foreach(f)

  }

  /** First element within hybrid buffer. */
  def head =
    if(mappers == null || mappedElements == 0)
      buffer.head
    else
      get(0)

  /** Find index of element within hybrid buffer.
   *
   * @param element to find
   *
   * @return >= 0 if found, else -1
   */
  def indexOf[B >: A](element: B): Int = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1)
        if(element == get(i))
          return i

    buffer.indexOf(element)

  }

  /** Inserts element into this hybrid buffer.
   *
   * @param n position
   * @param element to insert
   */
  def insert(n: Int, element: A) {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    buffer.insert(n, element)

  }

  /** Inserts elements of other hybrid buffer into this hybrid buffer.
   *
   * @param n position
   * @param other hybrid buffer to insert
   */
  def insertAll(n: Int, other: HybridBufferLike[A]) {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    other.foreach(element => buffer.insert(n, element))

  }

  /** Last element within hybrid buffer. */
  def last =
    if(mappers == null || mappedElements == 0)
      buffer.last
    else
      get(mappedElements - 1)

  /** Removes the element at a given index position.
   *
   * @param n position
   *
   * @return element that was formerly at position `n`
   */
  def remove(n: Int) = {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    buffer.remove(n)

  }

  /** Removes the element at a given index position.
   *
   * @param n position
   * @param count to remove
   */
  def remove(n: Int, count: Int) {

    if(mappers != null)
      throw new UnsupportedOperationException("Mapped buffer")

    buffer.remove(n, count)

  }

  /** Updates element within hybrid buffer.
   *
   * @param index of element
   * @param element ot update
   */
  def update(index: Int, element: A) {

    if(mappers != null && index < mappedElements)
      set(index, element)
    else
      buffer(index - mappedElements) = element

  }

  /** Number of elements within hybrid buffer. */
  def length = mappedElements + buffer.length

  protected def get(index: Int) = {

    val position = index.toLong * sizeOf

    val region = (position / Int.MaxValue).toInt

    if(region == 0)
      mappers(region).position = position.toInt
    else
      mappers(region).position = (position / region).toInt

    read(region)

  }

  protected def set(index: Int, element: A) = {

    val position = index.toLong * sizeOf

    val region = (position / Int.MaxValue).toInt

    if(region == 0)
      mappers(region).position = position.toInt
    else
      mappers(region).position = (position / region).toInt

    write(region, element)

  }
}

