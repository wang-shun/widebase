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

  /** Counts the number of elements in the hybrid buffer which satisfy a predicate..
   *
   * @param predicate used to test elements
   *
   * @return number of elements satisfying the predicate
   */
  def count(predicate: A => Boolean) = {

    var number = 0

    if(mappers != null)
      for(i <- 0 to mappedElements - 1)
        if(predicate(get(i)))
          number += 1

    number + buffer.count(predicate)

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

  /** Tests whether a predicate holds for some of the elements of this hybrid buffer.
   *
   * @param predicate used to test elements
   *
   * @return true if exists, else false
   */
  def exists(predicate: A => Boolean): Boolean = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1) {

        val value = get(i)

        if(predicate(value))
          return true

      }

    buffer.exists(predicate)

  }

  /** Filters all elements of this hybrid buffer which satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered column
   */
  def filter(predicate: A => Boolean): ArrayBuffer[A] = {

    val column = ArrayBuffer[A]()

    if(mappers != null)
      for(i <- 0 to mappedElements - 1) {

        val value = get(i)

        if(predicate(value))
          column += value

      }

    (column ++ buffer.filter(predicate)).result

  }

  /** Filters all elements of this hybrid buffer which do not satisfy a predicate.
   *
   * @param predicate used to test elements.
   *
   * @return filtered column
   */
  def filterNot(predicate: A => Boolean) = filter(!predicate(_))

  /** Finds the first element of the hybrid buffer satisfying a predicate, if any.
   *
   * @param predicate used to test elements
   *
   * @return option of value
   */
  def find(predicate: A => Boolean): Option[A] = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1) {

        val value = get(i)

        if(predicate(value))
          return Some(value)

      }

    buffer.find(predicate)

  }

  /** Applies a function to all elements of this hybrid buffer.
   *
   * @param function apply to all emements
   */
  def foreach[U](function: A => U) = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1)
        function(get(i))

    buffer.foreach(function)

  }

  /** Tests whether a predicate holds for all elements of this hybrid buffer.
   *
   * @param predicate used to test elements
   *
   * @return `true` if predicate holds for all elements, else `false`
   */
  def forall[U](predicate: A => Boolean): Boolean = {

    if(mappers != null)
      for(i <- 0 to mappedElements - 1) {

        val value = get(i)

        if(!predicate(value))
          return false

      }

    buffer.forall(predicate)

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
  def indexOf[B >: A](element: B): Int = indexOf(element, 0)

  /** Find index of element within hybrid buffer.
   *
   * @param element to find
   * @param from start index
   *
   * @return >= 0 if found, else -1
   */
  def indexOf[B >: A](element: B, from: Int): Int = {

    if(mappers != null && from < mappedElements)
      for(i <- from to mappedElements - 1)
        if(element == get(i))
          return i

    if(mappers == null)
      buffer.indexOf(element, from)
    else
      buffer.indexOf(element, from - mappedElements + 1)

  }

  /** Finds index of first element satisfying some predicate.
   *
   * @param predicate used to test elements
   *
   * @return >= 0 if found, else -1
   */
  def indexWhere(predicate: A => Boolean): Int = indexWhere(predicate, 0)

  /** Finds index of first element satisfying some predicate.
   *
   * @param predicate used to test elements
   * @param from start index
   *
   * @return >= 0 if found, else -1
   */
  def indexWhere(predicate: A => Boolean, from: Int): Int = {

    if(mappers != null && from < mappedElements)
      for(i <- from to mappedElements - 1) {

        val value = get(i)

        if(predicate(value))
          return i

      }

    val index =
      if(mappers == null)
        buffer.indexWhere(predicate, from)
      else
        buffer.indexWhere(predicate, from - mappedElements - 1)

    if(index == -1)
      return -1

    if(mappers == null)
      index
    else
      mappedElements - 1 + index

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

  /** Tests whether column is empty. */
  def isEmpty = length == 0

  /** Last element within hybrid buffer. */
  def last =
    if(mappers == null || mappedElements == 0)
      buffer.last
    else
      get(mappedElements - 1)

  /** Find index of element within hybrid buffer.
   *
   * @param element to find
   *
   * @return >= 0 if found, else -1
   */
  def lastIndexOf[B >: A](element: B): Int = lastIndexOf(element, 0)

  /** Find index of element within hybrid buffer.
   *
   * @param element to find
   * @param from start index
   *
   * @return >= 0 if found, else -1
   */
  def lastIndexOf[B >: A](element: B, from: Int): Int = {

    val index = buffer.lastIndexOf(element, from)

    if(index != -1)
      if(mappers == null)
        return index
      else
        return mappedElements - 1 + index

    if(mappers != null) {

      var i = mappedElements - 1

      while(i >= 0) {

        if(element == get(i))
          return i

        i -= 1

      }
    }

    -1

  }

  /** Finds index of last element satisfying some predicate.
   *
   * @param predicate used to test elements
   *
   * @return >= 0 if found, else -1
   */
  def lastIndexWhere(predicate: A => Boolean): Int =
    lastIndexWhere(predicate, 0)

  /** Finds index of last element satisfying some predicate.
   *
   * @param predicate used to test elements
   * @param from start index
   *
   * @return >= 0 if found, else -1
   */
  def lastIndexWhere(predicate: A => Boolean, from: Int): Int = {

    val index = buffer.lastIndexWhere(predicate, from)

    if(mappers == null)
      return index
    else
      return mappedElements - 1 + index

    if(mappers != null && from < mappedElements) {

      var i = mappedElements - 1

      while(i >= 0) {

        if(predicate(get(i)))
          return i

        i -= 1

      }

    }

    -1

  }

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

